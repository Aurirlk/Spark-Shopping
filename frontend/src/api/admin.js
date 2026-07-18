import request from './request'

// ==================== 商品管理 ====================

// 分页查询商品（管理端）
export function getAdminProductList(params) {
  return request({
    url: '/admin/product/list',
    method: 'get',
    params
  })
}

// 添加商品
export function saveProduct(data) {
  return request({
    url: '/admin/product/save',
    method: 'post',
    data
  })
}

// 更新商品
export function updateProduct(data) {
  return request({
    url: '/admin/product/update',
    method: 'put',
    data
  })
}

// 切换上下架
export function toggleProductStatus(id) {
  return request({
    url: `/admin/product/status/${id}`,
    method: 'put'
  })
}

// 删除商品
export function deleteProduct(id) {
  return request({
    url: `/admin/product/${id}`,
    method: 'delete'
  })
}

// ==================== 订单管理 ====================

// 分页查询订单（管理端）
export function getAdminOrderList(params) {
  return request({
    url: '/admin/order/list',
    method: 'get',
    params
  })
}

// 发货
export function shipOrder(orderId) {
  return request({
    url: `/admin/order/ship/${orderId}`,
    method: 'put'
  })
}

// 完成订单
export function completeOrder(orderId) {
  return request({
    url: `/admin/order/complete/${orderId}`,
    method: 'put'
  })
}

// 取消订单（管理端）
export function cancelOrderAdmin(orderId) {
  return request({
    url: `/admin/order/cancel/${orderId}`,
    method: 'put'
  })
}
