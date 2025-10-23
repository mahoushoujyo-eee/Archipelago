<template>
  <div class="admin-dashboard">
    <div class="container">
      <h1>管理员仪表盘</h1>
      
      <!-- <div class="stats-grid">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <h3>文章总数</h3>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.posts }}</div>
        </el-card>
        
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <h3>评论总数</h3>
              <el-icon><ChatDotRound /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.comments }}</div>
        </el-card>
        
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <h3>用户总数</h3>
              <el-icon><User /></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.users }}</div>
        </el-card>
      </div> -->
      
      <!-- 管理操作区域 -->
      <div class="admin-main-actions">
        <!-- 新文章创建区域 -->
        <el-card class="create-article-section">
          <template #header>
            <div class="card-header">
              <h3>文章管理</h3>
              <el-icon><Document /></el-icon>
            </div>
          </template>
          
          <div class="create-article-area">
            <div class="action-description">
              <h4>创建新文章</h4>
              <p>开始撰写新的博客文章，分享您的想法和见解</p>
            </div>
            
            <div class="action-buttons">
              <router-link to="/admin/blog/create">
                <el-button type="primary" size="large" class="create-btn">
                  <el-icon><Plus /></el-icon>
                  <span>新建文章</span>
                </el-button>
              </router-link>
              
              <!-- <router-link to="/admin/blog/manage">
                <el-button type="info" size="large" class="manage-btn">
                  <el-icon><Document /></el-icon>
                  <span>管理文章</span>
                </el-button>
              </router-link> -->
            </div>
            
            <div class="quick-stats">
              <!-- <div class="stat-item">
                <span class="stat-label">草稿</span>
                <span class="stat-value">{{ stats.posts }}</span>
              </div> -->
              <div class="stat-item">
                <span class="stat-label">已发布</span>
                <span class="stat-value">{{ totalPosts }}</span>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 公共资源上传区域 -->
        <el-card class="upload-section">
          <template #header>
            <div class="card-header">
              <h3>公共资源管理</h3>
              <el-icon><FolderOpened /></el-icon>
            </div>
          </template>
          
          <div class="upload-area">
            <div class="action-description">
              <h4>上传公共资源</h4>
              <p>上传文件作为公共资源，供所有用户访问使用</p>
            </div>
            
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleFileUpload" 
              multiple 
              style="display: none"
            >
            <el-button 
              type="success" 
              size="large"
              @click="$refs.fileInput.click()" 
              :disabled="isUploading"
              :loading="isUploading"
              class="upload-btn"
            >
              <el-icon><Upload /></el-icon>
              <span v-if="!isUploading">选择文件上传</span>
              <span v-else>上传中... {{ uploadProgress }}%</span>
            </el-button>
            
            <div class="upload-tips">
              <p>支持多文件上传，文件将作为公共资源供所有用户访问</p>
              <p>支持的文件类型：文档、图片、视频、音频等</p>
            </div>
            
            <!-- 上传进度条 -->
            <el-progress 
              v-if="isUploading" 
              :percentage="uploadProgress" 
              :stroke-width="8"
              class="upload-progress"
            />
          </div>
        </el-card>
      </div>
      
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, FolderOpened, Document, Plus } from '@element-plus/icons-vue'
import { resourceApi } from '@/api/resource'
import { ensureHttps } from '@/utils/url'
import { useUserStore } from '@/stores/user'
import { useBlogStore } from '@/stores/blog'

// 用户状态
const userStore = useUserStore()
const blogStore = useBlogStore()

// 响应式数据
const stats = ref({
  comments: 0,
  users: 0
})

// 计算属性：从blog store获取博客总数
const totalPosts = computed(() => blogStore.pagination.total)

const isUploading = ref(false)
const uploadProgress = ref(0)

