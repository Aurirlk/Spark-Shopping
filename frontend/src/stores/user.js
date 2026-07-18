import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')

  async function loginAction(loginData) {
    const res = await login(loginData)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    return res
  }

  async function getUserInfoAction() {
    const res = await getUserInfo()
    userInfo.value = res.data
    return res
  }

  async function logoutAction() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function clearToken() {
    token.value = ''
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    loginAction,
    getUserInfoAction,
    logoutAction,
    setToken,
    clearToken
  }
})
