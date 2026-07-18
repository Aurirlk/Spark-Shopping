<template>
  <div class="coupon-page">
    <div class="container">
      <h1 class="page-title">领券中心</h1>
      <div v-if="coupons.length === 0" class="empty"><el-empty description="暂无可用优惠券" /></div>
      <div v-for="c in coupons" :key="c.id" class="coupon-card" :class="{ claimed: claimedIds.has(c.id) }">
        <div class="coupon-left">
          <span class="coupon-amount">¥{{ c.discountAmount }}</span>
          <span class="coupon-condition">满{{ c.conditionAmount }}可用</span>
        </div>
        <div class="coupon-body">
          <h3 class="coupon-name">{{ c.name }}</h3>
          <p class="coupon-date">{{ c.startTime?.slice(0,10) }} ~ {{ c.endTime?.slice(0,10) }}</p>
          <span class="coupon-stock">剩余 {{ c.stock }} 张</span>
        </div>
        <el-button :type="claimedIds.has(c.id) ? 'default' : 'danger'" :disabled="claimedIds.has(c.id)" @click="handleClaim(c.id)" size="large" round>
          {{ claimedIds.has(c.id) ? '已领取' : '立即领取' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableCoupons, claimCoupon, getMyCoupons } from '@/api/coupon'

const coupons = ref([])
const claimedIds = ref(new Set())

onMounted(async () => {
  const [avail, mine] = await Promise.all([getAvailableCoupons(), getMyCoupons()])
  coupons.value = avail.data || []
  ;(mine.data || []).forEach(c => claimedIds.value.add(c.id))
})

const handleClaim = async (id) => {
  const res = await claimCoupon(id)
  if (res.code === 200) {
    claimedIds.value.add(id)
    ElMessage.success('领取成功！')
  } else {
    ElMessage.error(res.message || '领取失败')
  }
}
</script>

<style scoped>
.coupon-page { padding: 20px 0; min-height: calc(100vh - 120px); background: #f5f7fa; }
.page-title { font-size: 22px; margin-bottom: 20px; }
.coupon-card { display: flex; align-items: center; background: #fff; border-radius: 12px; padding: 16px 20px; margin-bottom: 12px; box-shadow: 0 2px 8px rgba(0,0,0,.06); gap: 16px; }
.coupon-left { text-align: center; min-width: 100px; padding-right: 16px; border-right: 1px dashed #eee; }
.coupon-amount { font-size: 28px; font-weight: bold; color: #f56c6c; display: block; }
.coupon-condition { font-size: 12px; color: #999; }
.coupon-body { flex: 1; }
.coupon-name { font-size: 15px; margin-bottom: 4px; }
.coupon-date { font-size: 12px; color: #999; margin-bottom: 2px; }
.coupon-stock { font-size: 12px; color: #999; }
.claimed { opacity: .6; }
</style>
