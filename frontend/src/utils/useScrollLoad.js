import { ref, onMounted, onUnmounted, watch } from 'vue'

/**
 * 下拉刷新 + 上拉加载更多
 * @param {Function} loadFn - 加载函数，返回 { records, total }
 * @param {Object} options - 配置
 */
export function useScrollLoad(loadFn, options = {}) {
  const { pageSize = 10, immediate = true } = options

  const list = ref([])
  const loading = ref(false)
  const finished = ref(false)
  const refreshing = ref(false)
  const pageNum = ref(1)
  const total = ref(0)
  const error = ref(null)

  const load = async (reset = false) => {
    if (loading.value || finished.value) return
    if (reset) { pageNum.value = 1; finished.value = false }
    loading.value = true; error.value = null
    try {
      const res = await loadFn({ pageNum: pageNum.value, pageSize })
      const records = res.data?.records || res.data || []
      if (reset) list.value = records
      else list.value.push(...records)
      total.value = res.data?.total || records.length
      if (records.length < pageSize) finished.value = true
      pageNum.value++
    } catch (e) {
      error.value = e.message || '加载失败'
    } finally {
      loading.value = false; refreshing.value = false
    }
  }

  const refresh = () => { refreshing.value = true; load(true) }
  const loadMore = () => load(false)
  const retry = () => load(true)

  if (immediate) onMounted(() => load(true))

  return { list, loading, finished, refreshing, error, refresh, loadMore, retry }
}

/**
 * 断网检测
 */
export function useOnlineStatus() {
  const online = ref(navigator.onLine)
  const onOnline = () => { online.value = true }
  const onOffline = () => { online.value = false }
  onMounted(() => { window.addEventListener('online', onOnline); window.addEventListener('offline', onOffline) })
  onUnmounted(() => { window.removeEventListener('online', onOnline); window.removeEventListener('offline', onOffline) })
  return { online }
}
