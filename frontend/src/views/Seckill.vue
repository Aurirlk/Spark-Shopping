<template>
  <div class="seckill-page">
    <div class="container">
      <div class="seckill-header">
        <h1>⚡ 限时秒杀</h1>
        <div class="countdown">距结束 <b>{{ h }}</b>:<b>{{ m }}</b>:<b>{{ s }}</b></div>
      </div>
      <div v-if="loading" style="text-align:center;padding:60px"><el-icon class="spin" :size="40"><Loading /></el-icon></div>
      <div v-else-if="list.length === 0" class="empty"><el-empty description="暂无秒杀活动" /></div>
      <div v-else class="seckill-grid">
        <div v-for="item in list" :key="item.id" class="sec-card">
          <div class="sec-img"><img :src="item.main_image" :alt="item.product_name"></div>
          <div class="sec-body">
            <h3>{{ item.product_name }}</h3>
            <div class="sec-price">
              <span class="now">¥{{ item.seckill_price }}</span>
              <span class="old">¥{{ item.original_price }}</span>
            </div>
            <div class="sec-progress">
              <el-progress :percentage="item.stock > 0 ? Math.round((1 - item.stock / 50) * 100) : 100" :stroke-width="6" color="#ff4757" />
              <span class="sec-stock">仅剩 {{ item.stock }} 件</span>
            </div>
            <el-button type="danger" size="large" class="sec-btn" :disabled="item.stock <= 0" @click="handleBuy(item.id)" :loading="buyingId === item.id">
              {{ item.stock > 0 ? '立即抢购' : '已售罄' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(true)
const list = ref([])
const buyingId = ref(null)

// 倒计时
const totalSec = ref(2 * 3600 + 30 * 60)
let timer = null
const h = computed(() => String(Math.floor(totalSec.value / 3600)).padStart(2, '0'))
const m = computed(() => String(Math.floor((totalSec.value % 3600) / 60)).padStart(2, '0'))
const s = computed(() => String(totalSec.value % 60).padStart(2, '0'))

onMounted(async () => {
  try {
    const res = await axios.get('/api/seckill/list')
    list.value = res.data.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
  timer = setInterval(() => { if (totalSec.value > 0) totalSec.value-- }, 1000)
})
onUnmounted(() => clearInterval(timer))

const handleBuy = async (id) => {
  buyingId.value = id
  try {
    const res = await axios.post(`/api/seckill/buy/${id}`)
    ElMessage.success(res.data.data || '秒杀成功！')
    list.value = list.value.map(i => i.id === id ? { ...i, stock: i.stock - 1 } : i)
  } catch (e) { ElMessage.error('抢购失败') }
  finally { buyingId.value = null }
}
</script>

<style scoped>
.seckill-page { padding: 20px 0; min-height: calc(100vh - 120px); background: #f5f7fa; }
.seckill-header { display: flex; justify-content: space-between; align-items: center; background: linear-gradient(135deg,#ff4757,#ff6b81); color: #fff; padding: 16px 24px; border-radius: 12px; margin-bottom: 20px; }
.seckill-header h1 { font-size: 24px; margin: 0; }
.countdown b { background: rgba(255,255,255,.2); padding: 4px 8px; border-radius: 4px; margin: 0 2px; font-size: 20px; }
.seckill-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; }
.sec-card { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.sec-img { height: 200px; overflow: hidden; }
.sec-img img { width: 100%; height: 100%; object-fit: cover; transition: transform .3s; }
.sec-card:hover .sec-img img { transform: scale(1.05); }
.sec-body { padding: 14px; }
.sec-body h3 { font-size: 15px; margin-bottom: 8px; }
.sec-price { display: flex; align-items: baseline; gap: 8px; margin-bottom: 10px; }
.now { font-size: 24px; font-weight: bold; color: #ff4757; }
.old { font-size: 14px; color: #ccc; text-decoration: line-through; }
.sec-progress { margin-bottom: 12px; }
.sec-stock { font-size: 12px; color: #ff4757; display: block; margin-top: 4px; }
.sec-btn { width: 100%; border-radius: 8px; }
.empty { text-align: center; padding: 60px; }
</style>