// 根据文件扩展名获取ContentType
const getContentType = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase()
  const contentTypeMap = {
    // 文档类型
    'pdf': 'application/pdf',
    'doc': 'application/msword',
    'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'txt': 'text/plain',
    'rtf': 'application/rtf',
    'xls': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'ppt': 'application/vnd.ms-powerpoint',
    'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    
    // 图片类型
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'png': 'image/png',
    'gif': 'image/gif',
    'bmp': 'image/bmp',
    'webp': 'image/webp',
    'svg': 'image/svg+xml',
    'ico': 'image/x-icon',
    
    // 视频类型
    'mp4': 'video/mp4',
    'avi': 'video/x-msvideo',
    'mov': 'video/quicktime',
    'wmv': 'video/x-ms-wmv',
    'flv': 'video/x-flv',
    'webm': 'video/webm',
    'mkv': 'video/x-matroska',
    
    // 音频类型
    'mp3': 'audio/mpeg',
    'wav': 'audio/wav',
    'flac': 'audio/flac',
    'aac': 'audio/aac',
    'ogg': 'audio/ogg',
    
    // 压缩文件
    'zip': 'application/zip',
    'rar': 'application/x-rar-compressed',
    '7z': 'application/x-7z-compressed',
    'tar': 'application/x-tar',
    'gz': 'application/gzip',
    
    // 其他常见类型
    'json': 'application/json',
    'xml': 'application/xml',
    'html': 'text/html',
    'css': 'text/css',
    'js': 'application/javascript',
    'csv': 'text/csv'
  }
  
  return contentTypeMap[extension] || 'application/octet-stream'
}

// 文件上传处理
const handleFileUpload = async (event) => {
  const files = Array.from(event.target.files)
  
  if (files.length === 0) return
  
  // 检查用户是否为管理员
  if (!userStore.isLoggedIn ||  !userStore.isAdmin) 
  {
    ElMessage.warning('只有管理员可以上传公共资源')
    return
  }
  
  // 检查文件名长度
  for (const file of files) {
    if (file.name.length > 50) {
      ElMessage.error(`文件名 "${file.name}" 超出50个字符限制，请重命名后再上传`)
      event.target.value = '' // 清空文件输入框
      return
    }
  }
  
  ElMessage.info(`开始上传 ${files.length} 个公共资源文件...`)
  
  isUploading.value = true
  uploadProgress.value = 0
  
  try {
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      try {
        await uploadPublicFileToOSS(file)
        uploadProgress.value = Math.round(((i + 1) / files.length) * 100)
      } catch (error) {
        console.error('上传文件失败:', file.name, error)
        ElMessage.error(`上传文件 "${file.name}" 失败，请稍后再试`)
      }
    }
  } finally {
    isUploading.value = false
    uploadProgress.value = 0
    // 清空文件输入框
    event.target.value = ''
    
    if (files.length > 1) {
      ElMessage.success('所有公共资源文件上传完成！')
    }
  }
}

// OSS公共文件上传方法
const uploadPublicFileToOSS = async (file) => {
  try {
    // 第一步：获取OSS签名，type设置为1表示公共资源
    const requestData = {
      type: 1, // 公共资源类型
      objectName: file.name,
      bucketName: "resource-5",
      userId: userStore.user?.id || null,
      size: file.size,
      contentType: getContentType(file.name)
    }

    const signatureResponse = await resourceApi.getOssSignature(requestData)
    
    if (!signatureResponse.data || signatureResponse.data.code !== 200) {
      throw new Error("获取OSS签名失败")
    }

    const signatureData = signatureResponse.data
    const ossData = ensureHttps(signatureData.data)
    console.log('OSS上传数据:', ossData)

    const response = await fetch(ossData, {
      method: 'PUT',
      headers: {'Content-Type': getContentType(file.name)},
      body: file
    })

    console.log('上传响应状态:', response.ok)

    if (!response.ok) {
       throw new Error(`Upload failed, status: ${response.status}`)
    }
      
    ElMessage.success(`公共资源文件 "${file.name}" 上传成功！`)
      
  } catch(err) {
    console.error('上传公共资源失败:', err)
    ElMessage.error(`上传公共资源文件 "${file.name}" 失败，请稍后再试`)
    throw err
  }
}

