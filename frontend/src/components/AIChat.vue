<template>
  <div class="ai-chat" :class="{ 'is-open': isOpen }">
    <!-- 聊天触发按钮 -->
    <div class="chat-trigger" @click="toggleChat">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0">
        <el-icon :size="24"><ChatDotRound /></el-icon>
      </el-badge>
    </div>

    <!-- 聊天窗口 -->
    <transition name="slide">
      <div v-show="isOpen" class="chat-window">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <el-icon><Service /></el-icon>
            <span>AI智能客服</span>
          </div>
          <div class="header-right">
            <el-tooltip content="清空对话">
              <el-icon @click="clearChat"><Delete /></el-icon>
            </el-tooltip>
            <el-tooltip content="关闭">
              <el-icon @click="closeChat"><Close /></el-icon>
            </el-tooltip>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesContainer">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-avatar">
              <el-icon :size="40"><Service /></el-icon>
            </div>
            <h3>你好！我是AI智能客服</h3>
            <p>有什么可以帮您的吗？</p>
            <div class="quick-questions">
              <el-button
                v-for="question in quickQuestions"
                :key="question"
                size="small"
                @click="sendQuickQuestion(question)"
              >
                {{ question }}
              </el-button>
            </div>
          </div>

          <!-- 消息项 -->
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="message-item"
            :class="[msg.role === 'user' ? 'user-message' : 'assistant-message']"
          >
            <div class="message-avatar">
              <el-icon v-if="msg.role === 'user'"><User /></el-icon>
              <el-icon v-else><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-bubble" v-html="formatMessage(msg.content)"></div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message-item assistant-message">
            <div class="message-avatar">
              <el-icon><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-bubble loading">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 推荐问题 -->
        <div v-if="suggestedQuestions.length > 0" class="suggested-questions">
          <div
            v-for="question in suggestedQuestions"
            :key="question"
            class="suggested-item"
            @click="sendQuickQuestion(question)"
          >
            {{ question }}
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="1"
            :autosize="{ minRows: 1, maxRows: 4 }"
            placeholder="输入您的问题..."
            @keydown.enter.exact.prevent="sendMessage"
            @keydown.enter.shift.exact.prevent="inputMessage += '\n'"
          />
          <el-button
            type="primary"
            :icon="Promotion"
            :loading="loading"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
          />
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound,
  Service,
  User,
  Close,
  Delete,
  Promotion
} from '@element-plus/icons-vue'
import axios from 'axios'

// Props
const props = defineProps({
  userId: {
    type: Number,
    default: null
  }
})

// 状态
const isOpen = ref(false)
const loading = ref(false)
const inputMessage = ref('')
const messages = ref([])
const suggestedQuestions = ref([])
const unreadCount = ref(0)
const conversationId = ref(null)
const messagesContainer = ref(null)

// 快捷问题
const quickQuestions = [
  '有什么推荐的商品？',
  '如何查看订单状态？',
  '退换货流程是什么？',
  '最近有什么活动？'
]

// 切换聊天窗口
const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    unreadCount.value = 0
    nextTick(() => scrollToBottom())
  }
}

// 关闭聊天窗口
const closeChat = () => {
  isOpen.value = false
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || loading.value) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: message,
    createTime: new Date().toISOString()
  })

  inputMessage.value = ''
  loading.value = true
  suggestedQuestions.value = []

  await nextTick()
  scrollToBottom()

  try {
    // 调用AI接口
    const response = await axios.post('/api/ai/chat', {
      conversationId: conversationId.value,
      message: message
    })

    if (response.data.code === 200) {
      const data = response.data.data

      // 更新会话ID
      conversationId.value = data.conversationId

      // 添加助手回复
      messages.value.push({
        role: 'assistant',
        content: data.content,
        createTime: new Date().toISOString()
      })

      // 更新推荐问题
      if (data.suggestedQuestions) {
        suggestedQuestions.value = data.suggestedQuestions
      }

      // 如果窗口未打开，增加未读计数
      if (!isOpen.value) {
        unreadCount.value++
      }
    } else {
      ElMessage.error('获取回复失败，请稍后重试')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 发送快捷问题
const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

// 清空聊天
const clearChat = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有对话记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 调用清空接口
    if (conversationId.value) {
      await axios.delete(`/api/ai/conversations/${conversationId.value}`)
    }

    messages.value = []
    suggestedQuestions.value = []
    conversationId.value = null

    ElMessage.success('对话已清空')
  } catch {
    // 取消操作
  }
}

// 格式化消息（支持简单的Markdown）
const formatMessage = (content) => {
  if (!content) return ''

  // 转换换行符
  let formatted = content.replace(/\n/g, '<br>')

  // 转换粗体
  formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')

  // 转换斜体
  formatted = formatted.replace(/\*(.*?)\*/g, '<em>$1</em>')

  // 转换链接
  formatted = formatted.replace(
    /\[(.*?)\]\((.*?)\)/g,
    '<a href="$2" target="_blank">$1</a>'
  )

  return formatted
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 监听消息变化，自动滚动
watch(messages, () => {
  nextTick(() => scrollToBottom())
}, { deep: true })
</script>

<style scoped>
.ai-chat {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 9999;
}

.chat-trigger {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  color: white;
}

.chat-trigger:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.chat-window {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 380px;
  height: 560px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right .el-icon {
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.header-right .el-icon:hover {
  opacity: 1;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}

.welcome-message {
  text-align: center;
  padding: 40px 20px;
}

.welcome-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: white;
}

.welcome-message h3 {
  margin: 0 0 8px;
  color: #333;
  font-size: 18px;
}

.welcome-message p {
  margin: 0 0 20px;
  color: #666;
  font-size: 14px;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.message-item {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #667eea;
  color: white;
}

.assistant-message .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-content {
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.user-message .message-bubble {
  background: #667eea;
  color: white;
  border-top-right-radius: 4px;
}

.assistant-message .message-bubble {
  background: white;
  color: #333;
  border-top-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.user-message .message-time {
  text-align: right;
}

.loading {
  display: flex;
  gap: 4px;
  padding: 16px;
}

.loading .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading .dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.suggested-questions {
  padding: 8px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  background: white;
  border-top: 1px solid #eee;
}

.suggested-item {
  padding: 6px 12px;
  background: #f0f5ff;
  color: #667eea;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.suggested-item:hover {
  background: #667eea;
  color: white;
}

.chat-input {
  padding: 12px;
  background: white;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.chat-input .el-input {
  flex: 1;
}

.chat-input .el-button {
  height: 32px;
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* 动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
