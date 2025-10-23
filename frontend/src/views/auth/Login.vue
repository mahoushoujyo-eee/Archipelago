<template>
  <div class="auth-page">
    <div class="form-container">
      <h1 class="form-title">欢迎回来</h1>
      
      <!-- Login form -->
      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="rules"
        @submit.native.prevent="handleSubmit"
      >
        <el-form-item prop="email">
          <el-input
            v-model="loginForm.email"
            placeholder="邮箱"
            type="email"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            placeholder="密码"
            :type="showPassword ? 'text' : 'password'"
            prefix-icon="Lock"
          >
            <template #suffix>
              <el-icon class="password-toggle" @click="showPassword = !showPassword">
                <View v-if="showPassword" />
                <Hide v-else />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <div class="form-options">
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
          <router-link to="/reset-password" class="forgot-password">
            忘记密码？
          </router-link>
        </div>
        
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            class="submit-button"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="auth-footer">
        没有账号？
        <router-link to="/register" class="auth-link">
          注册
        </router-link>
      </div>
      
      <div class="form-divider">
        <span class="form-divider-text">或者使用</span>
      </div>
      
      <!-- OAuth buttons -->
      <div class="oauth-buttons">
        <button class="oauth-button github-button" @click="handleOAuth('github')">
          <svg class="oauth-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          GitHub
        </button>
        <button class="oauth-button google-button" @click="handleOAuth('google')">
          <svg class="oauth-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
            <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
            <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
            <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
          </svg>
          Google
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const showPassword = ref(false)

// Form data
const loginForm = reactive({
  email: '',
  password: '',
  remember: false
})

// Form validation rules
const rules = {
  email: [
    { required: true, message: '请输入您的邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入您的密码', trigger: 'blur' },
    { min: 6, message: '密码至少需要 6 个字符', trigger: 'blur' }
  ]
}

// Handle form submission
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    const success = await userStore.login({
      email: loginForm.email,
      password: loginForm.password,
      remember: loginForm.remember
    })
    
    if (success) {
      ElMessage.success('登录成功')
      
      // Redirect to intended page or home
      const redirectPath = route.query.redirect || '/'
      router.push(redirectPath)
    } else {
      ElMessage.error(userStore.error || '登录失败')
    }
  } catch (error) {
    console.error('验证失败:', error)
  } finally {
    loading.value = false
  }
}

// Handle OAuth login
const handleOAuth = (provider) => 
{
  if (provider === 'google') 
  {
    ElMessage.info('Google OAuth2 功能正在开发中')
    return
  }
  
  // In a real implementation, this would redirect to the OAuth provider
  // For demo purposes, we'll just show a message
  ElMessage.info(`${provider} 登录将重定向到认证页面`)
  window.location.href = `http://127.0.0.1:58080/oauth2/authorization/${provider}`
}
</script>

<style scoped>
.auth-page {
  padding: 3rem 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px);
}

.form-container {
  width: 100%;
  max-width: 480px; /* 增加最大宽度 */
  padding: 2.5rem; /* 增加内边距 */
  background-color: var(--el-bg-color);
  border-radius: 12px; /* 增加圆角 */
  box-shadow: var(--el-box-shadow-light);
}

.form-title {
  text-align: center;
  margin-bottom: 2rem; /* 增加标题下方间距 */
  color: var(--el-text-color-primary);
  font-size: 1.75rem;
  font-weight: 600;
}

/* OAuth按钮样式 */
.oauth-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 0;
}

.oauth-button {
  width: 100%;
  padding: 12px 16px;
  font-size: 16px;
  font-weight: 500;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  height: 48px;
}

.oauth-button:hover {
  border-color: var(--el-color-primary);
  background-color: var(--el-fill-color-light);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.oauth-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.github-button {
  border-color: #24292e;
}

.github-button:hover {
  border-color: #24292e;
  background-color: #24292e;
  color: white;
}

.github-button .oauth-icon {
  color: #24292e;
}

.github-button:hover .oauth-icon {
  color: white;
}

.google-button:hover {
  border-color: #4285f4;
  background-color: #f8f9ff;
}

/* 分隔线样式 */
.form-divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 2rem 0 1.5rem;
  color: var(--el-text-color-secondary);
}

.form-divider::before,
.form-divider::after {
  content: '';
  flex-grow: 1;
  height: 1px;
  background: var(--el-border-color);
}

.form-divider-text {
  padding: 0 1rem;
  font-size: 14px;
  font-weight: 500;
}

/* 增加表单项间距 */
.el-form-item {
  margin-bottom: 1.5rem;
}

/* 增加输入框高度 */
:deep(.el-input__inner) {
  height: 48px;
  font-size: 16px;
}
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.forgot-password {
  color: var(--el-color-primary);
  text-decoration: none;
  font-size: 0.875rem;
}

.forgot-password:hover {
  text-decoration: underline;
}

.submit-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 0.5rem;
}

.auth-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
  color: var(--el-text-color-secondary);
}

/* 夜间模式样式 */
.dark-mode .form-container {
  background-color: var(--el-bg-color);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.dark-mode .form-title {
  color: var(--el-text-color-primary);
}

.dark-mode .oauth-button {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
  color: var(--el-text-color-primary);
}

.dark-mode .oauth-button:hover {
  background-color: var(--el-fill-color);
  border-color: var(--el-color-primary);
}

.dark-mode .github-button:hover {
  background-color: #24292e;
  color: white;
}

.dark-mode .google-button:hover {
  background-color: rgba(66, 133, 244, 0.1);
  border-color: #4285f4;
}

.dark-mode .auth-footer {
  color: var(--el-text-color-secondary);
}

.auth-link {
  color: var(--el-color-primary);
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}

.password-toggle {
  cursor: pointer;
}
</style>