<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo">
            <router-link to="/">
              <span class="logo-icon">🛒</span>
              <span class="logo-text">购物商城</span>
            </router-link>
          </div>

          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品"
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-button @click="handleSearch">搜索</el-button>
              </template>
            </el-input>
          </div>

          <div class="header-actions">
            <router-link to="/cart" class="action-item">
              <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0" :max="99">
                <el-icon :size="20"><ShoppingCart /></el-icon>
              </el-badge>
              <span>购物车</span>
            </router-link>

            <template v-if="userStore.isLoggedIn">
              <el-dropdown trigger="click">
                <div class="action-item">
                  <el-avatar :size="28" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
                  <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/user')">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/order/list')">我的订单</el-dropdown-item>
                    <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <router-link to="/login" class="action-item">登录</router-link>
              <router-link to="/register" class="action-item register-btn">注册</router-link>
            </template>
          </div>
        </div>
      </div>
    </header>

    <!-- 分类导航 -->
    <nav class="category-nav">
      <div class="container">
        <ul class="category-list">
          <li><router-link to="/product">全部商品</router-link></li>
          <li v-for="category in categoryList" :key="category.id">
            <router-link :to="`/product?categoryId=${category.id}`">{{ category.name }}</router-link>
          </li>
        </ul>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <div class="container">
        <!-- 轮播图 -->
        <section class="banner-section">
          <el-carousel :height="bannerHeight" :interval="5000">
            <el-carousel-item v-for="banner in banners" :key="banner.id">
              <div class="banner-item" :style="{ background: banner.bg }">
                <div class="banner-content">
                  <h2>{{ banner.title }}</h2>
                  <p>{{ banner.desc }}</p>
                  <el-button type="primary" size="large" @click="$router.push('/product')">立即选购</el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </section>

        <!-- 功能入口 -->
        <section class="features-section">
          <div class="feature-item">
            <el-icon :size="24" color="#409eff"><Van /></el-icon>
            <div>
              <h4>全场包邮</h4>
              <p>满99元免运费</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#67c23a"><CircleCheckFilled /></el-icon>
            <div>
              <h4>正品保障</h4>
              <p>100%正品保证</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#e6a23c"><Service /></el-icon>
            <div>
              <h4>售后无忧</h4>
              <p>7天无理由退换</p>
            </div>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#f56c6c"><Timer /></el-icon>
            <div>
              <h4>闪电发货</h4>
              <p>24小时内发货</p>
            </div>
          </div>
          <div class="feature-item" style="cursor:pointer" @click="$router.push('/seckill')">
            <el-icon :size="24" color="#ff4757"><Lightning /></el-icon>
            <div>
              <h4>限时秒杀</h4>
              <p>超低价抢购</p>
          </div>
          </div>
        </section>

        <!-- 推荐商品 -->
        <section class="section">
          <div class="section-header">
            <h2>热销推荐</h2>
            <router-link to="/product" class="more-link">查看全部 <el-icon><ArrowRight /></el-icon></router-link>
          </div>

          <div class="product-grid">
            <div
              v-for="product in recommendProducts"
              :key="product.id"
              class="product-card"
              @click="$router.push(`/product/${product.id}`)"
            >
              <div class="product-image">
                <img :src="product.mainImage" :alt="product.name">
                <div v-if="product.originalPrice" class="product-badge">
                  {{ Math.round((product.price / product.originalPrice) * 10) / 10 }}折                </div>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-footer">
                  <div class="product-price">
                    <span class="current-price">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                  </div>
                  <span class="sales">已售 {{ product.sales }}+</span>
                </div>
                <el-button type="primary" class="add-cart-btn" @click.stop="handleAddToCart(product)">
                  加入购物车?                </el-button>
              </div>
            </div>
          </div>
        </section>

        <!-- 分类宫格 -->
        <CategoryGrid :categories="categoryGridItems" />

        <!-- 限时秒杀 -->
        <FlashSale />

        <!-- 推荐商品 -->
        <section class="section">
          <div class="section-header">
            <h2>🔥 热销推荐</h2>
            <router-link to="/product" class="more-link">查看全部 <el-icon><ArrowRight /></el-icon></router-link>
          </div>

          <div class="product-grid">
            <div
              v-for="(product, index) in recommendProducts"
              :key="product.id"
              class="product-card product-card-animate"
              :style="{ animationDelay: `${index * 0.05}s` }"
              @click="$router.push(`/product/${product.id}`)"
            >
              <div class="product-image shimmer">
                <img :src="product.mainImage" :alt="product.name">
                <div v-if="product.originalPrice" class="product-badge">
                  {{ Math.round((1 - product.price / product.originalPrice) * 100) }}% OFF
                </div>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-footer">
                  <div class="product-price">
                    <span class="current-price price-beat">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
                  </div>
                  <span class="sales">已售 {{ product.sales }}+</span>
                </div>
                <el-button type="primary" class="add-cart-btn" @click.stop="handleAddToCart(product)">
                  加入购物车?                </el-button>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>

    <SiteFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCategoryList } from '@/api/category'
import { getRecommendProducts } from '@/api/product'
import { Search, ShoppingCart, Van, CircleCheckFilled, Service, Timer, Lightning, ArrowRight } from '@element-plus/icons-vue'
import FlashSale from '@/components/FlashSale.vue'
import CategoryGrid from '@/components/CategoryGrid.vue'
import SiteFooter from '@/components/SiteFooter.vue'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')
const categoryList = ref([])
const recommendProducts = ref([])

const categoryGridItems = [
  { id: 6, name: '手机数码', icon: 'Phone', color: '#409eff' },
  { id: 8, name: '电脑办公', icon: 'Notebook', color: '#67c23a' },
  { id: 10, name: '家用电器', icon: 'Monitor', color: '#e6a23c' },
  { id: 7, name: '耳机音箱', icon: 'Headset', color: '#9b59b6' },
  { id: 4, name: '服装鞋包', icon: 'Watch', color: '#f56c6c' },
  { id: 5, name: '食品生鲜', icon: 'MilkTea', color: '#909399' },
  { id: 1, name: '图书文具', icon: 'Edit', color: '#1abc9c' },
  { id: 2, name: '运动户外', icon: 'Basketball', color: '#e74c3c' },
]

const banners = [
  { id: 1, title: '数码狂欢', desc: '精选数码好物，限时特惠低至5折', bg: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)' },
  { id: 2, title: '品质生活', desc: '家电焕新，满减优惠等你来', bg: 'linear-gradient(135deg, #2196F3 0%, #1976D2 100%)' },
  { id: 3, title: '年终大促', desc: '全场商品低至3折，错过再等一年', bg: 'linear-gradient(135deg, #0D47A1 0%, #1565C0 100%)' }
]

const windowWidth = ref(window.innerWidth)
const bannerHeight = computed(() => windowWidth.value < 768 ? '220px' : '400px')

const updateWidth = () => { windowWidth.value = window.innerWidth }

onMounted(async () => {
  window.addEventListener('resize', updateWidth)
  try {
    const [categoryRes, recommendRes] = await Promise.all([
      getCategoryList(),
      getRecommendProducts(8)
    ])
    categoryList.value = categoryRes.data || []
    recommendProducts.value = recommendRes.data || []
  } catch (error) {
        console.error('加载数据失败', error)
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', updateWidth)
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/product', query: { keyword: searchKeyword.value } })
  }
}

const handleAddToCart = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addToCartAction(product.id, 1)
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败：', error)
  }
}

const handleLogout = async () => {
  await userStore.logoutAction()
    ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>@import '@/assets/home.css';</style>