onMounted(async () => {
  // 获取博客总数
  await blogStore.fetchBlogs({ pageIndex: 1, limit: 1 })
  
  // Fetch dashboard stats
  // This would be replaced with actual API calls
  stats.value = {
    comments: 148,
    users: 53
  }
})
</script>

<style scoped>
.admin-dashboard {
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

h1 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 2rem;
  color: #1e293b;
}

:deep(.dark-mode) h1 {
  color: #f1f5f9;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
}

:deep(.dark-mode) .stat-card {
  background-color: #1e1e1e;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 1rem;
  color: #64748b;
}

:deep(.dark-mode) .card-header h3 {
  color: #94a3b8;
}

.stat-value {
  font-size: 2rem;
  font-weight: 600;
  color: #0288d1;
  text-align: center;
  padding: 1rem 0;
}

/* 主要操作区域布局 */
.admin-main-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

/* 新文章创建区域样式 */
.create-article-section {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.create-article-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

:deep(.dark-mode) .create-article-section {
  background-color: #1e1e1e;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

:deep(.dark-mode) .create-article-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
}

.create-article-area {
  padding: 2rem;
}

.action-description {
  margin-bottom: 2rem;
  text-align: center;
}

.action-description h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}

:deep(.dark-mode) .action-description h4 {
  color: #f1f5f9;
}

.action-description p {
  margin: 0;
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.5;
}

:deep(.dark-mode) .action-description p {
  color: #94a3b8;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.create-btn, .manage-btn {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.create-btn {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  border: none;
}

.create-btn:hover {
  background: linear-gradient(135deg, #2563eb, #1e40af);
  transform: translateY(-1px);
}

.manage-btn {
  background: linear-gradient(135deg, #6b7280, #4b5563);
  border: none;
}

.manage-btn:hover {
  background: linear-gradient(135deg, #4b5563, #374151);
  transform: translateY(-1px);
}

.quick-stats {
  display: flex;
  justify-content: space-around;
  padding: 1rem;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

:deep(.dark-mode) .quick-stats {
  background-color: #0f172a;
  border-color: #334155;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.875rem;
  color: #64748b;
  margin-bottom: 0.25rem;
}

:deep(.dark-mode) .stat-label {
  color: #94a3b8;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  font-weight: 600;
  color: #3b82f6;
}

:deep(.dark-mode) .stat-value {
  color: #60a5fa;
}

/* 上传区域样式 */
.upload-section {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.upload-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

:deep(.dark-mode) .upload-section {
  background-color: #1e1e1e;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

:deep(.dark-mode) .upload-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
}

.upload-area {
  padding: 2rem;
  text-align: center;
}

.upload-btn {
  width: 100%;
  height: 48px;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #10b981, #059669);
  border: none;
  margin-bottom: 1.5rem;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: linear-gradient(135deg, #059669, #047857);
  transform: translateY(-1px);
}

.upload-tips {
  margin-top: 1rem;
  color: #64748b;
}

:deep(.dark-mode) .upload-tips {
  color: #94a3b8;
}

.upload-tips p {
  margin: 0.5rem 0;
  font-size: 0.9rem;
}

.upload-progress {
  margin-top: 1rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .admin-main-actions {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .create-article-area,
  .upload-area {
    padding: 1.5rem;
  }
  
  .action-buttons {
    gap: 0.75rem;
  }
  
  .create-btn, .manage-btn, .upload-btn {
    height: 44px;
    font-size: 0.95rem;
  }
  
  .quick-stats {
    padding: 0.75rem;
  }
  
  .stat-value {
    font-size: 1.25rem;
  }
}
</style>
```