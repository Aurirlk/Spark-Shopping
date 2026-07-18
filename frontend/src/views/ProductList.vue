<template>
  <div class="product-list-page">
    <div class="container">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>商品列表</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 分类筛选 -->
      <div class="filter-section">
        <div class="filter-group">
          <span class="filter-label">分类：</span>
          <div class="filter-tags">
            <el-tag :type="!categoryId ? '' : 'info'" @click="handleCategoryChange(null)">全部</el-tag>
            <el-tag
              v-for="category in categoryList"
              :key="category.id"
              :type="categoryId === category.id ? '' : 'info'"
              @click="handleCategoryChange(category.id)"
            >
              {{ category.name }}
            </el-tag>
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-container">
        <SkeletonLoader type="product-card" :count="8" />
      </div>

      <template v-else>
        <div v-if="productList.length > 0" class="product-grid">
          <div
            v-for="product in productList"
            :key="product.id"
            class="product-card"
            @click="$router.push(`/product/${product.id}`)"
          >
            <div class="product-image">
              <img :src="product.mainImage" :alt="product.name">
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-price">
                <span class="current-price">¥{{ product.price }}</span>
                <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
              </div>
              <div class="product-meta">
                <span class="sales">已售 {{ product.sales }}+</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <el-empty description="暂无商品" />
        </div>

        <div v-if="total > 0" class="load-more">
          <el-button v-if="!finished" :loading="loadingMore" @click="loadMore" class="load-btn">
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </el-button>
          <span v-else class="finished-text">— 已加载全部 {{ total }} 件商品 —</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getCategoryList } from '@/api/category'
import { getProductList } from '@/api/product'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

const route = useRoute()

const loading = ref(false)
const loadingMore = ref(false)
const productList = ref([])
const categoryList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const categoryId = ref(null)
const keyword = ref('')
const finished = ref(false)

onMounted(async () => {
  keyword.value = route.query.keyword || ''
  categoryId.value = route.query.categoryId ? Number(route.query.categoryId) : null
  await loadData()
  window.addEventListener('scroll', onScroll)
})

onUnmounted(() => window.removeEventListener('scroll', onScroll))

const onScroll = () => {
  if (loadingMore.value || finished.value) return
  const nearBottom = window.innerHeight + window.scrollY >= document.body.offsetHeight - 400
  if (nearBottom) loadMore()
}

watch(() => route.query, (newQuery) => {
  keyword.value = newQuery.keyword || ''
  categoryId.value = newQuery.categoryId ? Number(newQuery.categoryId) : null
  pageNum.value = 1; finished.value = false
  loadData()
})

const loadData = async () => {
  loading.value = true; pageNum.value = 1
  try {
    const [catRes, prodRes] = await Promise.all([
      getCategoryList(),
      getProductList({ categoryId: categoryId.value, keyword: keyword.value, pageNum: 1, pageSize: pageSize.value })
    ])
    categoryList.value = catRes.data || []
    productList.value = prodRes.data?.records || []
    total.value = prodRes.data?.total || 0
    finished.value = (prodRes.data?.records || []).length < pageSize.value
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const loadMore = async () => {
  if (loadingMore.value || finished.value) return
  loadingMore.value = true; pageNum.value++
  try {
    const res = await getProductList({ categoryId: categoryId.value, keyword: keyword.value, pageNum: pageNum.value, pageSize: pageSize.value })
    const records = res.data?.records || []
    productList.value.push(...records)
    total.value = res.data?.total || 0
    if (records.length < pageSize.value) finished.value = true
  } catch (e) { console.error(e) }
  finally { loadingMore.value = false }
}

const handleCategoryChange = (id) => {
  categoryId.value = id; pageNum.value = 1; finished.value = false
  loadData()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.product-list-page {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
  box-sizing: border-box;
}

@media (max-width: 768px) {
  .container {
    padding: 0 12px;
  }
}

.breadcrumb {
  margin-bottom: 20px;
}

.filter-section {
  background: #fff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tags .el-tag {
  cursor: pointer;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

@media (max-width: 992px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); gap: 16px; }
}

@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 10px; }
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  height: 200px;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 14px;
}

.product-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  margin-bottom: 6px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.original-price {
  font-size: 12px;
  color: #c0c4cc;
  text-decoration: line-through;
  margin-left: 6px;
}

.product-meta {
  font-size: 12px;
  color: #909399;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: #fff;
  border-radius: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}

.load-more { text-align: center; padding: 30px 0; }
.load-btn { width: 240px; border-radius: 20px; }
.finished-text { color: #999; font-size: 13px; }
</style>
