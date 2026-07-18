<template>
  <div class="activity-mgr">
    <el-tabs v-model="tab">
      <el-tab-pane label="秒杀管理" name="seckill">
        <el-button type="primary" @click="showSeckillDialog = true" style="margin-bottom:16px">+ 新增秒杀</el-button>
        <el-table :data="seckillList" stripe>
          <el-table-column prop="product_name" label="商品" />
          <el-table-column prop="seckill_price" label="秒杀价" />
          <el-table-column prop="stock" label="库存" />
          <el-table-column prop="start_time" label="开始时间" />
          <el-table-column prop="end_time" label="结束时间" />
          <el-table-column label="操作">
            <template #default="{ row }"><el-button type="danger" link @click="delSeckill(row.id)">关闭</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="优惠券管理" name="coupon">
        <el-button type="primary" @click="showCouponDialog = true" style="margin-bottom:16px">+ 新增优惠券</el-button>
        <el-table :data="couponList" stripe>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="condition_amount" label="满减条件" />
          <el-table-column prop="discount_amount" label="减免金额" />
          <el-table-column prop="stock" label="库存" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增秒杀对话框 -->
    <el-dialog v-model="showSeckillDialog" title="新增秒杀" width="500px">
      <el-form :model="secForm" label-width="100px">
        <el-form-item label="商品ID"><el-input v-model="secForm.productId" /></el-form-item>
        <el-form-item label="秒杀价"><el-input-number v-model="secForm.seckillPrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="secForm.stock" :min="1" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="saveSeckill">确定</el-button></template>
    </el-dialog>

    <!-- 新增优惠券对话框 -->
    <el-dialog v-model="showCouponDialog" title="新增优惠券" width="500px">
      <el-form :model="cpForm" label-width="100px">
        <el-form-item label="名称"><el-input v-model="cpForm.name" /></el-form-item>
        <el-form-item label="满减条件"><el-input-number v-model="cpForm.conditionAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="减免"><el-input-number v-model="cpForm.discountAmount" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="cpForm.stock" :min="1" /></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="saveCoupon">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tab = ref('seckill')
const seckillList = ref([])
const couponList = ref([])
const showSeckillDialog = ref(false)
const showCouponDialog = ref(false)
const secForm = ref({ productId: '', seckillPrice: 0, stock: 10 })
const cpForm = ref({ name: '', conditionAmount: 0, discountAmount: 0, stock: 100 })

onMounted(() => { loadData() })
const loadData = async () => {
  const [s, c] = await Promise.all([axios.get('/api/admin/activity/seckill'), axios.get('/api/admin/activity/coupons')])
  seckillList.value = s.data.data || []
  couponList.value = c.data.data || []
}
const saveSeckill = async () => {
  await axios.post('/api/admin/activity/seckill', secForm.value)
  ElMessage.success('创建成功'); showSeckillDialog.value = false; loadData()
}
const delSeckill = async (id) => {
  await axios.delete(`/api/admin/activity/seckill/${id}`)
  ElMessage.success('已关闭'); loadData()
}
const saveCoupon = async () => {
  await axios.post('/api/admin/activity/coupons', cpForm.value)
  ElMessage.success('创建成功'); showCouponDialog.value = false; loadData()
}
</script>
