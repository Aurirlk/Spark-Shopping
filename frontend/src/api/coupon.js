import request from './request'

export function getAvailableCoupons() {
  return request({ url: '/coupon/available', method: 'get' })
}
export function claimCoupon(couponId) {
  return request({ url: `/coupon/claim/${couponId}`, method: 'post' })
}
export function getMyCoupons() {
  return request({ url: '/coupon/my', method: 'get' })
}
