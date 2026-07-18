<template>
  <div class="create-order-page">
    <div class="container">
      <h1 class="page-title">确认订单</h1>

      <div v-if="loading" class="loading-container">
        <el-icon class="loading" :size="40"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <template v-else>
        <div class="order-content">
          <div class="section">
            <div class="section-header">
              <h2>收货地址</h2>
              <el-button type="primary" link @click="showAddressDialog">
                新增收货地址
              </el-button>
            </div>

            <div v-if="addressList.length > 0" class="address-list">
              <div
                v-for="address in addressList"
                :key="address.id"
                class="address-item"
                :class="{ active: selectedAddress?.id === address.id }"
                @click="selectedAddress = address"
              >
                <div class="address-info">
                  <div class="receiver-info">
                    <span class="name">{{ address.receiverName }}</span>
                    <span class="phone">{{ address.receiverPhone }}</span>
                    <el-tag v-if="address.isDefault === 1" type="danger" size="small">
                      默认
                    </el-tag>
                  </div>
                  <div class="address-detail">
                    {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
                  </div>
                </div>
                <el-icon v-if="selectedAddress?.id === address.id" class="check-icon">
                  <CircleCheck />
                </el-icon>
              </div>
            </div>

            <div v-else class="empty-address">
              <el-empty description="暂无收货地址">
                <el-button type="primary" @click="showAddressDialog">添加地址</el-button>
              </el-empty>
            </div>
          </div>

          <div class="section">
            <div class="section-header">
              <h2>商品清单</h2>
            </div>

            <div class="product-list">
              <div
                v-for="item in orderItems"
                :key="item.productId"
                class="product-item"
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

          <div class="section">
            <div class="section-header">
              <h2>订单备注</h2>
            </div>
            <el-input
              v-model="orderNote"
              type="textarea"
              :rows="3"
              placeholder="请输入订单备注（选填）"
            />
          </div>

          <div class="section">
            <div class="section-header">
              <h2>支付方式</h2>
            </div>
            <el-radio-group v-model="payType">
              <el-radio :label="1">在线支付</el-radio>
              <el-radio :label="2">货到付款</el-radio>
            </el-radio-group>
          </div>

          <div class="section order-amount">
            <div class="amount-item">
              <span class="label">商品金额：</span>
              <span class="value">¥{{ productAmount }}</span>
            </div>
            <div class="amount-item">
              <span class="label">运费：</span>
              <span class="value">¥{{ freightAmount }}</span>
            </div>
            <div class="amount-item total">
              <span class="label">应付金额：</span>
              <span class="value price">¥{{ totalAmount }}</span>
            </div>
          </div>

          <div class="submit-section">
            <div class="submit-info">
              <span>应付金额：</span>
              <span class="price">¥{{ totalAmount }}</span>
            </div>
            <el-button
              type="danger"
              size="large"
              class="submit-btn"
              :loading="submitting"
              :disabled="!selectedAddress"
              @click="handleSubmitOrder"
            >
              提交订单
            </el-button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { getAddressList } from '@/api/address'
import { createOrder } from '@/api/order'
import { getProductDetail } from '@/api/product'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const selectedAddress = ref(null)
const orderNote = ref('')
const payType = ref(1)
const freightAmount = ref(0)
const directBuyItems = ref([]) // 立即购买商品

const orderItems = computed(() => {
  // 立即购买：从 directBuyItems 取
  if (directBuyItems.value.length > 0) {
    return directBuyItems.value.map(item => ({
      productId: item.productId,
      productName: item.productName,
      productImage: item.productImage,
      price: item.price,
      quantity: item.quantity,
      totalAmount: item.price * item.quantity
    }))
  }
  // 从购物车结算
  const cartIds = JSON.parse(localStorage.getItem('checkoutCartIds') || '[]')
  return cartStore.cartList
    .filter(item => cartIds.includes(item.id))
    .map(item => ({
      productId: item.productId,
      productName: item.product?.name,
      productImage: item.product?.mainImage,
      price: item.product?.price,
      quantity: item.quantity,
      totalAmount: item.subtotal
    }))
})

const productAmount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.totalAmount, 0)
})

const totalAmount = computed(() => {
  return productAmount.value + freightAmount.value
})

onMounted(async () => {
  await loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    // 检查是否是立即购买（有 query 参数）
    const productId = route.query.productId
    const quantity = route.query.quantity

    if (productId) {
      // 立即购买：直接查商品信息
      const res = await getProductDetail(productId)
      directBuyItems.value = [{
        productId: Number(productId),
        productName: res.data.name,
        productImage: res.data.mainImage,
        price: res.data.price,
        quantity: Number(quantity) || 1
      }]
    } else {
      // 从购物车结算
      await cartStore.getCartListAction()
    }
    await loadAddressList()
  } catch (error) {
    console.error('加载数据失败：', error)
  } finally {
    loading.value = false
  }
}

const loadAddressList = async () => {
  const res = await getAddressList()
  addressList.value = res.data

  const defaultAddress = addressList.value.find(a => a.isDefault === 1)
  if (defaultAddress) {
    selectedAddress.value = defaultAddress
  } else if (addressList.value.length > 0) {
    selectedAddress.value = addressList.value[0]
  }
}

const showAddressDialog = () => {
  ElMessage.info('新增地址功能待实现')
}

const handleSubmitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning('订单商品为空')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      items: orderItems.value.map(i => ({
        productId: i.productId,
        quantity: i.quantity
      })),
      receiverName: selectedAddress.value.receiverName,
      receiverPhone: selectedAddress.value.receiverPhone,
      receiverAddress: `${selectedAddress.value.province}${selectedAddress.value.city}${selectedAddress.value.district}${selectedAddress.value.detailAddress}`,
      note: orderNote.value,
      payType: payType.value
    }

    const res = await createOrder(orderData)

    ElMessage.success('订单创建成功')
    localStorage.removeItem('checkoutCartIds')
    router.push(`/payment?orderId=${res.data.id}&orderNo=${res.data.orderNo}&amount=${res.data.totalAmount}`)
  } catch (error) {
    console.error('创建订单失败：', error)
  } finally {
    submitting.value = false
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  color: #333;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.address-info {
  flex: 1;
}

.receiver-info {
  margin-bottom: 8px;
}

.receiver-info .name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-right: 15px;
}

.receiver-info .phone {
  color: #666;
  margin-right: 15px;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.check-icon {
  color: #409eff;
  font-size: 24px;
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
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
}

.product-price {
  color: #f56c6c;
  font-weight: bold;
}

.product-quantity {
  color: #666;
}

.product-subtotal {
  font-size: 16px;
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

.submit-section {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.submit-info {
  font-size: 16px;
}

.submit-info .price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.submit-btn {
  height: 50px;
  font-size: 16px;
  padding: 0 50px;
}
</style>
