import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getCartList,
  addToCart,
  updateQuantity,
  removeFromCart,
  checkCart,
  checkAll,
  clearCart,
  getCartCount
} from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartList = ref([])
  const cartCount = ref(0)

  const checkedCartList = computed(() => cartList.value.filter(item => item.checked === 1))
  const totalPrice = computed(() => {
    return checkedCartList.value.reduce((total, item) => {
      return total + (item.product?.price || 0) * item.quantity
    }, 0)
  })
  const totalCount = computed(() => {
    return checkedCartList.value.reduce((total, item) => total + item.quantity, 0)
  })

  async function getCartListAction() {
    const res = await getCartList()
    cartList.value = res.data
    return res
  }

  async function addToCartAction(productId, quantity) {
    const res = await addToCart(productId, quantity)
    await getCartListAction()
    await getCartCountAction()
    return res
  }

  async function updateQuantityAction(cartId, quantity) {
    const res = await updateQuantity(cartId, quantity)
    await getCartListAction()
    await getCartCountAction()
    return res
  }

  async function removeFromCartAction(cartId) {
    const res = await removeFromCart(cartId)
    await getCartListAction()
    await getCartCountAction()
    return res
  }

  async function checkCartAction(cartId, checked) {
    const res = await checkCart(cartId, checked)
    await getCartListAction()
    return res
  }

  async function checkAllAction(checked) {
    const res = await checkAll(checked)
    await getCartListAction()
    return res
  }

  async function clearCartAction() {
    const res = await clearCart()
    cartList.value = []
    cartCount.value = 0
    return res
  }

  async function getCartCountAction() {
    const res = await getCartCount()
    cartCount.value = res.data
    return res
  }

  return {
    cartList,
    cartCount,
    checkedCartList,
    totalPrice,
    totalCount,
    getCartListAction,
    addToCartAction,
    updateQuantityAction,
    removeFromCartAction,
    checkCartAction,
    checkAllAction,
    clearCartAction,
    getCartCountAction
  }
})
