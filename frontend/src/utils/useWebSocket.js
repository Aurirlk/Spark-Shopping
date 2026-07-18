import { ref, onMounted, onUnmounted } from 'vue'

/**
 * WebSocket 实时通知
 */
export function useWebSocket(userId) {
  const connected = ref(false)
  const message = ref(null)
  let ws = null
  let reconnectTimer = null

  const connect = () => {
    if (!userId) return
    ws = new WebSocket(`ws://${location.host}/api/ws/notify?userId=${userId}`)
    ws.onopen = () => { connected.value = true }
    ws.onmessage = (e) => {
      try { message.value = JSON.parse(e.data) } catch { message.value = e.data }
    }
    ws.onclose = () => {
      connected.value = false
      reconnectTimer = setTimeout(connect, 5000) // 自动重连
    }
  }

  const disconnect = () => {
    if (ws) { ws.close(); ws = null }
    clearTimeout(reconnectTimer)
  }

  onMounted(() => connect())
  onUnmounted(() => disconnect())

  return { connected, message }
}
