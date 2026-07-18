<template>
  <div class="user-center-page">
    <div class="container">
      <h1 class="page-title">个人中心</h1>

      <div class="user-center-layout">
        <!-- 侧边栏 -->
        <div class="sidebar">
          <div class="user-info">
            <el-avatar :size="64" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <h3>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h3>
          </div>

          <!-- 每日签到 -->
          <div class="checkin-card">
            <div class="checkin-info">
              <span class="checkin-label">已签到 {{ streak }} 天</span>
              <span class="checkin-points">{{ pointsV }} 积分</span>
            </div>
            <el-button :type="checkedIn ? 'default' : 'success'" :disabled="checkedIn" size="small" round @click="handleCheckin">
              {{ checkedIn ? '已签到' : '签到 +1' }}
            </el-button>
          </div>

          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="/user">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="/order/list">
              <el-icon><List /></el-icon>
              <span>我的订单</span>
            </el-menu-item>
            <el-menu-item index="/cart">
              <el-icon><ShoppingCart /></el-icon>
              <span>我的购物车</span>
            </el-menu-item>
            <el-menu-item index="/user/favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="/coupon">
              <el-icon><Ticket /></el-icon>
              <span>优惠券</span>
            </el-menu-item>
            <el-menu-item index="settings">
              <el-icon><Setting /></el-icon>
              <span>设置</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 主内容区 -->
        <div class="main-content">
          <div v-if="activeView === 'info'" class="user-info-form">
            <h2>个人信息</h2>

            <el-form ref="formRef" :model="form" label-width="80px" size="large">
              <el-form-item label="用户名">
                <el-input v-model="form.username" disabled />
              </el-form-item>

              <el-form-item label="昵称">
                <el-input v-model="form.nickname" placeholder="请输入昵称" />
              </el-form-item>

              <el-form-item label="手机号">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>

              <el-form-item label="性别">
                <el-radio-group v-model="form.gender">
                  <el-radio :label="0">未知</el-radio>
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="handleSave">保存修改</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 设置面板 -->
          <div v-if="activeView === 'settings'" class="settings-panel">
            <h2>设置</h2>
            <el-form label-width="100px" size="large">
              <el-form-item label="消息通知">
                <el-switch v-model="settings.pushNotify" active-text="推送通知" />
              </el-form-item>
              <el-form-item label="声音">
                <el-switch v-model="settings.sound" active-text="声音提醒" />
              </el-form-item>
              <el-form-item label="隐私">
                <el-switch v-model="settings.personalized" active-text="个性化推荐" />
              </el-form-item>
              <el-form-item label="暗色模式">
                <el-switch :model-value="isDark" @update:model-value="toggleDark" active-text="暗色主题" />
              </el-form-item>
              <el-form-item label="关于">
                <span class="about-version">Shopping 商城 v3.0</span>
              </el-form-item>
              <el-form-item>
                <el-button type="danger" @click="handleLogout">退出登录</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, List, ShoppingCart, Star, Ticket, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/user'
import { useDarkMode } from '@/stores/dark'
import axios from 'axios'

const { isDark, toggle: toggleDark } = useDarkMode()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)

const activeView = computed(() => {
  const menu = route.query.menu
  if (menu === 'settings') return 'settings'
  return 'info'
})

const settings = reactive({
  pushNotify: localStorage.getItem('setting_push') !== '0',
  sound: localStorage.getItem('setting_sound') !== '0',
  personalized: localStorage.getItem('setting_personalized') !== '0'
})

watch(settings, (val) => {
  localStorage.setItem('setting_push', val.pushNotify ? '1' : '0')
  localStorage.setItem('setting_sound', val.sound ? '1' : '0')
  localStorage.setItem('setting_personalized', val.personalized ? '1' : '0')
}, { deep: true })

const checkedIn = ref(false)
const streak = ref(0)
const pointsV = ref(0)

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  gender: 0
})

const activeMenu = computed(() => route.path)

onMounted(async () => {
  try {
    const res = await axios.get('/api/checkin/status')
    checkedIn.value = res.data.data?.todayChecked || false
    streak.value = (res.data.data?.weekCheckins || []).length
    pointsV.value = res.data.data?.totalPoints || 0
  } catch (e) {}
  loadUserInfo()
})

const handleCheckin = async () => {
  const res = await axios.post('/api/checkin/do')
  if (res.data.code === 200) {
    checkedIn.value = true; streak.value++; pointsV.value += res.data.data.points
    ElMessage.success(res.data.data.message)
  } else ElMessage.warning(res.data.message || '签到失败')
}

const loadUserInfo = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(form, {
      username: userInfo.username,
      nickname: userInfo.nickname,
      phone: userInfo.phone,
      email: userInfo.email,
      gender: userInfo.gender
    })
  }
}

const handleMenuSelect = (index) => {
  if (index === 'settings') {
    router.push({ path: '/user', query: { menu: 'settings' } })
  } else {
    router.push(index)
  }
}

const handleSave = async () => {
  try {
    await updateUserInfo(form)
    await userStore.getUserInfoAction()
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败：', error)
  }
}

const handleReset = () => {
  loadUserInfo()
}

const handleLogout = () => {
  localStorage.removeItem('token')
  userStore.clearUserInfo?.() || userStore.$reset?.()
  router.push('/login')
}
</script>

<style scoped>
.user-center-page {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.container {
  width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

.page-title {
  font-size: 22px;
  color: #303133;
  margin-bottom: 20px;
}

.user-center-layout {
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 240px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.user-info {
  padding: 24px;
  text-align: center;
  background: #409eff;
  color: #fff;
}

.user-info .el-avatar {
  margin-bottom: 12px;
}

.user-info h3 {
  font-size: 16px;
}

.main-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.user-info-form h2 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.checkin-card { display: flex; align-items: center; justify-content: space-between; padding: 12px; background: #fef0f0; border-radius: 8px; margin: 12px; }
.checkin-info { display: flex; flex-direction: column; gap: 2px; }
.checkin-label { font-size: 14px; font-weight: bold; color: #f56c6c; }
.checkin-points { font-size: 12px; color: #999; }

/* 设置面板 */
.settings-panel { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,.06); }
.settings-panel h2 { font-size: 18px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid #ebeef5; }
.about-version { color: #999; font-size: 14px; }
</style>
