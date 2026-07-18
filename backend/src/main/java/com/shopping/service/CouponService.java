package com.shopping.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.entity.Coupon;
import java.util.List;

public interface CouponService extends IService<Coupon> {
    List<Coupon> getAvailableCoupons();
    boolean claimCoupon(Long userId, Long couponId);
    List<Coupon> getUserCoupons(Long userId);
}
