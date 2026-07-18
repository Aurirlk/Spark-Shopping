<template>
  <div id="app">
    <OfflineNotice />
    <router-view v-slot="{ Component, route }">
      <transition name="page" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
    <MobileTabBar />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import MobileTabBar from '@/components/MobileTabBar.vue'
import OfflineNotice from '@/components/OfflineNotice.vue'
import { useTheme } from '@/stores/theme'

useTheme() // 初始化主题

const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(async () => {
  if (userStore.isLoggedIn) {
    try {
      await userStore.getUserInfoAction()
      await cartStore.getCartCountAction()
    } catch (error) {
      console.error('初始化失败：', error)
    }
  }
})
</script>

<style>
@import '@/assets/responsive.css';
@import '@/assets/animations.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f5f5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
  padding-bottom: env(safe-area-inset-bottom);
}

/* CSS 变量（主题换肤 + 暗色模式） */
:root {
  --primary: #409eff;
  --primary-light: #ecf5ff;
  --danger: #f56c6c;
  --gradient-start: #1e3c72;
  --gradient-end: #2a5298;
  --banner-bg: linear-gradient(135deg, var(--gradient-start), var(--gradient-end));
  --bg: #f5f5f5;
  --card-bg: #fff;
  --text: #303133;
  --text-secondary: #909399;
}

.dark {
  --primary: #409eff;
  --primary-light: #1a2a3a;
  --danger: #ff6b6b;
  --gradient-start: #0f1923;
  --gradient-end: #1a2a3a;
  --banner-bg: linear-gradient(135deg, #0f1923, #1a2a3a);
  --bg: #0f1923;
  --card-bg: #1a2332;
  --text: #e0e0e0;
  --text-secondary: #8899aa;
}

body { background: var(--bg); color: var(--text); }

@media (max-width: 768px) {
  #app {
    padding-bottom: 64px;
  }
}

/* ===== 页面过渡动画 ===== */
.page-enter-active {
  animation: pageIn 0.3s ease-out;
}
.page-leave-active {
  animation: pageOut 0.2s ease-in;
}
@keyframes pageIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes pageOut {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-10px); }
}

/* ===== 滚动条美化 ===== */
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #ddd; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #ccc; }
</style>
