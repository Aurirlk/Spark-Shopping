<template>
  <div class="skeleton-wrap">
    <!-- 商品卡片骨架 -->
    <div v-if="type === 'product-card'" class="skeleton-card" v-for="i in count" :key="i">
      <div class="sk-img pulse" />
      <div class="sk-body">
        <div class="sk-line w-80 pulse" />
        <div class="sk-line w-60 pulse" />
        <div class="sk-line w-40 pulse" />
      </div>
    </div>

    <!-- 商品列表骨架 -->
    <div v-if="type === 'product-list'" class="skeleton-list">
      <div class="sk-row" v-for="i in count" :key="i">
        <div class="sk-img-sm pulse" />
        <div class="sk-body">
          <div class="sk-line w-60 pulse" />
          <div class="sk-line w-40 pulse" />
          <div class="sk-line w-30 pulse" />
        </div>
      </div>
    </div>

    <!-- 详情页骨架 -->
    <div v-if="type === 'detail'" class="skeleton-detail">
      <div class="sk-img-lg pulse" />
      <div class="sk-body">
        <div class="sk-line w-80 pulse" />
        <div class="sk-line w-60 pulse" />
        <div class="sk-line w-90 pulse" />
        <div class="sk-line w-40 pulse" />
      </div>
    </div>

    <!-- 订单骨架 -->
    <div v-if="type === 'order'" class="skeleton-order" v-for="i in count" :key="i">
      <div class="sk-header pulse" />
      <div class="sk-row">
        <div class="sk-img-sm pulse" />
        <div class="sk-body">
          <div class="sk-line w-50 pulse" />
          <div class="sk-line w-30 pulse" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'product-card' },
  count: { type: Number, default: 4 }
})
</script>

<style scoped>
.skeleton-wrap { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 16px; }
.skeleton-list, .skeleton-order { display: flex; flex-direction: column; gap: 12px; }
.skeleton-card, .skeleton-detail, .skeleton-order { background: #fff; border-radius: 12px; overflow: hidden; }
.skeleton-card .sk-img { height: 180px; background: #eee; }
.skeleton-card .sk-body, .skeleton-detail .sk-body { padding: 14px; display: flex; flex-direction: column; gap: 10px; }
.sk-img-sm { width: 70px; height: 70px; border-radius: 8px; background: #eee; flex-shrink: 0; }
.sk-img-lg { width: 100%; height: 300px; background: #eee; }
.sk-header { height: 40px; background: #eee; margin-bottom: 8px; }
.sk-row { display: flex; align-items: center; gap: 12px; background: #fff; border-radius: 8px; padding: 12px; }
.sk-body { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.sk-line { height: 14px; border-radius: 6px; background: #eee; }
.w-80 { width: 80%; } .w-60 { width: 60%; } .w-50 { width: 50%; }
.w-40 { width: 40%; } .w-30 { width: 30%; } .w-90 { width: 90%; }
.pulse { animation: pulse 1.5s ease-in-out infinite; }
</style>
