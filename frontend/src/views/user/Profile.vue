```vue
<template>
  <div class="profile-page">
    <div class="container">
      <div class="profile-header">
        <h1>我的个人资料</h1>
      </div>
      
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="个人信息" name="profile">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-position="top"
          >
            <div class="avatar-upload">
              <el-avatar
                :size="100"
                :src="profileForm.avatar || currentAvatar"
              >
                {{ avatarFallback }}
              </el-avatar>
              <input
                ref="avatarInputRef"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleAvatarFileChange"
              />
              <el-button type="primary" plain @click="selectAvatarFile">
                更改头像
              </el-button>
            </div>
            
            <el-form-item label="姓名" prop="name">
              <el-input v-model="profileForm.name" />
            </el-form-item>
            
            <el-form-item label="电子邮件" prop="email">
              <el-input v-model="profileForm.email" disabled />
            </el-form-item>
            

            
            <el-form-item>
              <el-button
                type="primary"
                :loading="saving"
                @click="handleProfileSubmit"
              >
                保存更改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-position="top"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="changingPassword"
                @click="handlePasswordSubmit"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        

      </el-tabs>
    </div>
    
    <!-- 头像裁剪对话框 -->
    <el-dialog
      v-model="cropDialogVisible"
      title="裁剪头像"
      width="600px"
      :before-close="handleCropDialogClose"
    >
      <div class="crop-container">
        <div class="crop-info">
          <p>请按照 1:1 的比例裁剪图片作为头像</p>
        </div>
        <Cropper
          ref="cropperRef"
          :src="cropImageSrc"
          :stencil-props="{
            aspectRatio: 1
          }"
          class="cropper"
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCropDialogClose">取消</el-button>
          <el-button type="primary" @click="handleCropConfirm">确认裁剪</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'
import { userApi } from '../../api/user'
import { Cropper } from 'vue-advanced-cropper'
import 'vue-advanced-cropper/dist/style.css'
import { getUserAvatarUrl } from '../../utils/avatar'

const userStore = useUserStore()
const activeTab = ref('profile')
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const avatarInputRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)

// 头像裁剪相关
const cropDialogVisible = ref(false)
const cropImageSrc = ref('')
const cropperRef = ref(null)
const originalAvatarFile = ref(null)
const avatarFile = ref(null)
const currentAvatar = ref('')

// Avatar fallback
const avatarFallback = computed(() => {
  return userStore.user?.name ? userStore.user.name.charAt(0).toUpperCase() : '用户'
})

// 监听用户变化，更新头像
watch(() => userStore.user, async (newUser) => {
  if (newUser && newUser.id) {
    currentAvatar.value = await getUserAvatarUrl(newUser.id, newUser.avatar)
  } else {
    currentAvatar.value = ''
  }
}, { immediate: true })

// Profile form
const profileForm = reactive({
  name: userStore.user?.name || '',
  email: userStore.user?.email || '',
  avatar: ''
})

const profileRules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' },
    { min: 2, message: '姓名至少需要2个字符', trigger: 'blur' }
  ]
}

// Password form
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入您的密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validateConfirmPass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认您的密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不匹配'))
  } else {
    callback()
  }
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入您的当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码至少需要6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPass, trigger: 'blur' }
  ]
}

// Methods

// 头像相关方法
const selectAvatarFile = () => {
  if (avatarInputRef.value) {
    avatarInputRef.value.click()
  }
}

const handleAvatarFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  
  // 保存原始文件
  originalAvatarFile.value = file
  
  // 读取文件并显示裁剪对话框
  const reader = new FileReader()
  reader.onload = (e) => {
    cropImageSrc.value = e.target.result
    cropDialogVisible.value = true
  }
  reader.readAsDataURL(file)
  
  // 清空input值，以便可以重复选择同一文件
  event.target.value = ''
}

// 裁剪相关方法
const handleCropDialogClose = () => {
  cropDialogVisible.value = false
  cropImageSrc.value = ''
  originalAvatarFile.value = null
}

const handleCropConfirm = async () => {
  if (!cropperRef.value) {
    ElMessage.error('裁剪器未初始化')
    return
  }
  
  try {
    // 获取裁剪后的canvas
    const { canvas } = cropperRef.value.getResult()
    if (!canvas) {
      ElMessage.error('裁剪失败，请重试')
      return
    }
    
    // 将canvas转换为blob
    canvas.toBlob(async (blob) => {
      if (!blob) {
        ElMessage.error('裁剪失败，请重试')
        return
      }
      
      // 创建新的File对象
      const fileName = originalAvatarFile.value.name
      const fileExtension = fileName.split('.').pop()
      const newFileName = `avatar_${Date.now()}.${fileExtension}`
      
      avatarFile.value = new File([blob], newFileName, {
        type: blob.type,
        lastModified: Date.now()
      })
      
      // 上传头像
      await uploadAvatar()
      
      // 关闭对话框
      cropDialogVisible.value = false
      cropImageSrc.value = ''
      originalAvatarFile.value = null
      
      ElMessage.success('头像裁剪完成')
    }, 'image/jpeg', 0.9)
  } catch (error) {
    console.error('裁剪错误:', error)
    ElMessage.error('裁剪失败，请重试')
  }
}

// 根据文件扩展名获取Content-Type
const getImageContentType = (filename) => {
  const extension = filename.split('.').pop().toLowerCase()
  switch (extension) {
    case "jpg":
    case "jpeg":
      return "image/jpeg"
    case "png":
      return "image/png"
    case "gif":
      return "image/gif"
    case "webp":
      return "image/webp"
    case "bmp":
      return "image/bmp"
    case "svg":
      return "image/svg+xml"
    case "ico":
      return "image/x-icon"
    default:
      return "application/octet-stream"
  }
}

// 上传头像
const uploadAvatar = async () => {
  if (!avatarFile.value) {
    ElMessage.error('没有选择头像文件')
    return
  }
  
  try {
    ElMessage.info('正在获取上传地址...')
    
    // 调用API获取上传URL
    const response = await userApi.updateAvatar({
      avatar: avatarFile.value.name,
      username: userStore.user?.username,
      userId: userStore.user?.id
    })
    
    if (response.data.code !== 200 || !response.data.data) {
      throw new Error('获取上传地址失败')
    }
    
    const uploadUrl = response.data.data
    
    ElMessage.info('正在上传头像...')
    
    // 上传头像文件
    const contentType = getImageContentType(avatarFile.value.name)
    const uploadResponse = await fetch(uploadUrl, {
      method: 'PUT',
      body: avatarFile.value,
      headers: {
        'Content-Type': contentType
      }
    })
    
    if (!uploadResponse.ok) {
      throw new Error('头像上传失败')
    }
    
    // 获取上传后的URL（去掉查询参数）
    const avatarUrl = uploadUrl.split('?')[0]
    
    // 更新用户头像
    profileForm.avatar = avatarUrl
    if (userStore.user) {
      userStore.user.avatar = avatarUrl
      // 重新获取头像URL
      currentAvatar.value = await getUserAvatarUrl(userStore.user.id, avatarUrl)
    }
    
    ElMessage.success('头像上传成功')
    
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error(error.message || '头像上传失败，请重试')
  }
}

const handleProfileSubmit = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    saving.value = true
    
    // 调用API更新用户信息
    await userApi.updateUser({
      name: profileForm.name,
      id: userStore.user.id
    })
    
    // 更新本地store中的用户信息
    userStore.user.name = profileForm.name
    
    ElMessage.success('个人资料更新成功')
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('更新失败，请重试')
    }
  } finally {
    saving.value = false
  }
}

const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true
    
    await userStore.changePassword(passwordForm)
    ElMessage.success('密码修改成功')
    
    // Reset form
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value.resetFields()
  } catch (error) {
    if (error.message) {
      ElMessage.error(error.message)
    }
  } finally {
    changingPassword.value = false
  }
}


</script>

<style scoped>
.profile-page {
  padding: 2rem 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 1rem;
}

.profile-header {
  margin-bottom: 2rem;
}

h1 {
  font-size: 2rem;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.profile-tabs {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 2rem;
  box-shadow: var(--el-box-shadow-light);
  border: 1px solid var(--el-border-color);
  transition: background-color 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}

/* 黑夜模式样式 */
.dark-mode .profile-page {
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
}

.dark-mode .profile-tabs {
  background-color: var(--el-bg-color);
  box-shadow: var(--el-box-shadow-light);
  border-color: var(--el-border-color);
}

.dark-mode h1 {
  color: var(--el-text-color-primary);
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
}

/* 修复按钮文字颜色问题 */
.avatar-upload .el-button {
  color: #ffffff !important;
}

.dark-mode .avatar-upload .el-button {
  color: #ffffff !important;
}

/* 头像裁剪对话框样式 */
.crop-container {
  width: 100%;
  height: 400px;
}

.crop-info {
  margin-bottom: 15px;
  text-align: center;
}

.crop-info p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.cropper {
  width: 100%;
  height: 350px;
}

/* 黑夜模式下的头像裁剪对话框样式 */
.dark-mode .crop-container {
  background-color: var(--el-bg-color);
}

.dark-mode .crop-info p {
  color: var(--el-text-color-secondary);
}

.dark-mode .cropper {
  background-color: var(--el-fill-color-dark);
  border: 1px solid var(--el-border-color);
}

/* Element Plus 组件在黑夜模式下的样式 */
.dark-mode :deep(.el-dialog) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
}

.dark-mode :deep(.el-dialog__header) {
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
}

.dark-mode :deep(.el-dialog__body) {
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);
}

.dark-mode :deep(.el-form-item__label) {
  color: var(--el-text-color-primary);
}

.dark-mode :deep(.el-input__wrapper) {
  background-color: var(--el-fill-color);
  border-color: var(--el-border-color);
}

.dark-mode :deep(.el-input__inner) {
  background-color: var(--el-fill-color);
  color: var(--el-text-color-primary);
}

.dark-mode :deep(.el-input__inner::placeholder) {
  color: var(--el-text-color-placeholder);
}

.dark-mode :deep(.el-tabs__header) {
  background-color: var(--el-bg-color);
}

.dark-mode :deep(.el-tabs__nav-wrap::after) {
  background-color: var(--el-border-color);
}

.dark-mode :deep(.el-tabs__item) {
  color: var(--el-text-color-regular);
}

.dark-mode :deep(.el-tabs__item.is-active) {
  color: var(--el-color-primary);
}

.dark-mode :deep(.el-tabs__active-bar) {
  background-color: var(--el-color-primary);
}

@media (max-width: 768px) {
  .profile-tabs {
    padding: 1rem;
  }
  
  .crop-container {
    height: 300px;
  }
  
  .cropper {
    height: 250px;
  }
  
  /* 移动端黑夜模式样式 */
  .dark-mode .profile-tabs {
    padding: 1rem;
    background-color: var(--el-bg-color);
    border-color: var(--el-border-color);
  }
  
  .dark-mode .crop-container {
    height: 300px;
    background-color: var(--el-bg-color);
  }
  
  .dark-mode .cropper {
    height: 250px;
    background-color: var(--el-fill-color-dark);
    border-color: var(--el-border-color);
  }
}
</style>
