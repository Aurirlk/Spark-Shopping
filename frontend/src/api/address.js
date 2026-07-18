import request from './request'

// 获取收货地址列表
export function getAddressList() {
  return request({
    url: '/user/address/list',
    method: 'get'
  })
}

// 添加收货地址
export function addAddress(data) {
  return request({
    url: '/user/address/add',
    method: 'post',
    data
  })
}

// 更新收货地址
export function updateAddress(data) {
  return request({
    url: '/user/address/update',
    method: 'put',
    data
  })
}

// 删除收货地址
export function deleteAddress(id) {
  return request({
    url: `/user/address/delete/${id}`,
    method: 'delete'
  })
}

// 设置默认地址
export function setDefaultAddress(id) {
  return request({
    url: `/user/address/default/${id}`,
    method: 'put'
  })
}
