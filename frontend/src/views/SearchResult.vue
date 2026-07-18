<template>
  <div class="search-page">
    <div class="container">
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索商品" size="large" clearable @keyup.enter="doSearch" :prefix-icon="Search" />
        <el-button type="primary" size="large" @click="doSearch">搜索</el-button>
      </div>

      <div v-if="!keyword && !loading" class="suggest-box">
        <h3>热门搜索</h3>
        <div class="hot-tags">
          <el-tag v-for="w in hotWords" :key="w" @click="keyword = w; doSearch()" class="hot-tag" effect="plain">{{ w }}</el-tag>
        </div>
        <h3 v-if="history.length">搜索历史</h3>
        <div class="history-list">
          <span v-for="(h, i) in history" :key="i" @click="keyword = h; doSearch()">{{ h }}</span>
          <el-button link size="small" type="danger" @click="history = []; localStorage.removeItem('searchHistory')">清空</el-button>
        </div>
      </div>

      <template v-if="keyword && !loading">
        <div class="result-meta">找到 <b>{{ total }}</b> 个商品</div>
        <SkeletonLoader v-if="loading" type="product-card" :count="8" />
        <div v-else-if="products.length" class="product-grid">
          <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push(`/product/${p.id}`)">
            <img :src="p.mainImage" :alt="p.name" class="p-img" />
            <div class="p-info">
              <h4 class="p-name">{{ p.name }}</h4>
              <div class="p-price"><span class="price">¥{{ p.price }}</span></div>
            </div>
          </div>
        </div>
        <el-empty v-else description="没有找到相关商品" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

const route = useRoute(); const router = useRouter()
const keyword = ref(route.query.keyword || '')
const products = ref([]); const loading = ref(false); const total = ref(0)
const hotWords = ['iPhone','华为','耳机','笔记本','空调','运动鞋','冰箱','洗衣机']
const history = ref(JSON.parse(localStorage.getItem('searchHistory') || '[]'))

const doSearch = async () => {
  const k = keyword.value.trim()
  if (!k) return
  loading.value = true
  history.value = [k, ...history.value.filter(h => h !== k)].slice(0, 10)
  localStorage.setItem('searchHistory', JSON.stringify(history.value))
  router.replace({ query: { keyword: k } })
  try {
    const res = await getProductList({ keyword: k, pageNum: 1, pageSize: 20 })
    products.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

onMounted(() => { if (keyword.value) doSearch() })
</script>

<style scoped>
.search-page { padding: 20px 0; min-height: calc(100vh - 120px); background: #f5f7fa; }
.search-bar { display: flex; gap: 12px; margin-bottom: 20px; }
.search-bar .el-input { flex: 1; }
.suggest-box { background: #fff; border-radius: 12px; padding: 20px; }
.suggest-box h3 { font-size: 16px; margin-bottom: 12px; color: #333; }
.hot-tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 20px; }
.hot-tag { cursor: pointer; }
.history-list { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; }
.history-list span { cursor: pointer; color: #666; }
.history-list span:hover { color: #409eff; }
.result-meta { margin-bottom: 16px; font-size: 14px; color: #999; }
.product-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; }
.product-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all .2s; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.product-card:hover { transform: translateY(-4px); }
.p-img { width: 100%; height: 180px; object-fit: cover; }
.p-info { padding: 10px; }
.p-name { font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 6px; }
.price { font-size: 16px; font-weight: bold; color: #f56c6c; }
@media (max-width: 576px) { .product-grid { grid-template-columns: repeat(2,1fr); gap: 8px; } }
</style>
