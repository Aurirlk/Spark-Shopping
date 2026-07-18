<template>
  <div class="not-found-page">
    <div class="container">
      <el-empty description="页面不存在">
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
