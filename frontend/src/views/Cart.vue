<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">购物车</h1>

      <div v-if="loading" class="loading-container">
        <el-icon class="loading" :size="40"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <template v-else-if="cartStore.cartList.length > 0">
        <!-- ===== PC端：表格布局 ===== -->
        <div class="cart-table hide-mobile">
          <div class="cart-header">
            <div class="col-check"><el-checkbox v-model="isAllChecked" @change="handleCheckAll">全选</el-checkbox></div>
            <div class="col-product">商品信息</div>
            <div class="col-price">单价</div>
            <div class="col-qty">数量</div>
            <div class="col-total">小计</div>
            <div class="col-action">操作</div>
          </div>
          <div v-for="item in cartStore.cartList" :key="item.id" class="cart-row">
            <div class="col-check"><el-checkbox v-model="item.checked" :true-label="1" :false-label="0" @change="handleCheckItem(item)" /></div>
            <div class="col-product">
              <div class="product-info" @click="$router.push(`/product/${item.productId}`)">
                <img :src="item.product?.mainImage" :alt="item.product?.name" class="p-img">
                <div class="p-detail">
                  <h4 class="p-name">{{ item.product?.name }}</h4>
                  <p class="p-desc">{{ item.product?.description }}</p>
                </div>
              </div>
            </div>
            <div class="col-price">¥{{ item.product?.price }}</div>
            <div class="col-qty">
              <el-input-number v-model="item.quantity" :min="1" :max="item.product?.stock || 99" size="small" @change="handleQuantityChange(item)" />
            </div>
            <div class="col-total subtotal">¥{{ (item.product?.price * item.quantity).toFixed(2) }}</div>
            <div class="col-action"><el-button type="danger" link @click="handleDeleteItem(item)">删除</el-button></div>
          </div>
        </div>

        <!-- ===== 手机端：卡片布局 ===== -->
        <div class="cart-cards show-mobile">
          <div v-for="item in cartStore.cartList" :key="item.id" class="cart-card">
            <div class="card-row top">
              <el-checkbox v-model="item.checked" :true-label="1" :false-label="0" @change="handleCheckItem(item)" />
              <img :src="item.product?.mainImage" :alt="item.product?.name" class="card-img" @click="$router.push(`/product/${item.productId}`)">
              <div class="card-info" @click="$router.push(`/product/${item.productId}`)">
                <h4 class="card-name">{{ item.product?.name }}</h4>
                <p class="card-desc">{{ item.product?.description }}</p>
              </div>
            </div>
            <div class="card-row bottom">
              <div class="card-price">
                <span class="price">¥{{ item.product?.price }}</span>
                <span class="card-subtotal">小计：<b>¥{{ (item.product?.price * item.quantity).toFixed(2) }}</b></span>
              </div>
              <div class="card-actions">
                <el-input-number v-model="item.quantity" :min="1" :max="item.product?.stock || 99" size="small" @change="handleQuantityChange(item)" />
                <el-button :icon="Delete" circle size="small" type="danger" plain @click="handleDeleteItem(item)" />
              </div>
            </div>
          </div>
        </div>

        <!-- ===== 满减进度条 ===== -->
        <div v-if="discountNext" class="discount-bar">
          <div class="discount-text">再买 <b>¥{{ discountNext }}</b> 可享受满减优惠</div>
          <el-progress :percentage="discountPercent" :stroke-width="8" color="#f56c6c" />
        </div>

        <!-- ===== PC底部栏 ===== -->
        <div class="cart-footer hide-mobile">
          <div class="footer-left">
            <el-checkbox v-model="isAllChecked" @change="handleCheckAll">全选</el-checkbox>
            <el-button type="danger" link @click="handleClearCart">清空</el-button>
          </div>
          <div class="footer-right">
            <span>已选 <b class="num">{{ cartStore.totalCount }}</b> 件</span>
            <span>合计：<b class="total">¥{{ cartStore.totalPrice.toFixed(2) }}</b></span>
            <el-button type="primary" size="large" class="btn-checkout" :disabled="cartStore.totalCount === 0" @click="handleCheckout">去结算</el-button>
          </div>
        </div>

        <!-- ===== 手机端底部固定栏 ===== -->
        <div class="mobile-checkout-bar show-mobile">
          <div class="bar-left">
            <el-checkbox v-model="isAllChecked" @change="handleCheckAll">全选</el-checkbox>
            <el-button type="danger" link size="small" @click="handleClearCart">清空</el-button>
          </div>
          <div class="bar-right">
            <div class="bar-total">
              <span>¥</span><span class="bar-price">{{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <el-button type="primary" class="bar-btn" :disabled="cartStore.totalCount === 0" @click="handleCheckout">
              结算({{ cartStore.totalCount }})
            </el-button>
          </div>
        </div>
      </template>

      <!-- 空购物车 -->
      <div v-else class="empty-cart">
        <el-empty :image-size="160" description="购物车是空的，去逛逛吧">
          <el-button type="primary" size="large" @click="$router.push('/product')">去购物</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { updateQuantity, removeFromCart, checkAll, clearCart } from '@/api/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(false)

// 满减进度
const thresholds = [100, 200, 500, 1000]
const discountNext = computed(() => {
  const total = cartStore.totalPrice
  for (const t of thresholds) {
    if (total < t) return (t - total).toFixed(2)
  }
  return 0
})
const discountPercent = computed(() => {
  const total = cartStore.totalPrice
  for (const t of thresholds) {
    if (total < t) return Math.round((total / t) * 100)
  }
  return 100
})

const isAllChecked = computed({
  get() { return cartStore.cartList.length > 0 && cartStore.cartList.every(i => i.checked === 1) },
  set() {}
})

onMounted(async () => {
  loading.value = true
  try { await cartStore.getCartListAction() } finally { loading.value = false }
})

const handleCheckAll = async (checked) => {
  await checkAll(checked ? 1 : 0)
  await cartStore.getCartListAction()
}
const handleCheckItem = async (item) => {
  await updateQuantity(item.id, item.quantity)
  await cartStore.getCartListAction()
}
const handleQuantityChange = async (item) => {
  await updateQuantity(item.id, item.quantity)
  await cartStore.getCartListAction()
}
const handleDeleteItem = async (item) => {
  await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
  await removeFromCart(item.id)
  await cartStore.getCartListAction()
  ElMessage.success('已删除')
}
const handleClearCart = async () => {
  await ElMessageBox.confirm('确定要清空购物车吗？', '提示', { type: 'warning' })
  await clearCart()
  await cartStore.getCartListAction()
  ElMessage.success('已清空')
}
const handleCheckout = () => {
  const checked = cartStore.cartList.filter(i => i.checked === 1)
  if (!checked.length) { ElMessage.warning('请选择商品'); return }
  localStorage.setItem('checkoutCartIds', JSON.stringify(checked.map(i => i.id)))
  router.push('/order/create')
}
</script>

<style scoped>
.cart-page { padding: 20px 0; background: #f5f7fa; min-height: calc(100vh - 120px); }
.page-title { font-size: 22px; color: #303133; margin-bottom: 20px; }
.loading-container { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 400px; color: #909399; }
.empty-cart { display: flex; justify-content: center; align-items: center; height: 400px; background: #fff; border-radius: 12px; }

/* ===== PC表格 ===== */
.cart-table { background: #fff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); overflow: hidden; margin-bottom: 16px; }
.cart-header, .cart-row { display: flex; align-items: center; padding: 14px 20px; }
.cart-header { background: #f5f7fa; border-bottom: 1px solid #ebeef5; font-size: 14px; color: #606266; }
.cart-row { border-bottom: 1px solid #f0f0f0; transition: background 0.2s; }
.cart-row:hover { background: #fafafa; }
.cart-row:last-child { border-bottom: none; }
.col-check { width: 80px; }
.col-product { flex: 1; min-width: 0; }
.col-price { width: 100px; text-align: center; color: #606266; }
.col-qty { width: 140px; display: flex; justify-content: center; }
.col-total { width: 100px; text-align: center; }
.col-action { width: 60px; text-align: center; }
.subtotal { font-size: 14px; font-weight: bold; color: #f56c6c; }
.product-info { display: flex; align-items: center; gap: 14px; cursor: pointer; }
.p-img { width: 70px; height: 70px; object-fit: cover; border-radius: 8px; background: #f5f7fa; flex-shrink: 0; }
.p-detail { min-width: 0; }
.p-name { font-size: 14px; color: #303133; margin-bottom: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.p-desc { font-size: 12px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* ===== PC底部 ===== */
.cart-footer { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; background: #fff; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.footer-left { display: flex; align-items: center; gap: 16px; }
.footer-right { display: flex; align-items: center; gap: 20px; font-size: 14px; color: #606266; }
.num { color: #409eff; }
.total { font-size: 22px; color: #f56c6c; }
.btn-checkout { height: 44px; font-size: 16px; padding: 0 32px; border-radius: 22px; }

/* ===== 手机端卡片 ===== */
.cart-cards { display: flex; flex-direction: column; gap: 12px; padding-bottom: 80px; }
.cart-card { background: #fff; border-radius: 12px; padding: 14px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.card-row { display: flex; align-items: flex-start; gap: 12px; }
.card-row.top { margin-bottom: 12px; }
.card-img { width: 90px; height: 90px; object-fit: cover; border-radius: 8px; background: #f5f7fa; flex-shrink: 0; cursor: pointer; }
.card-info { flex: 1; cursor: pointer; min-width: 0; }
.card-name { font-size: 14px; color: #303133; margin-bottom: 6px; line-height: 1.3; }
.card-desc { font-size: 12px; color: #909399; line-height: 1.4; }
.card-row.bottom { align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 8px; }
.card-price { display: flex; flex-direction: column; gap: 2px; }
.price { font-size: 16px; font-weight: bold; color: #f56c6c; }
.card-subtotal { font-size: 12px; color: #909399; }
.card-subtotal b { color: #f56c6c; }
.card-actions { display: flex; align-items: center; gap: 8px; }

/* ===== 手机端底部固定栏 ===== */
.mobile-checkout-bar { position: fixed; bottom: 56px; left: 0; right: 0; background: #fff; border-top: 1px solid #eee; padding: 8px 12px; padding-bottom: calc(8px + env(safe-area-inset-bottom)); display: flex; align-items: center; justify-content: space-between; z-index: 100; box-shadow: 0 -2px 8px rgba(0,0,0,0.06); }
.bar-left { display: flex; align-items: center; gap: 8px; }
.bar-right { display: flex; align-items: center; gap: 10px; }
.bar-total { display: flex; align-items: baseline; gap: 0; }
.bar-total span:first-child { font-size: 14px; color: #f56c6c; }
.bar-price { font-size: 20px; font-weight: bold; color: #f56c6c; }
.bar-btn { border-radius: 20px; padding: 8px 20px; font-size: 14px; flex-shrink: 0; }

/* ===== 满减进度条 ===== */
.discount-bar { background: #fff; border-radius: 12px; padding: 12px 16px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.discount-text { font-size: 13px; color: #666; margin-bottom: 6px; }
.discount-text b { color: #f56c6c; }

/* ===== 响应式控制 ===== */
@media (max-width: 768px) {
  .cart-page { padding: 12px 0; }
  .page-title { font-size: 18px; margin-bottom: 12px; }
  .cart-footer { margin-bottom: 60px; }
}
</style>
