import request from './request'

// 获取商品列表
export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

// 获取商品详情
export function getProductDetail(id) {
  return request({
    url: `/product/detail/${id}`,
    method: 'get'
  })
}

// 获取推荐商品
export function getRecommendProducts(limit = 8) {
  return request({
    url: '/product/recommend',
    method: 'get',
    params: { limit }
  })
}

// 获取新品上架
export function getNewProducts(limit = 8) {
  return request({
    url: '/product/new',
    method: 'get',
    params: { limit }
  })
}
