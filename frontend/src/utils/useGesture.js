import { ref } from 'vue'

/**
 * 滑动删除（移动端触摸手势）
 * @param {Function} onDelete - 删除回调
 */
export function useSwipeDelete(onDelete) {
  const swipingId = ref(null)
  const startX = ref(0)
  const offsetX = ref(0)
  const threshold = 60 // 滑动60px触发删除

  const touchStart = (e, id) => {
    swipingId.value = id
    startX.value = e.touches[0].clientX
    offsetX.value = 0
  }

  const touchMove = (e) => {
    if (!swipingId.value) return
    const diff = startX.value - e.touches[0].clientX
    offsetX.value = Math.max(0, Math.min(120, diff))
  }

  const touchEnd = () => {
    if (offsetX.value > threshold) {
      onDelete(swipingId.value)
    }
    swipingId.value = null
    offsetX.value = 0
  }

  const style = (id) => swipingId.value === id ? { transform: `translateX(-${offsetX.value}px)`, transition: 'transform 0.2s' } : {}

  return { swipingId, offsetX, touchStart, touchMove, touchEnd, style }
}

/**
 * 长按事件
 */
export function useLongPress(callback, delay = 600) {
  let timer = null
  const start = (e, ...args) => { timer = setTimeout(() => callback(...args), delay) }
  const end = () => { clearTimeout(timer) }
  return { onTouchstart: start, onTouchend: end, onTouchmove: end }
}
