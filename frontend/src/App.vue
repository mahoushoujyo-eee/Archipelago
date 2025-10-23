<template>
  <el-config-provider :locale="zhCn">
    <div class="app-container" :class="{ 'dark-mode': isDark }">
      <AppHeader />
      <main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
      <AppFooter />
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue'
import { useThemeStore } from './stores/theme'
import { useUserStore } from './stores/user'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import AppHeader from './components/layout/AppHeader.vue'
import AppFooter from './components/layout/AppFooter.vue'

const themeStore = useThemeStore()
const userStore = useUserStore()
const isDark = computed(() => themeStore.isDark)

// 监听主题变化，更新 html 元素的 class
watch(isDark, (newValue) => {
  if (newValue) {
    document.documentElement.classList.add('dark-mode')
  } else {
    document.documentElement.classList.remove('dark-mode')
  }
}, { immediate: true })

onMounted(() => {
  // 检查用户登录状态
  if(localStorage.getItem('token')) {
    userStore.token = localStorage.getItem('token')
  }
  console.log('token', userStore.token)
  if (userStore.token) {
    userStore.fetchUser()
  }
  
  // 初始化主题
  if (isDark.value) {
    document.documentElement.classList.add('dark-mode')
  }
})
</script>

<style>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.main-content {
  flex: 1;
  padding-bottom: 2rem;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 确保夜间模式样式正确应用 */
html.dark-mode {
  color-scheme: dark;
}

html.dark-mode body {
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
}
</style>