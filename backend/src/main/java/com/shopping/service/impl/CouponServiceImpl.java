package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.entity.Coupon;
import com.shopping.entity.UserCoupon;
import com.shopping.mapper.CouponMapper;
import com.shopping.mapper.UserCouponMapper;
import com.shopping.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> getAvailableCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return lambdaQuery()
                .eq(Coupon::getStatus, 1)
                .gt(Coupon::getStock, 0)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now)
                .list();
    }

    @Override
    @Transactional
    public boolean claimCoupon(Long userId, Long couponId) {
        Coupon coupon = getById(couponId);
        if (coupon == null || coupon.getStock() <= 0 || coupon.getStatus() != 1) return false;
        // 检查是否已领取
        Long count = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId));
        if (count > 0) return false;

        coupon.setStock(coupon.getStock() - 1);
        couponMapper.updateById(coupon);

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId); uc.setCouponId(couponId);
        uc.setUsed(0); uc.setCreateTime(LocalDateTime.now());
        userCouponMapper.insert(uc);
        return true;
    }

    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
                new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getUserId, userId));
        if (userCoupons.isEmpty()) return List.of();
        List<Long> ids = userCoupons.stream().map(UserCoupon::getCouponId).collect(Collectors.toList());
        return couponMapper.selectBatchIds(ids);
    }
}
