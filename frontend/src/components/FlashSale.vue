<template>
  <section class="flash-sale" v-show="visible">
    <div class="flash-header">
      <div class="flash-title">
        <el-icon :size="20" color="#ff4757"><Lightning /></el-icon>
        <span>限时秒杀</span>
      </div>
      <div class="countdown">
        <span class="countdown-label">距结束</span>
        <span class="time-block">{{ displayH }}</span>
        <span class="colon">:</span>
        <span class="time-block">{{ displayM }}</span>
        <span class="colon">:</span>
        <span class="time-block">{{ displayS }}</span>
      </div>
      <el-button text type="primary" size="small" class="more-btn">更多 ›</el-button>
    </div>
    <div class="flash-body">
      <div class="flash-scroll">
        <div
          v-for="item in items"
          :key="item.id"
          class="flash-item"
          @click="$router.push(`/product/${item.id}`)"
        >
          <div class="flash-img">
            <img :src="item.img" :alt="item.name">
            <span class="flash-discount">-{{ item.discount }}%</span>
          </div>
          <div class="flash-name">{{ item.name }}</div>
          <div class="flash-price">
            <span class="now">¥{{ item.price }}</span>
            <span class="old">¥{{ item.oldPrice }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Lightning } from '@element-plus/icons-vue'

const props = defineProps({
  visible: { type: Boolean, default: true }
})

// 倒计时
const totalSeconds = ref(2 * 3600 + 30 * 60) // 2小时30分
let timer = null

const displayH = computed(() => String(Math.floor(totalSeconds.value / 3600)).padStart(2, '0'))
const displayM = computed(() => String(Math.floor((totalSeconds.value % 3600) / 60)).padStart(2, '0'))
const displayS = computed(() => String(totalSeconds.value % 60).padStart(2, '0'))

onMounted(() => {
  timer = setInterval(() => {
    if (totalSeconds.value > 0) totalSeconds.value--
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

// 秒杀商品
const items = [
  { id: 14, name: '耐克运动鞋', price: 399, oldPrice: 899, discount: 56, img: '/images/product/product-1.png' },
  { id: 3, name: 'AirPods Pro 2', price: 1299, oldPrice: 2499, discount: 48, img: '/images/product/product-2.png' },
  { id: 18, name: '三星 Galaxy S24', price: 4599, oldPrice: 7999, discount: 43, img: '/images/product/product-3.png' },
  { id: 7, name: '索尼降噪耳机', price: 1599, oldPrice: 2999, discount: 47, img: '/images/product/product-4.png' },
  { id: 9, name: '戴森吸尘器', price: 2990, oldPrice: 5990, discount: 50, img: '/images/product/product-5.png' },
  { id: 5, name: 'Apple Watch', price: 2999, oldPrice: 6999, discount: 57, img: '/images/product/product-6.png' },
]
</script>

<style scoped>
.flash-sale {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.flash-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  gap: 12px;
}

.flash-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: bold;
  font-size: 16px;
  color: #ff4757;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.countdown-label {
  font-size: 12px;
  color: #999;
  margin-right: 4px;
}

.time-block {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #ff4757;
  color: #fff;
  font-size: 14px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
  min-width: 28px;
}

.colon {
  color: #ff4757;
  font-weight: bold;
  font-size: 14px;
}

.more-btn {
  flex-shrink: 0;
}

.flash-body {
  padding: 0 12px 12px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.flash-body::-webkit-scrollbar {
  display: none;
}

.flash-scroll {
  display: flex;
  gap: 10px;
}

.flash-item {
  flex: 0 0 120px;
  cursor: pointer;
  transition: transform 0.2s;
}

.flash-item:hover {
  transform: translateY(-2px);
}

.flash-img {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  margin-bottom: 8px;
}

.flash-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.flash-discount {
  position: absolute;
  top: 4px;
  left: 4px;
  background: #ff4757;
  color: #fff;
  font-size: 11px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
}

.flash-name {
  font-size: 12px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.flash-price {
  display: flex;
  align-items: center;
  gap: 6px;
}

.now {
  font-size: 14px;
  font-weight: bold;
  color: #ff4757;
}

.old {
  font-size: 11px;
  color: #ccc;
  text-decoration: line-through;
}

/* 响应式 */
@media (max-width: 576px) {
  .flash-header { padding: 10px 12px; }
  .flash-item { flex: 0 0 100px; }
  .flash-img { width: 100px; height: 100px; }
}
</style>
