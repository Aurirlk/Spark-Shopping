<template>
  <div class="order-list-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>

      <div class="order-tabs">
        <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
          <el-tab-pane label="全部订单" name="all" />
          <el-tab-pane label="待付款" name="0" />
          <el-tab-pane label="待发货" name="1" />
          <el-tab-pane label="待收货" name="2" />
          <el-tab-pane label="已完成" name="3" />
          <el-tab-pane label="已取消" name="4" />
        </el-tabs>
      </div>

      <div v-if="loading" class="loading-container">
        <el-icon class="loading" :size="40"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <template v-else>
        <div v-if="orderList.length > 0" class="order-list">
          <div
            v-for="order in orderList"
            :key="order.id"
            class="order-item"
          >
            <div class="order-header">
              <div class="order-info">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ order.createTime }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">
                  {{ order.statusDesc }}
                </el-tag>
              </div>
            </div>

            <div class="order-body">
              <div
                v-for="item in order.orderItems"
                :key="item.id"
                class="order-product"
                @click="$router.push(`/product/${item.productId}`)"
              >
                <img :src="item.productImage" :alt="item.productName" class="product-image">
                <div class="product-info">
                  <h3 class="product-name">{{ item.productName }}</h3>
                  <p class="product-price">¥{{ item.price }} x {{ item.quantity }}</p>
                </div>
                <div class="product-subtotal">
                  ¥{{ item.totalAmount }}
                </div>
              </div>
            </div>

            <div class="order-footer">
              <div class="order-total">
                <span>共 {{ order.orderItems.length }} 件商品</span>
                <span class="total-price">
                  合计：<strong>¥{{ order.payAmount }}</strong>
                </span>
              </div>

              <div class="order-actions">
                <template v-if="order.status === 0">
                  <el-button type="danger" size="small" @click="handleCancelOrder(order)">
                    取消订单
                  </el-button>
                </template>

                <template v-if="order.status === 2">
                  <el-button type="primary" size="small" @click="handleConfirmReceive(order)">
                    确认收货
                  </el-button>
                </template>

                <template v-if="order.status === 3 || order.status === 4">
                  <el-button type="danger" size="small" @click="handleDeleteOrder(order)">
                    删除订单
                  </el-button>
                </template>

                <el-button size="small" @click="$router.push(`/order/${order.id}`)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>

          <div class="pagination">
            <el-pagination
              v-model:current-page="pageNum"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @current-change="handlePageChange"
              @size-change="handleSizeChange"
            />
          </div>
        </div>

        <div v-else class="empty-order">
          <el-empty description="暂无订单">
            <el-button type="primary" @click="$router.push('/product')">去购物</el-button>
          </el-empty>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, cancelOrder, confirmReceive, deleteOrder } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeStatus = ref('all')

onMounted(async () => {
  await loadOrderList()
})

const loadOrderList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }
    const res = await getUserOrders(params)
    orderList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取订单列表失败：', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pageNum.value = 1
  loadOrderList()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadOrderList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  loadOrderList()
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return typeMap[status] || 'info'
}

const handleCancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败：', error)
    }
  }
}

const handleConfirmReceive = async (order) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', { type: 'warning' })
    await confirmReceive(order.id)
    ElMessage.success('确认收货成功')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败：', error)
    }
  }
}

const handleDeleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后将无法恢复', '提示', { type: 'warning' })
    await deleteOrder(order.id)
    ElMessage.success('订单已删除')
    loadOrderList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败：', error)
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

.order-tabs {
  background: #fff;
  padding: 0 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 20px;
}

.order-no {
  font-size: 14px;
  color: #333;
}

.order-time {
  font-size: 14px;
  color: #999;
}

.order-body {
  padding: 15px 20px;
}

.order-product {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px 0;
  cursor: pointer;
}

.order-product:hover {
  background: #f5f7fa;
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

.product-subtotal {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f5f7fa;
  border-top: 1px solid #eee;
}

.order-total {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.total-price strong {
  font-size: 16px;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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
