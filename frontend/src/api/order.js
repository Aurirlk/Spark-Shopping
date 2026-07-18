import request from './request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return request({
    url: `/order/${orderId}`,
    method: 'get'
  })
}

// 获取用户订单列表
export function getUserOrders(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: `/order/cancel/${orderId}`,
    method: 'put'
  })
}

// 确认收货
export function confirmReceive(orderId) {
  return request({
    url: `/order/confirm/${orderId}`,
    method: 'put'
  })
}

// 删除订单
export function deleteOrder(orderId) {
  return request({
    url: `/order/${orderId}`,
    method: 'delete'
  })
}

// 创建支付
export function createPayment(data) {
  return request({
    url: '/payment/create',
    method: 'post',
    data
  })
}

// 查询支付状态
export function queryPaymentStatus(orderNo) {
  return request({
    url: `/payment/status/${orderNo}`,
    method: 'get'
  })
}

// 申请退款
export function refund(orderNo) {
  return request({
    url: `/payment/refund/${orderNo}`,
    method: 'post'
  })
}
