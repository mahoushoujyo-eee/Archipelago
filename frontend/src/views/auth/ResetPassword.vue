<template>
  <div class="auth-page">
    <div class="form-container">
      <h1 class="form-title">重置密码</h1>
      
      <template v-if="!isSuccess">
        <p class="form-description">
          请填写您的邮箱地址、验证码和新密码来重置密码。
        </p>
        
        <el-form
          ref="resetFormRef"
          :model="resetForm"
          :rules="resetRules"
          @submit.native.prevent="handleResetSubmit"
        >
          <el-form-item prop="email">
            <el-input
              v-model="resetForm.email"
              placeholder="邮箱"
              type="email"
              prefix-icon="Message"
            />
          </el-form-item>
          
          <el-form-item prop="emailCode">
            <div class="verification-group">
              <el-input
                v-model="resetForm.emailCode"
                placeholder="验证码"
                prefix-icon="Check"
                class="verification-input"
              />
              <el-button 
                type="primary" 
                :disabled="countdown > 0 || !resetForm.email"
                @click="sendVerifyCode"
                :loading="sendingCode"
                class="verification-button"
              >
                {{ countdown > 0 ? `重新发送 ${countdown}s` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item prop="newPassword">
            <el-input
              v-model="resetForm.newPassword"
              placeholder="新密码"
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
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="resetForm.confirmPassword"
              placeholder="确认新密码"
              :type="showPassword ? 'text' : 'password'"
              prefix-icon="Lock"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              native-type="submit"
              :loading="loading"
              class="submit-button"
            >
              重置密码
            </el-button>
          </el-form-item>
        </el-form>
      </template>
      
      <template v-else>
        <div class="success-container">
          <el-icon class="success-icon"><CircleCheck /></el-icon>
          <h2>密码重置成功</h2>
          <p>您的密码已成功重置。您现在可以使用新密码登录了。</p>
          <el-button type="primary" @click="goToLogin" class="submit-button">
            去登录
          </el-button>
        </div>
      </template>
      
      <div class="auth-footer" v-if="!isSuccess">
        记住密码？
        <router-link to="/login" class="auth-link">
          登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()
const resetFormRef = ref(null)
const loading = ref(false)
const sendingCode = ref(false)
const showPassword = ref(false)
const countdown = ref(0)
const isSuccess = ref(false)

// Reset form data
const resetForm = reactive({
  email: '',
  emailCode: '',
  newPassword: '',
  confirmPassword: ''
})

// Validation functions
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入您的新密码'))
  } else {
    if (resetForm.confirmPassword !== '') {
      resetFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认您的密码'))
  } else if (value !== resetForm.newPassword) {
    callback(new Error('两次输入的密码不匹配'))
  } else {
    callback()
  }
}

// Form validation rules
const resetRules = {
  email: [
    { required: true, message: '请输入您的邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  emailCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码必须是6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码至少需要 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' }
  ]
}

// Send verification code
const sendVerifyCode = async () => {
  // Validate email first
  if (!resetForm.email) {
    ElMessage.error('请先输入邮箱地址')
    return
  }
  
  // Validate email format
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(resetForm.email)) {
    ElMessage.error('请输入有效的邮箱地址')
    return
  }
  
  try {
    sendingCode.value = true
    
    // Send verification code to email
    const response = await authApi.sendVerifyCode(resetForm.email)
    
    if (response.data.code === 200) {
      ElMessage.success(`验证码已发送至 ${resetForm.email}`)
      
      // Set countdown for resend button
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error(response.data.message || '发送验证码失败')
    }
    
    sendingCode.value = false
  } catch (error) {
    console.error('Send verification code failed:', error)
    ElMessage.error('发送验证码失败，请稍后重试')
    sendingCode.value = false
  }
}

// Handle reset password
const handleResetSubmit = async () => {
  if (!resetFormRef.value) return
  
  try {
    await resetFormRef.value.validate()
    loading.value = true
    
    // Prepare reset password data according to backend requirements
    const resetData = {
      email: resetForm.email,
      emailCode: resetForm.emailCode,
      newPassword: resetForm.newPassword
    }
    
    // Call reset password API
    const response = await authApi.resetPassword(resetData)
    
    if (response.data.code === 200) {
      ElMessage.success('密码重置成功')
      isSuccess.value = true
    } else {
      ElMessage.error(response.data.message || '密码重置失败')
    }
    
    loading.value = false
  } catch (error) {
    console.error('Reset password failed:', error)
    ElMessage.error('密码重置失败，请检查验证码是否正确')
    loading.value = false
  }
}

// Go to login page
const goToLogin = () => {
  router.push('/login')
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

.form-description {
  margin-bottom: 1.5rem;
  color: #64748b;
  text-align: center;
}

:deep(.dark-mode) .form-description {
  color: #94a3b8;
}

.verification-group {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}

.verification-input {
  flex: 1;
}

.verification-button {
  width: 120px;
  flex-shrink: 0;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
}

.auth-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
  color: #64748b;
}

:deep(.dark-mode) .auth-footer {
  color: #94a3b8;
}

.auth-link {
  color: var(--el-color-primary);
  text-decoration: none;
  font-weight: 500;
}

.auth-link:hover {
  text-decoration: underline;
}

.form-actions {
  display: flex;
  justify-content: flex-start;
  margin-top: 1rem;
}

.text-button {
  background: none;
  border: none;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

:deep(.dark-mode) .text-button {
  color: #94a3b8;
}

.text-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #334155;
}

:deep(.dark-mode) .text-button:hover {
  background-color: rgba(255, 255, 255, 0.05);
  color: #e2e8f0;
}

.password-toggle {
  cursor: pointer;
}

.success-container {
  text-align: center;
  padding: 1rem;
}

.success-icon {
  font-size: 4rem;
  color: var(--el-color-success);
  margin-bottom: 1rem;
}

.success-container h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #1e293b;
}

:deep(.dark-mode) .success-container h2 {
  color: #f1f5f9;
}

.success-container p {
  color: #64748b;
  margin-bottom: 2rem;
}

:deep(.dark-mode) .success-container p {
  color: #94a3b8;
}
</style>