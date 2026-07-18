<template>
  <div class="mobile-tab-bar">
    <div class="tab-item" :class="{ active: activeTab === 'home' }" @click="goTo('/home')">
      <el-icon :size="22"><HomeFilled /></el-icon>
      <span>首页</span>
    </div>
    <div class="tab-item" :class="{ active: activeTab === 'category' }" @click="goTo('/product')">
      <el-icon :size="22"><Grid /></el-icon>
      <span>分类</span>
    </div>
    <div class="tab-item cart-tab" :class="{ active: activeTab === 'cart' }" @click="goTo('/cart')">
      <el-badge :value="cartCount" :hidden="!cartCount" :max="99">
        <el-icon :size="22"><ShoppingCart /></el-icon>
      </el-badge>
      <span>购物车</span>
    </div>
    <div class="tab-item" :class="{ active: activeTab === 'user' }" @click="goTo('/user')">
      <el-icon :size="22"><User /></el-icon>
      <span>我的</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { HomeFilled, Grid, ShoppingCart, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const cartCount = computed(() => cartStore.cartCount)

const activeTab = computed(() => {
  const path = route.path
  if (path === '/home') return 'home'
  if (path.startsWith('/product')) return 'category'
  if (path === '/cart') return 'cart'
  if (path.startsWith('/user') || path.startsWith('/order')) return 'user'
  return 'home'
})

const goTo = (path) => {
  if (route.path !== path) router.push(path)
}
</script>

<style scoped>
.mobile-tab-bar {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background: #fff;
  border-top: 1px solid #eee;
  z-index: 1000;
  padding-bottom: env(safe-area-inset-bottom);
}

@media (max-width: 768px) {
  .mobile-tab-bar {
    display: flex;
    justify-content: space-around;
    align-items: center;
  }
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  color: #999;
  transition: color 0.2s;
  padding: 4px 12px;
  position: relative;
}

.tab-item span {
  font-size: 10px;
}

.tab-item.active {
  color: #409eff;
}

.tab-item:hover {
  color: #409eff;
}

.cart-tab :deep(.el-badge) {
  margin-top: 2px;
}
</style>
