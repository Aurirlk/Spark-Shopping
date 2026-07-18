import request from './request'

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(params) {
  return request({
    url: '/user/password',
    method: 'put',
    params
  })
}
