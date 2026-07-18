<template>
  <div class="payment-page">
    <div class="container">
      <div v-if="paying" class="pay-box">
        <el-icon :size="60" color="#67c23a"><CircleCheck /></el-icon>
        <h2>支付成功！</h2>
        <p class="pay-amount">¥{{ amount }}</p>
        <p class="pay-order">订单号：{{ orderNo }}</p>
        <el-button type="primary" size="large" @click="$router.push('/order/list')" class="mt-20">查看订单</el-button>
      </div>

      <div v-else-if="timeout" class="pay-box">
        <el-icon :size="60" color="#f56c6c"><CircleClose /></el-icon>
        <h2>支付超时</h2>
        <p>订单已自动取消</p>
        <el-button type="primary" size="large" @click="$router.push('/order/list')" class="mt-20">重新下单</el-button>
      </div>

      <div v-else class="pay-card">
        <div class="pay-header">
          <h2>确认支付</h2>
          <div class="countdown">剩余 <b>{{ mm }}:{{ ss }}</b></div>
        </div>
        <div class="pay-amount-box">
          <span class="label">应付金额</span>
          <span class="amount">¥{{ amount }}</span>
        </div>
        <div class="pay-methods">
          <div class="method" :class="{ active: method === 'wx' }" @click="method = 'wx'">
            <el-icon :size="24" color="#07c160"><ChatDotRound /></el-icon><span>微信支付</span>
          </div>
          <div class="method" :class="{ active: method === 'ali' }" @click="method = 'ali'">
            <el-icon :size="24" color="#1677ff"><Wallet /></el-icon><span>支付宝</span>
          </div>
        </div>
        <el-button type="primary" size="large" class="pay-btn" :loading="submitting" @click="handlePay">
          确认支付 ¥{{ amount }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose, ChatDotRound, Wallet } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute(); const router = useRouter()
const orderNo = ref(route.query.orderNo || '')
const amount = ref(Number(route.query.amount) || 0)
const orderId = ref(Number(route.query.orderId) || 0)

const method = ref('wx'); const submitting = ref(false)
const paying = ref(false); const timeout = ref(false)

const totalSec = ref(15 * 60)
const mm = computed(() => String(Math.floor(totalSec.value / 60)).padStart(2, '0'))
const ss = computed(() => String(totalSec.value % 60).padStart(2, '0'))
let timer = null

onMounted(() => {
  timer = setInterval(() => {
    if (totalSec.value > 0) totalSec.value--
    else { clearInterval(timer); timeout.value = true }
  }, 1000)
})
onUnmounted(() => clearInterval(timer))

const handlePay = async () => {
  submitting.value = true
  try {
    await axios.post(`/api/payment/pay/${orderId.value}?payType=${method.value === 'wx' ? 1 : 2}`)
    paying.value = true; clearInterval(timer)
  } catch (e) { ElMessage.error('支付失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.payment-page { padding: 40px 0; min-height: calc(100vh - 120px); background: #f5f7fa; }
.pay-card { background: #fff; border-radius: 12px; padding: 30px; max-width: 480px; margin: 0 auto; box-shadow: 0 2px 12px rgba(0,0,0,.08); }
.pay-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.pay-header h2 { font-size: 20px; margin: 0; }
.countdown b { color: #f56c6c; font-size: 18px; }
.pay-amount-box { background: #fef0f0; padding: 20px; border-radius: 8px; margin-bottom: 24px; text-align: center; }
.pay-amount-box .label { font-size: 14px; color: #999; display: block; margin-bottom: 8px; }
.pay-amount-box .amount { font-size: 36px; font-weight: bold; color: #f56c6c; }
.pay-methods { display: flex; gap: 12px; margin-bottom: 24px; }
.method { flex: 1; border: 2px solid #eee; border-radius: 8px; padding: 16px; text-align: center; cursor: pointer; transition: all .2s; display: flex; align-items: center; justify-content: center; gap: 8px; }
.method.active { border-color: #409eff; background: #ecf5ff; }
.pay-btn { width: 100%; height: 48px; font-size: 16px; border-radius: 24px; }
.pay-box { text-align: center; padding: 60px 0; }
.pay-box h2 { margin: 16px 0 8px; font-size: 24px; }
.pay-amount { font-size: 28px; font-weight: bold; color: #f56c6c; margin-bottom: 8px; }
.pay-order { color: #999; font-size: 14px; }
.mt-20 { margin-top: 20px; }
</style>
