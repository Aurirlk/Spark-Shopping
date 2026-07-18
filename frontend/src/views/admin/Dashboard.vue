<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日订单</span>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.todayOrders }}</div>
          <div class="stat-change" :class="stats.orderChange >= 0 ? 'up' : 'down'">
            {{ stats.orderChange >= 0 ? '+' : '' }}{{ stats.orderChange }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>今日销售额</span>
              <el-icon><Money /></el-icon>
            </div>
          </template>
          <div class="stat-value">¥{{ stats.todayRevenue }}</div>
          <div class="stat-change" :class="stats.revenueChange >= 0 ? 'up' : 'down'">
            {{ stats.revenueChange >= 0 ? '+' : '' }}{{ stats.revenueChange }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>新增用户</span>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.newUsers }}</div>
          <div class="stat-change" :class="stats.userChange >= 0 ? 'up' : 'down'">
            {{ stats.userChange >= 0 ? '+' : '' }}{{ stats.userChange }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>商品总数</span>
              <el-icon><Goods /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.totalProducts }}</div>
          <div class="stat-info">在售商品</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>销售趋势</span>
          </template>
          <div ref="salesChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div ref="orderChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card>
          <template #header><span>📈 销售额趋势（近7天）</span></template>
          <div ref="salesTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>👥 用户增长趋势（近7天）</span></template>
          <div ref="userTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>热销商品 Top 10</span>
          </template>
          <el-table :data="hotProducts" stripe>
            <el-table-column type="index" width="50" />
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="sales" label="销量" width="80" />
            <el-table-column prop="revenue" label="销售额" width="100">
              <template #default="{ row }">¥{{ row.revenue }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新订单</span>
          </template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="amount" label="金额" width="100">
              <template #default="{ row }">¥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const stats = ref({
  todayOrders: 156,
  orderChange: 12.5,
  todayRevenue: '28,560',
  revenueChange: 8.3,
  newUsers: 89,
  userChange: 15.2,
  totalProducts: 1234
})

const hotProducts = ref([
  { name: 'iPhone 14 Pro Max', sales: 156, revenue: '155,844' },
  { name: '华为 Mate 60 Pro', sales: 120, revenue: '83,880' },
  { name: 'AirPods Pro 2', sales: 300, revenue: '59,700' },
  { name: '联想小新Pro16', sales: 60, revenue: '35,940' },
  { name: 'MacBook Pro 14', sales: 40, revenue: '59,960' }
])

const recentOrders = ref([
  { orderNo: '20240115001', amount: '11,998', status: 3, statusText: '已完成', createTime: '2024-01-15 10:30' },
  { orderNo: '20240115002', amount: '1,999', status: 2, statusText: '待收货', createTime: '2024-01-15 14:20' },
  { orderNo: '20240116001', amount: '14,999', status: 1, statusText: '待发货', createTime: '2024-01-16 09:15' },
  { orderNo: '20240116002', amount: '699', status: 0, statusText: '待付款', createTime: '2024-01-16 11:30' }
])

const salesChart = ref(null)
const orderChart = ref(null)
const salesTrendChart = ref(null)
const userTrendChart = ref(null)

const getStatusType = (status) => {
  const types = ['info', 'warning', 'primary', 'success', 'danger']
  return types[status] || 'info'
}

onMounted(() => {
  initSalesChart()
  initOrderChart()
  initTrendCharts()
})

const initSalesChart = () => {
  const chart = echarts.init(salesChart.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: [150, 230, 224, 218, 135, 147, 260, 380, 350, 420, 380, 450],
        areaStyle: { opacity: 0.3 }
      }
    ]
  }
  chart.setOption(option)
}

const initOrderChart = () => {
  const chart = echarts.init(orderChart.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 10, name: '待付款' },
          { value: 20, name: '待发货' },
          { value: 30, name: '待收货' },
          { value: 35, name: '已完成' },
          { value: 5, name: '已取消' }
        ]
      }
    ]
  }
  chart.setOption(option)
}

const initTrendCharts = () => {
  const days = ['06-23','06-24','06-25','06-26','06-27','06-28','06-29']
  // 销售额趋势
  const sChart = echarts.init(salesTrendChart.value)
  sChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days, axisLabel: { color: '#999' } },
    yAxis: { type: 'value', axisLabel: { color: '#999' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{
      data: [820, 932, 901, 1290, 1330, 1120, 980], type: 'line', smooth: true,
      areaStyle: { opacity: 0.3 }, lineStyle: { color: '#409eff', width: 3 },
      itemStyle: { color: '#409eff' }
    }]
  })
  // 用户增长趋势
  const uChart = echarts.init(userTrendChart.value)
  uChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days, axisLabel: { color: '#999' } },
    yAxis: { type: 'value', axisLabel: { color: '#999' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{
      data: [120, 132, 145, 180, 165, 158, 172], type: 'bar', barWidth: '40%',
      itemStyle: { color: '#67c23a', borderRadius: [4,4,0,0] }
    }]
  })
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-change {
  font-size: 14px;
  margin-top: 8px;
}

.stat-change.up {
  color: #67c23a;
}

.stat-change.down {
  color: #f56c6c;
}

.stat-info {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}
</style>
