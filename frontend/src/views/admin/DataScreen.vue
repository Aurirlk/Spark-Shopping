<template>
  <div class="data-screen">
    <!-- 顶部标题栏 -->
    <header class="screen-header">
      <h1>📊 电商数据分析大屏</h1>
      <div class="header-right">
        <span class="current-time">{{ currentTime }}</span>
        <el-button type="primary" size="small" @click="refreshData">🔄 刷新数据</el-button>
      </div>
    </header>

    <!-- 核心指标卡片 -->
    <el-row :gutter="16" class="metric-cards">
      <el-col :span="4" v-for="card in metricCards" :key="card.title">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" :style="{ background: card.color }">
            <el-icon :size="24"><component :is="card.icon" /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ card.value }}</div>
            <div class="metric-title">{{ card.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <!-- 左侧：月度销售趋势 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>📈 月度销售趋势</span>
          </template>
          <div ref="monthlyTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 右侧：品类销售占比 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>🥧 品类销售占比</span>
          </template>
          <div ref="categoryPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <!-- 左侧：热销商品Top10 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>🏆 热销商品 Top 10</span>
          </template>
          <div ref="hotProductsChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 右侧：转化漏斗 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>🔻 用户转化漏斗</span>
          </template>
          <div ref="funnelChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：用户价值分层 -->
    <el-row :gutter="16">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <span>👥 用户价值分层</span>
          </template>
          <div ref="userValueChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

// 当前时间
const currentTime = ref('')
let timeInterval = null

// 核心指标卡片
const metricCards = ref([
  { title: '总订单数', value: '加载中...', icon: 'Document', color: '#409eff' },
  { title: '总销售额', value: '加载中...', icon: 'Money', color: '#67c23a' },
  { title: '用户总数', value: '加载中...', icon: 'User', color: '#e6a23c' },
  { title: '商品总数', value: '加载中...', icon: 'Goods', color: '#f56c6c' },
  { title: '转化率', value: '加载中...', icon: 'TrendCharts', color: '#909399' },
])

// 图表引用
const monthlyTrendChart = ref(null)
const categoryPieChart = ref(null)
const hotProductsChart = ref(null)
const funnelChart = ref(null)
const userValueChart = ref(null)

// 图表实例
let charts = []

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  initCharts()
  fetchMetrics()
  loadData()
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  charts.forEach(chart => chart.dispose())
})

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN')
}

const refreshData = () => {
  fetchMetrics()
  loadData()
}

const initCharts = () => {
  charts.push(echarts.init(monthlyTrendChart.value))
  charts.push(echarts.init(categoryPieChart.value))
  charts.push(echarts.init(hotProductsChart.value))
  charts.push(echarts.init(funnelChart.value))
  charts.push(echarts.init(userValueChart.value))
}

