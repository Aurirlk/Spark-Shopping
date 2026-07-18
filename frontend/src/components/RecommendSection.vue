<template>
  <div class="recommend-section">
    <!-- 个性化推荐 -->
    <div v-if="showPersonalized && personalizedProducts.length > 0" class="recommend-block">
      <div class="recommend-header">
        <h3>
          <el-icon><Star /></el-icon>
          为您推荐
        </h3>
        <el-button text @click="refreshPersonalized">
          <el-icon><Refresh /></el-icon>
          换一批
        </el-button>
      </div>
      <div class="recommend-list">
        <div
          v-for="product in personalizedProducts"
          :key="product.id"
          class="recommend-item"
          @click="goToProduct(product.id)"
        >
          <div class="item-image">
            <img :src="product.mainImage || '/images/default-product.png'" :alt="product.name" />
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ product.name }}</h4>
            <div class="item-price">
              <span class="current-price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="item-reason" v-if="product.reason">
              <el-tag size="small" type="info">{{ product.reason }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 相似商品推荐 -->
    <div v-if="showSimilar && similarProducts.length > 0" class="recommend-block">
      <div class="recommend-header">
        <h3>
          <el-icon><Goods /></el-icon>
          相似商品
        </h3>
      </div>
      <div class="recommend-list">
        <div
          v-for="product in similarProducts"
          :key="product.id"
          class="recommend-item"
          @click="goToProduct(product.id)"
        >
          <div class="item-image">
            <img :src="product.mainImage || '/images/default-product.png'" :alt="product.name" />
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ product.name }}</h4>
            <div class="item-price">
              <span class="current-price">¥{{ product.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 热门推荐 -->
    <div v-if="showHot && hotProducts.length > 0" class="recommend-block">
      <div class="recommend-header">
        <h3>
          <el-icon><TrendCharts /></el-icon>
          热门推荐
        </h3>
      </div>
      <div class="recommend-list">
        <div
          v-for="(product, index) in hotProducts"
          :key="product.id"
          class="recommend-item"
          @click="goToProduct(product.id)"
        >
          <div class="item-rank" :class="{ 'top-3': index < 3 }">{{ index + 1 }}</div>
          <div class="item-image">
            <img :src="product.mainImage || '/images/default-product.png'" :alt="product.name" />
          </div>
          <div class="item-info">
            <h4 class="item-name">{{ product.name }}</h4>
            <div class="item-price">
              <span class="current-price">¥{{ product.price }}</span>
            </div>
            <div class="item-sales">已售 {{ product.sales }} 件</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Star, Refresh, Goods, TrendCharts } from '@element-plus/icons-vue'
import {
  getUserRecommendations,
  getSimilarProducts,
  getHotProducts,
  getRecommendationReason,
  recordBehavior,
  BEHAVIOR_TYPES
} from '@/api/recommend'

const props = defineProps({
  // 商品ID（用于相似商品推荐）
  productId: {
    type: Number,
    default: null
  },
  // 分类ID（用于热门推荐）
  categoryId: {
    type: Number,
    default: null
  },
  // 显示个性化推荐
  showPersonalized: {
    type: Boolean,
    default: true
  },
  // 显示相似商品
  showSimilar: {
    type: Boolean,
    default: false
  },
  // 显示热门推荐
  showHot: {
    type: Boolean,
    default: true
  },
  // 推荐数量
  limit: {
    type: Number,
    default: 6
  }
})

const router = useRouter()

// 推荐商品数据
const personalizedProducts = ref([])
const similarProducts = ref([])
const hotProducts = ref([])

// 加载状态
const loading = ref({
  personalized: false,
  similar: false,
  hot: false
})

// 获取个性化推荐
const fetchPersonalized = async () => {
  loading.value.personalized = true
  try {
    const res = await getUserRecommendations(props.limit)
    if (res.data.code === 200) {
      const productIds = res.data.data
      // 获取商品详情（实际应该批量查询）
      personalizedProducts.value = productIds.map(id => ({
        id,
        name: `商品${id}`,
        price: (Math.random() * 1000).toFixed(2),
        mainImage: '/images/default-product.png'
      }))

      // 获取推荐理由
      for (let product of personalizedProducts.value) {
        try {
          const reasonRes = await getRecommendationReason(product.id)
          if (reasonRes.data.code === 200) {
            product.reason = reasonRes.data.data
          }
        } catch (e) {
          // 忽略推荐理由错误
        }
      }
    }
  } catch (error) {
    console.error('获取个性化推荐失败:', error)
  } finally {
    loading.value.personalized = false
  }
}

// 获取相似商品
const fetchSimilar = async () => {
  if (!props.productId) return

  loading.value.similar = true
  try {
    const res = await getSimilarProducts(props.productId, props.limit)
    if (res.data.code === 200) {
      const productIds = res.data.data
      similarProducts.value = productIds.map(id => ({
        id,
        name: `商品${id}`,
        price: (Math.random() * 1000).toFixed(2),
        mainImage: '/images/default-product.png'
      }))
    }
  } catch (error) {
    console.error('获取相似商品失败:', error)
  } finally {
    loading.value.similar = false
  }
}

// 获取热门商品
const fetchHot = async () => {
  loading.value.hot = true
  try {
    const res = await getHotProducts(props.categoryId, props.limit)
    if (res.data.code === 200) {
      const productIds = res.data.data
      hotProducts.value = productIds.map(id => ({
        id,
        name: `商品${id}`,
        price: (Math.random() * 1000).toFixed(2),
        sales: Math.floor(Math.random() * 1000),
        mainImage: '/images/default-product.png'
      }))
    }
  } catch (error) {
    console.error('获取热门商品失败:', error)
  } finally {
    loading.value.hot = false
  }
}

// 刷新个性化推荐
const refreshPersonalized = () => {
  fetchPersonalized()
}

// 跳转商品详情
const goToProduct = (productId) => {
  // 记录浏览行为
  recordBehavior(productId, BEHAVIOR_TYPES.VIEW)
  router.push(`/product/${productId}`)
}

// 监听商品ID变化
watch(() => props.productId, (newVal) => {
  if (newVal && props.showSimilar) {
    fetchSimilar()
  }
})

// 初始化
onMounted(() => {
  if (props.showPersonalized) {
    fetchPersonalized()
  }
  if (props.showSimilar && props.productId) {
    fetchSimilar()
  }
  if (props.showHot) {
    fetchHot()
  }
})
</script>

<style scoped>
.recommend-section {
  padding: 20px 0;
}

.recommend-block {
  margin-bottom: 30px;
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.recommend-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.recommend-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
}

.recommend-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.item-rank {
  position: absolute;
  top: 8px;
  left: 8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #999;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  z-index: 1;
}

.item-rank.top-3 {
  background: linear-gradient(135deg, #ff6b6b, #ff8e53);
}

.item-image {
  width: 100%;
  padding-top: 100%;
  position: relative;
  overflow: hidden;
}

.item-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.recommend-item:hover .item-image img {
  transform: scale(1.05);
}

.item-info {
  padding: 12px;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 16px;
  font-weight: bold;
  color: #ff4757;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.item-reason {
  margin-top: 8px;
}

.item-sales {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
