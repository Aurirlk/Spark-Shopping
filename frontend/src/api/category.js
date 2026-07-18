import request from './request'

// 获取分类列表
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

// 获取分类树形结构
export function getCategoryTree() {
  return request({
    url: '/category/tree',
    method: 'get'
  })
}

// 获取一级分类
export function getTopCategories() {
  return request({
    url: '/category/top',
    method: 'get'
  })
}
