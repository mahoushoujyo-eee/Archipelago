```vue
<template>
  <div class="not-found">
    <div class="container">
      <el-result
        icon="warning"
        title="404"
        sub-title="抱歉，您访问的页面不存在。"
      >
        <template #extra>
          <router-link to="/">
            <el-button type="primary">返回首页</el-button>
          </router-link>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 0;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 1rem;
}
</style>
```