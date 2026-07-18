<template>
  <footer class="site-footer">
    <div class="footer-content">
      <div class="footer-brand">
        <div class="footer-logo">
          <el-icon :size="24" color="#409eff"><ShoppingBag /></el-icon>
          <span>Shopping 商城</span>
        </div>
        <p class="footer-desc">品质生活，从这里开始。精选全球好物，为您提供优质的购物体验。</p>
        <div class="footer-social">
          <el-icon :size="18" color="#999"><ChatDotRound /></el-icon>
          <el-icon :size="18" color="#999"><Message /></el-icon>
          <el-icon :size="18" color="#999"><Microphone /></el-icon>
        </div>
      </div>
      <div class="footer-links">
        <div class="link-group">
          <h4>购物指南</h4>
          <a>购物流程</a>
          <a>支付方式</a>
          <a>配送说明</a>
          <a>常见问题</a>
        </div>
        <div class="link-group">
          <h4>售后服务</h4>
          <a>退换货政策</a>
          <a>退款说明</a>
          <a>投诉建议</a>
          <a>联系客服</a>
        </div>
        <div class="link-group">
          <h4>关于我们</h4>
          <a>公司介绍</a>
          <a>加入我们</a>
          <a>合作伙伴</a>
          <a>隐私政策</a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p>© 2026 Shopping 商城. All rights reserved. | 京ICP备xxxxxxxx号</p>
    </div>
  </footer>
</template>

<script setup>
import { ShoppingBag, ChatDotRound, Message, Microphone } from '@element-plus/icons-vue'
</script>

<style scoped>
.site-footer {
  background: #2c3e50;
  color: #bdc3c7;
  padding: 40px 20px 20px;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 60px;
}

.footer-brand {
  flex: 1;
  max-width: 300px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 12px;
}

.footer-desc {
  font-size: 13px;
  line-height: 1.6;
  margin-bottom: 16px;
}

.footer-social {
  display: flex;
  gap: 16px;
}

.footer-links {
  flex: 2;
  display: flex;
  gap: 60px;
}

.link-group h4 {
  font-size: 15px;
  color: #fff;
  margin-bottom: 12px;
}

.link-group a {
  display: block;
  font-size: 13px;
  color: #95a5a6;
  margin-bottom: 8px;
  cursor: pointer;
  transition: color 0.2s;
}

.link-group a:hover {
  color: #409eff;
}

.footer-bottom {
  max-width: 1200px;
  margin: 30px auto 0;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  text-align: center;
  font-size: 12px;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 30px;
  }
  .footer-links {
    flex-wrap: wrap;
    gap: 30px;
  }
  .link-group {
    width: calc(50% - 15px);
  }
}
</style>
