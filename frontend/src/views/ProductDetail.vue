<template>
  <div class="product-detail-page">
    <div class="container">
      <!-- 面包屑 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/product' }">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div v-if="loading" class="loading-container">
        <el-icon class="loading" :size="40"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <template v-else>
        <div class="product-main">
          <!-- 商品图片 -->
          <div class="product-gallery">
            <div class="main-image">
              <img :src="product.mainImage" :alt="product.name">
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="product-info">
            <div class="title-row">
              <h1 class="product-title">{{ product.name }}</h1>
              <div class="title-actions">
                <el-button :icon="Share" circle @click="handleShare" title="分享" />
                <el-button :icon="isFav ? StarFilled : Star" :type="isFav ? 'warning' : 'default'" circle @click="handleToggleFav" :title="isFav ? '取消收藏' : '收藏'" />
              </div>
            </div>
            <p class="product-desc">{{ product.description }}</p>

            <div class="price-box">
              <div class="price-row">
                <span class="label">促销价</span>
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              </div>
            </div>

            <div class="info-row">
              <span class="label">销量</span>
              <span class="value">{{ product.sales }} 件</span>
            </div>

            <div class="info-row">
              <span class="label">库存</span>
              <span class="value">{{ product.stock }} 件</span>
            </div>

            <div class="info-row">
              <span class="label">分类</span>
              <span class="value">{{ product.categoryName || '未分类' }}</span>
            </div>

            <div class="quantity-row">
              <span class="label">数量</span>
              <el-input-number v-model="quantity" :min="1" :max="product.stock" size="large" />
            </div>

            <div class="action-buttons">
              <el-button type="primary" size="large" class="buy-btn" @click="handleBuyNow">
                立即购买
              </el-button>
              <el-button type="warning" size="large" class="cart-btn" @click="handleAddToCart">
                加入购物车
              </el-button>
            </div>
          </div>
        </div>

        <!-- 商品详情 -->
        <div class="product-detail-section">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="商品详情" name="detail">
              <div class="detail-content" v-html="product.detail || '暂无详情'"></div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, Share } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getProductDetail } from '@/api/product'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { copyShareLink, getShareCard } from '@/utils/share'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const product = ref({})
const quantity = ref(1)
const activeTab = ref('detail')
const isFav = ref(false)
const currentImg = ref(0)

const productImages = computed(() => {
  const imgs = []
  if (product.value.mainImage) imgs.push(product.value.mainImage)
  return imgs.length ? imgs : ['/images/default-product.png']
})

onMounted(async () => {
  const productId = route.params.id
  if (!productId) { router.push('/product'); return }
  loading.value = true
  try {
    const res = await getProductDetail(productId)
    product.value = res.data
    if (userStore.isLoggedIn) {
      const favRes = await checkFavorite(productId)
      isFav.value = favRes.data === true
    }
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  try {
    await cartStore.addToCartAction(product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (e) { console.error(e) }
}

const handleBuyNow = () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  router.push({ path: '/order/create', query: { productId: product.value.id, quantity: quantity.value } })
}

const handleToggleFav = async () => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push('/login'); return }
  await toggleFavorite(product.value.id)
  isFav.value = !isFav.value
  ElMessage.success(isFav.value ? '已收藏' : '已取消收藏')
}

const handleShare = async () => {
  const card = getShareCard(product.value)
  await copyShareLink(product.value)
  ElMessage.success('链接已复制，快去分享给好友吧！')
}
</script>

<style scoped>
.product-detail-page {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

.breadcrumb {
  margin-bottom: 20px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.product-main {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.product-gallery {
  width: 400px;
  flex-shrink: 0;
}

.main-image {
  width: 400px;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-title {
  font-size: 22px;
  color: #303133;
  margin-bottom: 10px;
}

.product-desc {
  font-size: 14px;
  color: #909399;
  margin-bottom: 20px;
}

.price-box {
  background: #fff5f5;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.label {
  font-size: 14px;
  color: #909399;
  width: 50px;
}

.current-price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 14px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  font-size: 14px;
}

.value {
  color: #606266;
}

.quantity-row {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.buy-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

.cart-btn {
  flex: 1;
  height: 48px;
  font-size: 16px;
}

.product-detail-section {
  background: #fff;
  padding: 20px 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.detail-content {
  padding: 16px 0;
  line-height: 1.8;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
}
</style>
