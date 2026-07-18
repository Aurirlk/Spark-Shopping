import axios from 'axios'

/**
 * AI聊天API
 */

// 发送消息（非流式）
export function sendChatMessage(data) {
  return axios.post('/api/ai/chat', data)
}

// 发送消息（流式）
export function sendChatMessageStream(data) {
  return axios.post('/api/ai/chat/stream', data, {
    responseType: 'stream'
  })
}

// 获取会话历史
export function getChatHistory(conversationId) {
  return axios.get(`/api/ai/history/${conversationId}`)
}

// 获取会话列表
export function getConversations() {
  return axios.get('/api/ai/conversations')
}

// 删除会话
export function deleteConversation(conversationId) {
  return axios.delete(`/api/ai/conversations/${conversationId}`)
}

// 清空所有会话
export function clearAllConversations() {
  return axios.delete('/api/ai/conversations')
}
