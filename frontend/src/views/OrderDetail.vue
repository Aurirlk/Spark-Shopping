<template>
  <div class="order-detail-page">
    <div class="container">
      <h1 class="page-title">订单详情</h1>

      <div v-if="loading" class="loading-container">
        <el-icon class="loading" :size="40"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <template v-else>
        <div v-if="order" class="order-content">
          <div class="section order-status-section">
            <div class="status-info">
              <h2>{{ order.statusDesc }}</h2>
              <p v-if="order.status === 0">请尽快完成支付，超时订单将自动取消</p>
              <p v-if="order.status === 2">商品已发货，请注意查收</p>
            </div>

            <div class="status-actions">
              <template v-if="order.status === 0">
                <el-button type="danger" size="large" @click="handleCancelOrder">
                  取消订单
                </el-button>
              </template>

              <template v-if="order.status === 2">
                <el-button type="primary" size="large" @click="handleConfirmReceive">
                  确认收货
                </el-button>
              </template>
            </div>
          </div>

          <div class="section">
            <div class="section-header">
              <h2>订单信息</h2>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单号：</span>
                <span class="value">{{ order.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">下单时间：</span>
                <span class="value">{{ order.createTime }}</span>
              </div>
              <div class="info-item">
                <span class="label">支付方式：</span>
                <span class="value">{{ order.payType === 1 ? '在线支付' : '货到付款' }}</span>
              </div>
              <div class="info-item">
                <span class="label">订单状态：</span>
                <span class="value">{{ order.statusDesc }}</span>
              </div>
            </div>
          </div>

          <div class="section">
            <div class="section-header">
              <h2>收货信息</h2>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span class="label">收货人：</span>
                <span class="value">{{ order.receiverName }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系电话：</span>
                <span class="value">{{ order.receiverPhone }}</span>
              </div>
              <div class="info-item full">
                <span class="label">收货地址：</span>
                <span class="value">{{ order.receiverAddress }}</span>
              </div>
              <div v-if="order.note" class="info-item full">
                <span class="label">订单备注：</span>
                <span class="value">{{ order.note }}</span>
              </div>
            </div>
          </div>

          <!-- 物流追踪 -->
          <div v-if="order.status >= 2" class="section">
            <div class="section-header">
              <h2>📦 物流追踪</h2>
            </div>
            <div v-if="logistics" class="logistics-box">
              <div class="logistics-header">
                <span class="l-company">{{ logistics.company }}</span>
                <span class="l-no">{{ logistics.trackingNo }}</span>
              </div>
              <div class="l-timeline">
                <div v-for="(t, i) in logistics.traces" :key="i" class="l-item" :class="{ active: i === logistics.currentStep, done: i <= logistics.currentStep }">
                  <div class="l-dot" />
                  <div class="l-content">
                    <p class="l-desc">{{ t.desc }}</p>
                    <span class="l-time">{{ t.time }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="loading" style="text-align:center;padding:20px;color:#999">加载物流信息...</div>
          </div>

          <div class="section">
            <div class="section-header">
              <h2>商品清单</h2>
            </div>

            <div class="product-list">
              <div
                v-for="item in order.orderItems"
                :key="item.id"
                class="product-item"
                @click="$router.push(`/product/${item.productId}`)"
              >
                <img :src="item.productImage" :alt="item.productName" class="product-image">
                <div class="product-info">
                  <h3 class="product-name">{{ item.productName }}</h3>
                  <p class="product-price">¥{{ item.price }}</p>
                </div>
                <div class="product-quantity">
                  x {{ item.quantity }}
                </div>
                <div class="product-subtotal">
                  ¥{{ item.totalAmount }}
                </div>
              </div>
            </div>
          </div>

          <div class="section order-amount">
            <div class="amount-item">
              <span class="label">商品金额：</span>
              <span class="value">¥{{ order.totalAmount }}</span>
            </div>
            <div class="amount-item">
              <span class="label">运费：</span>
              <span class="value">¥{{ order.freightAmount }}</span>
            </div>
            <div class="amount-item total">
              <span class="label">实付金额：</span>
              <span class="value price">¥{{ order.payAmount }}</span>
            </div>
          </div>
        </div>

        <div v-else class="empty-order">
          <el-empty description="订单不存在">
            <el-button type="primary" @click="$router.push('/order/list')">返回订单列表</el-button>
          </el-empty>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, confirmReceive } from '@/api/order'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref(null)
const logistics = ref(null)

onMounted(async () => {
  await loadOrderDetail()
})

const loadOrderDetail = async () => {
  const orderId = route.params.id
  if (!orderId) {
    ElMessage.error('订单ID不存在')
    router.push('/order/list')
    return
  }

  loading.value = true
  try {
    const res = await getOrderDetail(orderId)
    order.value = res.data
    // 已发货则加载物流
    if (order.value && order.value.status >= 2) {
      try {
        const logRes = await axios.get(`/api/logistics/track/${orderId}`)
        logistics.value = logRes.data.data
      } catch (e) { /* ignore */ }
    }
  } catch (error) {
    console.error('加载订单详情失败：', error)
  } finally {
    loading.value = false
  }
}

const handleCancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败：', error)
    }
  }
}

const handleConfirmReceive = async () => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', { type: 'warning' })
    await confirmReceive(order.value.id)
    ElMessage.success('确认收货成功')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败：', error)
    }
  }
}
</script>

<style scoped>
.container {
  width: 1200px;
  margin: 0 auto;
  padding: 20px 15px;
}

.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #666;
}

.loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  color: #333;
}

.order-status-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.status-info p {
  font-size: 14px;
  color: #666;
}

.status-actions {
  display: flex;
  gap: 15px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  gap: 10px;
}

.info-item.full {
  grid-column: 1 / -1;
}

.info-item .label {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
}

.info-item .value {
  font-size: 14px;
  color: #333;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
}

.product-item:hover {
  background: #ecf5ff;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.product-price {
  font-size: 14px;
  color: #666;
}

.product-quantity {
  font-size: 14px;
  color: #666;
}

.product-subtotal {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.order-amount {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.amount-item {
  display: flex;
  gap: 10px;
  font-size: 16px;
}

.amount-item .label {
  color: #666;
}

.amount-item .value {
  color: #333;
  font-weight: bold;
}

.amount-item.total {
  font-size: 18px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.amount-item.total .price {
  color: #f56c6c;
  font-size: 24px;
}

.empty-order {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: #fff;
  border-radius: 8px;
}
</style>
