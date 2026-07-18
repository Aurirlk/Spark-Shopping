<template>
  <transition name="fade">
    <div v-if="!online" class="offline-bar">
      <el-icon><Connection /></el-icon>
      <span>网络已断开，请检查网络连接</span>
      <el-button size="small" @click="retry">重试</el-button>
    </div>
  </transition>
</template>

<script setup>
import { useOnlineStatus } from '@/utils/useScrollLoad'
import { Connection } from '@element-plus/icons-vue'

const { online } = useOnlineStatus()
const emit = defineEmits(['reconnect'])

const retry = () => {
  location.reload()
}
</script>

<style scoped>
.offline-bar {
  position: fixed; top: 0; left: 0; right: 0;
  background: #f56c6c; color: #fff;
  display: flex; align-items: center; justify-content: center;
  gap: 12px; padding: 10px; z-index: 9999;
  font-size: 14px;
}
.fade-enter-active, .fade-leave-active { transition: opacity .3s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