// 获取核心指标
const fetchMetrics = async () => {
  try {
    const res = await axios.get('/api/admin/stats/summary')
    if (res.data.code === 200) {
      const d = res.data.data
      metricCards.value[0].value = (d.totalOrders || 0).toLocaleString()
      metricCards.value[1].value = '¥' + (d.totalRevenue || 0).toLocaleString()
      metricCards.value[2].value = (d.totalUsers || 0).toLocaleString()
      metricCards.value[3].value = (d.totalProducts || 0).toLocaleString()
      metricCards.value[4].value = d.monthOrders ? ((d.monthOrders / Math.max(d.totalOrders, 1)) * 100).toFixed(1) + '%' : '0%'
    }
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
}

const loadData = () => {
  // 月度销售趋势
  charts[0]?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单量'], textStyle: { color: '#aaa' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      axisLabel: { color: '#aaa' }
    },
    yAxis: [
      { type: 'value', name: '销售额(万)', nameTextStyle: { color: '#aaa' }, axisLabel: { color: '#aaa' } },
      { type: 'value', name: '订单量', nameTextStyle: { color: '#aaa' }, axisLabel: { color: '#aaa' } }
    ],
    series: [
      {
        name: '销售额', type: 'line', smooth: true,
        data: [150, 230, 224, 218, 135, 147, 260, 380, 350, 420, 380, 450],
        areaStyle: { opacity: 0.3 },
        lineStyle: { color: '#00d4ff' },
        itemStyle: { color: '#00d4ff' }
      },
      {
        name: '订单量', type: 'bar', yAxisIndex: 1,
        data: [120, 180, 175, 168, 105, 115, 200, 290, 270, 320, 290, 345],
        itemStyle: { color: '#ff6b6b', borderRadius: [4, 4, 0, 0] }
      }
    ]
  })

  // 品类销售占比
  charts[1]?.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left', textStyle: { color: '#aaa' } },
    series: [{
      name: '品类销售', type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      label: { color: '#aaa' },
      data: [
        { value: 350, name: '手机数码' },
        { value: 280, name: '电脑办公' },
        { value: 220, name: '家用电器' },
        { value: 180, name: '服装鞋包' },
        { value: 120, name: '食品生鲜' }
      ],
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  })

  // 热销商品Top10
  charts[2]?.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', axisLabel: { color: '#aaa' } },
    yAxis: {
      type: 'category',
      data: ['小天鹅洗衣机', '海尔冰箱', 'AirPods Pro', '戴尔XPS', '联想小新', '华为Mate60', 'iPhone14', 'MacBook', '小米14', '三星S24'],
      inverse: true,
      axisLabel: { color: '#aaa' }
    },
    series: [{
      name: '销量', type: 'bar',
      data: [320, 280, 250, 220, 200, 180, 160, 140, 120, 100],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#00d4ff' },
          { offset: 1, color: '#0072ff' }
        ]),
        borderRadius: [0, 4, 4, 0]
      }
    }]
  })

  // 用户转化漏斗
  charts[3]?.setOption({
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c}' },
    toolbox: { feature: { dataView: { readOnly: false }, restore: {}, saveAsImage: {} } },
    legend: { data: ['浏览', '收藏', '加购', '下单'], textStyle: { color: '#aaa' } },
    series: [{
      name: '转化漏斗', type: 'funnel', left: '10%', right: '10%',
      minSize: '20%', maxSize: '80%',
      label: { color: '#fff', formatter: '{b}: {c}' },
      data: [
        { value: 5000, name: '浏览' },
        { value: 2000, name: '收藏' },
        { value: 1200, name: '加购' },
        { value: 400, name: '下单' }
      ]
    }]
  })

  // 用户价值分层
  charts[4]?.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '用户数', axisLabel: { color: '#aaa' }, nameTextStyle: { color: '#aaa' } },
    yAxis: {
      type: 'category',
      data: ['高价值', '中价值', '低价值', '潜在用户'],
      axisLabel: { color: '#aaa' }
    },
    series: [{
      name: '用户数', type: 'bar',
      data: [
        { value: 320, itemStyle: { color: '#ff6b6b' } },
        { value: 850, itemStyle: { color: '#ffa502' } },
        { value: 1200, itemStyle: { color: '#2ed573' } },
        { value: 886, itemStyle: { color: '#1e90ff' } }
      ],
      label: { show: true, position: 'right', color: '#aaa' }
    }]
  })
}
</script>

<style scoped>
.data-screen {
  background: #0f1923;
  min-height: 100vh;
  padding: 20px;
  color: #fff;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: rgba(0, 100, 200, 0.3);
  border-radius: 8px;
  margin-bottom: 20px;
}

.screen-header h1 {
  font-size: 24px;
  margin: 0;
  background: linear-gradient(90deg, #00d4ff, #0099ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-time {
  font-size: 14px;
  color: #aaa;
}

.metric-cards {
  margin-bottom: 20px;
}

.metric-card {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 10px;
}

.metric-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  line-height: 1.2;
}

.metric-title {
  font-size: 12px;
  color: #aaa;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.chart-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #ddd;
  font-size: 14px;
  padding: 12px 20px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .metric-cards .el-col-4 {
    width: 33.33%;
    margin-bottom: 12px;
  }
}

@media (max-width: 768px) {
  .metric-cards .el-col-4 {
    width: 50%;
  }
  .chart-row .el-col-12 {
    width: 100%;
  }
  .screen-header h1 {
    font-size: 18px;
  }
}
</style>
