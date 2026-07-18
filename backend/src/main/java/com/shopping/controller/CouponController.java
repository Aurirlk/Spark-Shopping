package com.shopping.controller;

import com.shopping.entity.Coupon;
import com.shopping.service.CouponService;
import com.shopping.utils.Result;
import com.shopping.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/available")
    public Result<List<Coupon>> getAvailable() {
        return Result.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/claim/{couponId}")
    public Result<String> claim(@PathVariable Long couponId) {
        boolean ok = couponService.claimCoupon(UserContext.getUserId(), couponId);
        return ok ? Result.success("领取成功") : Result.error("领取失败");
    }

    @GetMapping("/my")
    public Result<List<Coupon>> getMy() {
        return Result.success(couponService.getUserCoupons(UserContext.getUserId()));
    }
}
