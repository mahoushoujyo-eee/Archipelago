<template>
  <header class="app-header" :class="{ 'scrolled': scrolled, 'debug-mode': debugMode }">
    <div class="container">
      <div class="logo">
        <router-link to="/">
          <h1>Archipelago</h1>
        </router-link>
      </div>
      
      <nav class="desktop-nav">
        <router-link to="/" class="nav-link" @click="debugClick('首页')">首页</router-link>
        <router-link to="/blog" class="nav-link" @click="debugClick('博客')">博客</router-link>
        <router-link to="/ai" class="nav-link" @click="debugClick('AI')">知识问答</router-link>
        <router-link to="/resource" class="nav-link" @click="debugClick('资料库')">资料库</router-link>
        
        <!-- Admin links -->
        <template v-if="userStore.isAdmin">
          <router-link to="/admin" class="nav-link" @click="debugClick('仪表盘')">仪表盘</router-link>
        </template>
      </nav>
      
      <div class="right-actions">
        
        <!-- Theme toggler -->
        <button class="theme-toggle" @click="handleThemeToggle">
          <el-icon v-if="themeStore.isDark"><Sunny /></el-icon>
          <el-icon v-else><Moon /></el-icon>
        </button>
        
        <!-- User menu -->
        <template v-if="userStore.isLoggedIn">
          <el-dropdown trigger="click">
            <div class="user-avatar">
              <el-avatar :size="32" :src="userAvatar">
                {{ userAvatarFallback }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/profile" class="dropdown-link">个人资料</router-link>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        
        <!-- Auth buttons -->
        <template v-else>
          <router-link to="/login" class="auth-btn login-btn" @click="debugClick('登录')">登录</router-link>
          <router-link to="/register" class="auth-btn register-btn" @click="debugClick('注册')">注册</router-link>
        </template>
      </div>
      

    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useThemeStore } from '../../stores/theme'
import { ElMessage } from 'element-plus'
import { getUserAvatarUrl } from '../../utils/avatar'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()

const scrolled = ref(false)
const debugMode = ref(false)
const userAvatar = ref('')

const userAvatarFallback = computed(() => {
  if (!userStore.user) return ''
  return userStore.user.name ? userStore.user.name.charAt(0).toUpperCase() : 'U'
})

// 监听用户变化，更新头像
watch(() => userStore.user, async (newUser) => {
  if (newUser && newUser.id) {
    userAvatar.value = await getUserAvatarUrl(newUser.id, newUser.avatar)
  } else {
    userAvatar.value = ''
  }
}, { immediate: true, deep: true })

// 监听用户头像变化，实时更新
watch(() => userStore.user?.avatar, async (newAvatar) => {
  if (userStore.user && userStore.user.id) {
    userAvatar.value = await getUserAvatarUrl(userStore.user.id, newAvatar)
  }
}, { immediate: false })

// Handle scroll event for header style
const handleScroll = () => {
  scrolled.value = window.scrollY > 50
}

// Handle logout
const handleLogout = async () => {
  userStore.logout()
  ElMessage.success('登出成功！')
  router.push('/')
}





// Debug function to test click events
const debugClick = (elementName) => {
  console.log(`Clicked on: ${elementName}`)
}

// Handle theme toggle with debug
const handleThemeToggle = () => {
  debugClick('主题切换')
  themeStore.toggleTheme()
}

// Toggle debug mode
const toggleDebugMode = () => {
  debugMode.value = !debugMode.value
  console.log('Debug mode:', debugMode.value)
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  handleScroll() // Check initial scroll position
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  padding: 1rem 0;
  transition: background-color 0.3s, box-shadow 0.3s;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  pointer-events: auto;
}

.app-header.scrolled {
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 夜间模式下的导航栏样式 */
.dark-mode .app-header {
  background-color: rgba(20, 20, 20, 0.8);
}

.dark-mode .app-header.scrolled {
  background-color: rgba(20, 20, 20, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.logo a {
  text-decoration: none;
  color: #0288d1;
  font-weight: 700;
}

.logo h1 {
  margin: 0;
  font-size: 1.8rem;
  font-family: 'Playfair Display', 'Georgia', 'Times New Roman', serif;
  font-weight: 600;
  background: linear-gradient(135deg, #6db33f 0%, #5cb85c 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-animation 4s ease-in-out infinite;
  letter-spacing: 0.5px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 手机端隐藏标题并居中导航 */
@media (max-width: 768px) {
  .logo {
    display: none;
  }
  
  .desktop-nav {
    justify-content: center;
  }
}

@keyframes gradient-animation {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.desktop-nav {
  display: flex;
  gap: 1.5rem;
  position: relative;
  z-index: 10;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.3s ease;
  pointer-events: auto;
  z-index: 1;
}

.dark-mode .nav-link {
  color: var(--el-text-color-primary);
}

.nav-link:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #0288d1;
  transition: width 0.3s;
}

.nav-link:hover:after,
.router-link-active:after {
  width: 100%;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
  z-index: 10;
}

.theme-toggle {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  transition: background-color 0.3s;
  position: relative;
  z-index: 2;
  pointer-events: auto;
}

.dark-mode .theme-toggle {
  color: var(--el-text-color-primary);
}

.theme-toggle:hover {
  background-color: var(--el-fill-color-light);
}

.user-avatar {
  cursor: pointer;
  position: relative;
  z-index: 2;
  pointer-events: auto;
}

.auth-btn {
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  transition: background-color 0.3s, color 0.3s;
  position: relative;
  z-index: 2;
  pointer-events: auto;
  display: inline-block;
  /* 确保按钮不会意外扩展 */
  width: auto;
  max-width: fit-content;
  flex-shrink: 0;
}

.login-btn {
  color: #0288d1;
}

.login-btn:hover {
  color: #0277bd;
}

.register-btn {
  background-color: #0288d1;
  color: white;
}

.register-btn:hover {
  background-color: #0277bd;
}

.dropdown-link {
  display: block;
  color: inherit;
  text-decoration: none;
}




</style>