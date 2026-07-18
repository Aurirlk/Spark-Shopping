<template>
  <el-container class="admin-layout">
    <!-- 移动端侧边栏遮罩 -->
    <div v-if="sidebarOpen" class="sidebar-overlay" @click="sidebarOpen = false"></div>

    <el-aside :class="{ 'is-open': sidebarOpen }">
      <div class="logo">
        <h2>管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="admin-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据大屏</span>
        </el-menu-item>
        <el-menu-item index="/admin/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/activity">
          <el-icon><Promotion /></el-icon>
          <span>活动管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left">
          <!-- 移动端菜单按钮 -->
          <el-button class="menu-toggle hide-desktop" @click="sidebarOpen = !sidebarOpen" text>
            <el-icon size="20"><Menu /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span>管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/')">返回前台</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Menu, Promotion } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const sidebarOpen = ref(false)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '数据大屏',
    '/admin/products': '商品管理',
    '/admin/orders': '订单管理',
    '/admin/users': '用户管理'
  }
  return titles[route.path] || '首页'
})

const handleMenuSelect = () => {
  sidebarOpen.value = false
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-aside {
  background: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h2 {
  font-size: 18px;
  margin: 0;
}

.admin-menu {
  border-right: none;
  background: #304156;
}

.admin-menu .el-menu-item {
  color: #bfcbd9;
}

.admin-menu .el-menu-item:hover,
.admin-menu .el-menu-item.is-active {
  background: #263445;
  color: #409eff;
}

.el-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.el-main {
  background: #f5f7fa;
  padding: 20px;
}

/* ===== 响应式：移动端侧边栏 ===== */
@media (max-width: 768px) {
  .el-aside {
    position: fixed !important;
    top: 0;
    left: 0;
    width: 0 !important;
    overflow: hidden;
    z-index: 1000;
    height: 100vh;
    transition: width 0.3s ease;
  }
  .el-aside.is-open {
    width: 200px !important;
  }
  .sidebar-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    z-index: 999;
  }
  .hide-desktop {
    display: inline-flex !important;
  }
  .el-main {
    padding: 12px;
  }
}

@media (min-width: 769px) {
  .hide-desktop {
    display: none !important;
  }
}
</style>
