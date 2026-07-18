<template>
  <section class="cate-section">
    <div class="section-header">
      <h3>商品分类</h3>
      <router-link to="/product" class="more-link">全部分类 ›</router-link>
    </div>
    <div class="cate-grid">
      <div
        v-for="cat in categories"
        :key="cat.id"
        class="cate-item"
        @click="$router.push(`/product?categoryId=${cat.id}`)"
      >
        <div class="cate-icon">
          <el-icon :size="28" :color="cat.color"><component :is="cat.icon" /></el-icon>
        </div>
        <span class="cate-name">{{ cat.name }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
import { Phone, Notebook, Monitor, MilkTea, Watch, Basketball, Headset, Edit } from '@element-plus/icons-vue'

defineProps({
  categories: {
    type: Array,
    default: () => [
      { id: 6, name: '手机数码', icon: 'Phone', color: '#409eff' },
      { id: 8, name: '电脑办公', icon: 'Notebook', color: '#67c23a' },
      { id: 10, name: '家用电器', icon: 'Monitor', color: '#e6a23c' },
      { id: 4, name: '服装鞋包', icon: 'Watch', color: '#f56c6c' },
      { id: 5, name: '食品生鲜', icon: 'MilkTea', color: '#909399' },
      { id: 7, name: '耳机音箱', icon: 'Headset', color: '#9b59b6' },
      { id: 0, name: '图书文具', icon: 'Edit', color: '#1abc9c' },
      { id: 0, name: '运动户外', icon: 'Basketball', color: '#e74c3c' },
    ]
  }
})
</script>

<style scoped>
.cate-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.more-link {
  font-size: 13px;
  color: #409eff;
  text-decoration: none;
}

.cate-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.cate-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 0;
  border-radius: 8px;
  transition: all 0.2s;
}

.cate-item:hover {
  background: #f5f7fa;
  transform: translateY(-2px);
}

.cate-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.cate-item:hover .cate-icon {
  transform: scale(1.1);
}

.cate-name {
  font-size: 12px;
  color: #666;
}

@media (max-width: 576px) {
  .cate-grid {
    gap: 8px;
  }
  .cate-icon {
    width: 44px;
    height: 44px;
  }
}
</style>
