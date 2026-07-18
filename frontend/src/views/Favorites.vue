<template>
  <div class="favorites-page">
    <div class="container">
      <h1 class="page-title">我的收藏</h1>
      <template v-if="loading"><SkeletonLoader type="product-card" :count="8" /></template>
      <template v-else-if="products.length > 0">
        <div class="fav-grid">
          <div v-for="p in products" :key="p.id" class="fav-card" @click="$router.push(`/product/${p.id}`)">
            <img :src="p.mainImage" :alt="p.name" class="fav-img" />
            <div class="fav-body">
              <h3 class="fav-name">{{ p.name }}</h3>
              <div class="fav-price"><span class="price">¥{{ p.price }}</span></div>
            </div>
            <el-button :icon="Delete" circle size="small" class="fav-del" @click.stop="remove(p.id)" />
          </div>
        </div>
      </template>
      <el-empty v-else description="还没有收藏商品" :image-size="120">
        <el-button type="primary" @click="$router.push('/product')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getFavoriteList, toggleFavorite } from '@/api/favorite'
import { getProductDetail } from '@/api/product'
import SkeletonLoader from '@/components/SkeletonLoader.vue'

const loading = ref(true)
const products = ref([])

onMounted(async () => {
  try {
    const ids = await getFavoriteList()
    const data = await Promise.all((ids.data || []).map(id => getProductDetail(id)))
    products.value = data.map(r => r.data || {}).filter(Boolean)
  } catch (e) { console.error(e) }
  finally { loading.value = false }
})

const remove = async (id) => {
  await toggleFavorite(id)
  products.value = products.value.filter(p => p.id !== id)
  ElMessage.success('已取消收藏')
}
</script>

<style scoped>
.favorites-page { padding: 20px 0; background: #f5f7fa; min-height: calc(100vh - 120px); }
.page-title { font-size: 22px; color: #303133; margin-bottom: 20px; }
.fav-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px,1fr)); gap: 16px; }
.fav-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; position: relative; transition: transform .2s; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.fav-card:hover { transform: translateY(-4px); }
.fav-img { width: 100%; height: 180px; object-fit: cover; }
.fav-body { padding: 10px; }
.fav-name { font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 6px; }
.price { font-size: 16px; font-weight: bold; color: #f56c6c; }
.fav-del { position: absolute; top: 8px; right: 8px; }
@media (max-width: 576px) { .fav-grid { grid-template-columns: repeat(2,1fr); gap: 8px; } }
</style>
