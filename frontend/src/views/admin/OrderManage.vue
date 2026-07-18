<template>
  <div class="order-manage">
    <el-card>
      <template #header>
        <span>订单管理</span>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单表格 -->
      <el-table :data="orderList" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="联系电话" width="120" />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 1"
              type="success"
              link
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span class="price">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>商品信息</el-divider>

      <el-table :data="currentOrder.items" stripe>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="小计" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, shipOrder, completeOrder, cancelOrderAdmin } from '@/api/admin'

const loading = ref(false)
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  orderNo: '',
  status: null
})

const detailVisible = ref(false)
const currentOrder = ref({})

const getStatusType = (status) => {
  const types = ['info', 'warning', 'primary', 'success', 'danger']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待付款', '待发货', '待收货', '已完成', '已取消']
  return texts[status] || '未知'
}

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getAdminOrderList({
      status: searchForm.value.status ?? undefined,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    const list = res.data.records || []
    orderList.value = list.map(o => ({
      ...o,
      statusText: getStatusText(o.status)
    }))
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载订单失败', e)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

const handleReset = () => {
  searchForm.value = { orderNo: '', status: null }
  handleSearch()
}

const handleDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleShip = async (row) => {
  await ElMessageBox.confirm('确定要发货吗？', '提示')
  try {
    const res = await shipOrder(row.id)
    ElMessage.success(res.message || '发货成功')
    loadOrders()
  } catch (e) {
    console.error('发货失败', e)
  }
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('确定要取消该订单吗？', '提示')
  try {
    const res = await cancelOrderAdmin(row.id)
    ElMessage.success(res.message || '取消成功')
    loadOrders()
  } catch (e) {
    console.error('取消失败', e)
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadOrders()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadOrders()
}
</script>

<style scoped>
.order-manage {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
