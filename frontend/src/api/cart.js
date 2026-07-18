import request from './request'

// 获取购物车列表
export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

// 添加购物车
export function addToCart(productId, quantity) {
  return request({
    url: '/cart/add',
    method: 'post',
    params: { productId, quantity }
  })
}

// 更新购物车数量
export function updateQuantity(cartId, quantity) {
  return request({
    url: `/cart/quantity`,
    method: 'put',
    params: { quantity }
  })
}

// 删除购物车商品
export function removeFromCart(cartId) {
  return request({
    url: `/cart/${cartId}`,
    method: 'delete'
  })
}

// 选中/取消选中
export function checkCart(cartId, checked) {
  return request({
    url: `/cart/checked`,
    method: 'put',
    params: { checked }
  })
}

// 全选/取消全选
export function checkAll(checked) {
  return request({
    url: '/cart/checked/all',
    method: 'put',
    params: { checked }
  })
}

// 清空购物车
export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'delete'
  })
}

// 获取购物车数量
export function getCartCount() {
  return request({
    url: '/cart/count',
    method: 'get'
  })
}
