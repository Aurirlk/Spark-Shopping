import axios from 'axios'

/**
 * 推荐系统API
 */

// 获取用户个性化推荐
export function getUserRecommendations(limit = 10) {
  return axios.get('/api/recommend/user', { params: { limit } })
}

// 获取相似商品推荐
export function getSimilarProducts(productId, limit = 6) {
  return axios.get(`/api/recommend/similar/${productId}`, { params: { limit } })
}

// 获取热门商品推荐
export function getHotProducts(categoryId = null, limit = 10) {
  return axios.get('/api/recommend/hot', { params: { categoryId, limit } })
}

// 获取推荐理由
export function getRecommendationReason(productId) {
  return axios.get(`/api/recommend/reason/${productId}`)
}

// 记录用户行为
export function recordBehavior(productId, behaviorType) {
  return axios.post('/api/recommend/behavior', null, {
    params: { productId, behaviorType }
  })
}

/**
 * 行为类型常量
 */
export const BEHAVIOR_TYPES = {
  VIEW: 1,      // 浏览
  FAVORITE: 2,  // 收藏
  CART: 3,      // 加购
  PURCHASE: 4   // 购买
}
