import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/product',
    name: 'ProductList',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车', requireAuth: true }
  },
  {
    path: '/order/create',
    name: 'CreateOrder',
    component: () => import('@/views/CreateOrder.vue'),
    meta: { title: '创建订单', requireAuth: true }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@/views/OrderList.vue'),
    meta: { title: '订单列表', requireAuth: true }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { title: '订单详情', requireAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/UserCenter.vue'),
    meta: { title: '个人中心', requireAuth: true }
  },
  {
    path: '/user/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏', requireAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/SearchResult.vue'),
    meta: { title: '搜索结果' }
  },
  {
    path: '/payment',
    name: 'Payment',
    component: () => import('@/views/Payment.vue'),
    meta: { title: '支付中心', requireAuth: true }
  },
  {
    path: '/coupon',
    name: 'CouponCenter',
    component: () => import('@/views/CouponCenter.vue'),
    meta: { title: '领券中心' }
  },
  {
    path: '/seckill',
    name: 'Seckill',
    component: () => import('@/views/Seckill.vue'),
    meta: { title: '限时秒杀' }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { title: '管理后台', requireAuth: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据大屏' }
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/ProductManage.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'data-screen',
        name: 'DataScreen',
        component: () => import('@/views/admin/DataScreen.vue'),
        meta: { title: '数据大屏' }
      },
      {
        path: 'activity',
        name: 'AdminActivity',
        component: () => import('@/views/admin/ActivityManage.vue'),
        meta: { title: '活动管理' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 购物网站` : '购物网站'

  // 验证是否需要登录
  if (to.meta.requireAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
  }

  next()
})

export default router
