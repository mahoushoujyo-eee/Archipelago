<template>
  <div class="resource-container">
    <!-- 功能选择标签 -->
    <div class="tab-container">
      <button 
        class="tab-button" 
        :class="{ active: activeTab === 'public' }"
        @click="activeTab = 'public'"
      >
        公共资料
      </button>
      <button 
        v-if="userStore.isLoggedIn"
        class="tab-button" 
        :class="{ active: activeTab === 'private' }"
        @click="activeTab = 'private'"
      >
        私有网盘
      </button>
    </div>

    <!-- 公共资料模块 -->
    <div v-if="activeTab === 'public'" class="content-section">
      
      <div class="toolbar">
        <div class="search-box">
          <input 
            type="text" 
            v-model="publicSearchQuery" 
            placeholder="搜索公共资料..."
            class="search-input"
          >
          <button class="search-btn">🔍</button>
        </div>
        <div class="filter-options">
          <select v-model="publicFilter" class="filter-select">
            <option value="all">所有类型</option>
            <option value="document">文档</option>
            <option value="image">图片</option>
            <option value="video">视频</option>
            <option value="other">其他</option>
          </select>
        </div>
      </div>

      <div class="file-list">
        <div v-if="filteredPublicFiles.length === 0" class="empty-state">
          <div class="empty-icon">📂</div>
          <div class="empty-text">
            <h3>暂无公共资源</h3>
            <p>当前没有可用的公共资源文件</p>
          </div>
        </div>
        <template v-else>
          <div class="file-list-header">
            <div class="header-name"></div>
            <div class="header-name">文件</div>
            <div class="header-size">大小</div>
            <div class="header-date">上传日期</div>
            <div class="header-actions">操作</div>
          </div>
          <div 
            v-for="file in filteredPublicFiles" 
            :key="file.id" 
            class="file-item"
          >
            <div class="file-item-header">
              <div class="item-icon">
                <el-icon :size="20">
                  <component :is="getFileIcon(file.objectName)" />
                </el-icon>
              </div>
              <div class="item-name">{{ file.objectName }}</div>
              <div class="item-size">{{ formatFileSize(file.size) }}</div>
              <div class="item-date">{{ formatDate(file.createTime) }}</div>
            </div>
            <div class="item-actions">
              <button class="action-btn download-btn" @click="downloadFile(file)">下载</button>
              <button class="action-btn view-btn" @click="viewFile(file)">预览</button>
              <button 
                v-if="userStore.isLoggedIn && userStore.isAdmin" 
                class="action-btn delete-btn" 
                @click="deletePublicFile(file)"
              >
                删除
              </button>
            </div>
          </div>
        </template>
      </div>
      
      <!-- 公共资料分页 -->
      <div v-if="publicTotal > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="publicCurrentPage"
          :page-size="pageSize"
          :total="publicTotal"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePublicPageChange"
        />
      </div>
    </div>

    <!-- 私有网盘模块 -->
    <div v-if="activeTab === 'private' && userStore.isLoggedIn" class="content-section">
      
      <div class="toolbar">
        <div class="upload-section">
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileUpload" 
            multiple 
            style="display: none"
          >
          <button class="upload-btn" @click="$refs.fileInput.click()" :disabled="isUploading">
            <span v-if="!isUploading">📤 上传文件</span>
            <span v-else>⏳ 上传中... {{ uploadProgress }}%</span>
          </button>
        </div>
        <div class="search-box">
          <input 
            type="text" 
            v-model="privateSearchQuery" 
            placeholder="搜索我的文件..."
            class="search-input"
          >
          <button class="search-btn">🔍</button>
        </div>
        <div class="filter-options">
          <select v-model="privateFilter" class="filter-select">
            <option value="all">所有类型</option>
            <option value="document">文档</option>
            <option value="image">图片</option>
            <option value="video">视频</option>
            <option value="other">其他</option>
          </select>
        </div>
      </div>

      <div class="storage-info">
        <div class="storage-bar">
          <div class="storage-used" :style="{ width: storagePercentage + '%' }"></div>
        </div>
        <p class="storage-text">已使用 {{ usedStorage }}GB / {{ totalStorage }}GB</p>
      </div>

      <div class="file-list">
        <div v-if="filteredPrivateFiles.length === 0" class="empty-state">
          <div class="empty-icon">📁</div>
          <div class="empty-text">
            <h3>暂无私人资源</h3>
            <p>您还没有上传任何私人文件，点击上方的上传按钮开始使用吧！</p>
          </div>
        </div>
        <template v-else>
          <div class="file-list-header private">
            <div class="header-name"></div>
            <div class="header-name">文件</div>
            <div class="header-size">大小</div>
            <div class="header-date">上传日期</div>
            <div class="header-actions">操作</div>
          </div>
          <div 
            v-for="file in filteredPrivateFiles" 
            :key="file.id" 
            class="file-item private"
          >
            <div class="file-item-header">
              <div class="item-icon">
                <el-icon :size="20">
                  <component :is="getFileIcon(file.objectName || file.name)" />
                </el-icon>
              </div>
              <div class="item-name">{{ file.objectName || file.name }}</div>
              <div class="item-size">{{ formatFileSize(file.size) }}</div>
              <div class="item-date">{{ formatDate(file.createTime || file.uploadDate) }}</div>
            </div>
            <div class="item-actions">
              <button class="action-btn download-btn" @click="downloadFile(file)">下载</button>
              <button class="action-btn view-btn" @click="viewFile(file)">预览</button>
              <button class="action-btn delete-btn" @click="deleteFile(file)">删除</button>
              <button class="action-btn share-btn" @click="shareFile(file)">分享</button>
            </div>
          </div>
        </template>
      </div>
      
      <!-- 私有网盘分页 -->
      <div v-if="privateTotal > 0" class="pagination-container">
        <el-pagination
          v-model:current-page="privateCurrentPage"
          :page-size="pageSize"
          :total="privateTotal"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePrivatePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElPagination } from 'element-plus'
import { 
  Document, 
  Picture, 
  VideoPlay, 
  Microphone, 
  Box, 
  Files, 
  Monitor, 
  Setting, 
  Folder,
  DocumentCopy,
  DataAnalysis,
  Document as DocumentIcon,
  Cpu,
  Coffee,
  Connection
} from '@element-plus/icons-vue'
import { resourceApi } from '@/api/resource'
import { useUserStore } from '@/stores/user'
import { ensureHttps } from '@/utils/url'

// 用户状态
const userStore = useUserStore()

// 响应式数据
const activeTab = ref('public')
const publicSearchQuery = ref('')
const privateSearchQuery = ref('')
const publicFilter = ref('all')
const privateFilter = ref('all')
const usedStorage = ref(2.5)
const totalStorage = ref(10)
const isUploading = ref(false)
const uploadProgress = ref(0)

// 分页相关数据
const publicCurrentPage = ref(1)
const privateCurrentPage = ref(1)
const pageSize = ref(10)
const publicTotal = ref(0)
const privateTotal = ref(0)

// 存储桶名称
const bucketName = ref('resource-5')

// 公共资料数据
const publicFiles = ref([])

// 私有文件数据
const privateFiles = ref([])

// 计算属性
const storagePercentage = computed(() => {
  return (usedStorage.value / totalStorage.value) * 100
})

const filteredPublicFiles = computed(() => {
  return publicFiles.value.filter(file => {
    const matchesSearch = file.objectName.toLowerCase().includes(publicSearchQuery.value.toLowerCase())
    const matchesFilter = publicFilter.value === 'all' || getFileType(file.objectName) === publicFilter.value
    return matchesSearch && matchesFilter
  })
})

const filteredPrivateFiles = computed(() => {
  return privateFiles.value.filter(file => {
    const fileName = file.objectName || file.name || ''
    const matchesSearch = fileName.toLowerCase().includes(privateSearchQuery.value.toLowerCase())
    const fileType = getFileType(fileName)
    const matchesFilter = privateFilter.value === 'all' || fileType === privateFilter.value
    return matchesSearch && matchesFilter
  })
})

// 获取公共资源计数
const fetchPublicCount = async () => {
  try {
    const response = await resourceApi.getPublicCount()
    if (response.data && response.data.code === 200) {
      publicTotal.value = response.data.data
    }
  } catch (error) {
    console.error('获取公共资源计数失败:', error)
  }
}

// 获取公共资源数据
const fetchPublicResources = async (pageIndex = 1) => {
  try {
    const params = {
      pageIndex: pageIndex,
      pageSize: pageSize.value
    }
    const response = await resourceApi.getPublicResources(params)
    if (response.data && response.data.code === 200) {
      publicFiles.value = response.data.data
    }
  } catch (error) {
    console.error('获取公共资源失败:', error)
    ElMessage.error('获取公共资源失败，请稍后再试')
  }
}

// 获取私人资源计数
const fetchPrivateCount = async () => {
  if (!userStore.isLoggedIn) return
  
  try {
    const params = {
      userId: userStore.user?.id
    }
    const response = await resourceApi.getPrivateCount(params)
    if (response.data && response.data.code === 200) {
      privateTotal.value = response.data.data
    }
  } catch (error) {
    console.error('获取私人资源计数失败:', error)
  }
}

// 获取存储使用量
const fetchStorageUsage = async () => {
  if (!userStore.isLoggedIn) return
  
  try {
    const params = {
      userId: userStore.user?.id,
      bucketName: bucketName.value
    }
    const response = await resourceApi.getPrivateUsage(params)
    if (response.data && response.data.code === 200) {
      // 后端返回Long类型的字节数，转换为GB
      const usageInBytes = response.data.data
      usedStorage.value = (usageInBytes / (1024 * 1024 * 1024)).toFixed(2)
    }
  } catch (error) {
    console.error('获取存储使用量失败:', error)
  }
}

// 获取私人资源数据
const fetchPrivateResources = async (pageIndex = 1) => {
  if (!userStore.isLoggedIn) return
  
  try {
    const params = {
      userId: userStore.user?.id,
      pageIndex: pageIndex,
      pageSize: pageSize.value
    }
    const response = await resourceApi.getPrivateResource(params)
    if (response.data && response.data.code === 200) {
      const privateData = response.data.data
      privateFiles.value = privateData
    }
  } catch (error) {
    console.error('获取私人资源失败:', error)
    ElMessage.error('获取私人资源失败，请稍后再试')
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 监听用户登录状态变化
watch(() => userStore.isLoggedIn, (newValue) => {
  // 如果用户登出且当前在私有网盘页面，自动切换到公共资料
  if (!newValue && activeTab.value === 'private') {
    activeTab.value = 'public'
  }
})

// 分页事件处理
const handlePublicPageChange = (page) => {
  publicCurrentPage.value = page
  fetchPublicResources(page)
}

const handlePrivatePageChange = (page) => {
  privateCurrentPage.value = page
  fetchPrivateResources(page)
}

// 监听标签页切换
watch(activeTab, async (newTab) => {
  if (newTab === 'public') {
    await fetchPublicCount()
    await fetchPublicResources(publicCurrentPage.value)
  } else if (newTab === 'private' && userStore.isLoggedIn) {
    await fetchPrivateCount()
    await fetchPrivateResources(privateCurrentPage.value)
    await fetchStorageUsage()
  }
})

// 组件挂载时获取数据
onMounted(async () => {
  if (activeTab.value === 'public') {
    await fetchPublicCount()
    await fetchPublicResources(publicCurrentPage.value)
  } else if (activeTab.value === 'private' && userStore.isLoggedIn) {
    await fetchPrivateCount()
    await fetchPrivateResources(privateCurrentPage.value)
    await fetchStorageUsage()
  }
})

// 方法
const handleFileUpload = async (event) => {
  const files = Array.from(event.target.files)
  
  if (files.length === 0) return
  
  // 检查用户是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再上传文件')
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
  
  ElMessage.info(`开始上传 ${files.length} 个文件...`)
  
  isUploading.value = true
  uploadProgress.value = 0
  
  try {
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      try {
        await uploadFileToOSS(file)
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
      ElMessage.success('所有文件上传完成！')
    }
  }
}

// OSS文件上传方法
const uploadFileToOSS = async (file) => {
  try {
    // 第一步：获取OSS签名
    const requestData = {
      type: 0,
      objectName: file.name,
      bucketName: "resource-5",
      userId: userStore.user?.id || null, // 从用户状态中获取
      size: file.size,
      contentType: getContentType(file.name)
    }

    const signatureResponse = await resourceApi.getOssSignature(requestData)
    
    if (!signatureResponse.data || signatureResponse.data.code !== 200) {
      throw new Error("获取OSS签名失败")
    }

    const signatureData = signatureResponse.data
    const ossData = ensureHttps(signatureData.data)
    console.log(ossData)

    const response = await fetch(ossData, 
    {
      method: 'PUT',
      headers: {'Content-Type':getContentType(file.name)},
      body: file
    });

    console.log(response.ok)

    if (!response.ok)
    {
       throw new Error(`Upload failed, status: ${response.status}`);
    }
      // 上传成功后重新获取私人资源列表和计数
      await fetchPrivateCount()
      await fetchPrivateResources(privateCurrentPage.value)
      await fetchStorageUsage()
      
      ElMessage.success(`文件 "${file.name}" 上传成功！`)
      
      // // 解析回调信息（如果需要）
      // const callbackData = await uploadResponse.json()
      // console.log("上传回调信息:", callbackData)
      
    } catch(err) 
    {
      ElMessage.error(`上传文件 "${file.name}" 失败，请稍后再试`)
    }
}

const getFileType = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase()
  if (['pdf', 'doc', 'docx', 'txt'].includes(extension)) return 'document'
  if (['jpg', 'jpeg', 'png', 'gif'].includes(extension)) return 'image'
  if (['mp4', 'avi', 'mov'].includes(extension)) return 'video'
  return 'other'
}

// 根据文件扩展名获取对应的Element Plus图标组件
const getFileIcon = (fileName) => {
  if (!fileName) return Document
  
  const extension = fileName.split('.').pop().toLowerCase()
  
  // 文档类型
  if (['pdf', 'doc', 'docx', 'txt', 'md', 'rtf'].includes(extension)) return Document
  if (['xls', 'xlsx'].includes(extension)) return DataAnalysis
   // PPT类型
   if (['ppt', 'pptx'].includes(extension)) return DocumentIcon
  
  // 图片类型
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg', 'ico'].includes(extension)) return Picture
  
  // 视频类型
  if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'webm', 'mkv'].includes(extension)) return VideoPlay
  
  // 音频类型
  if (['mp3', 'wav', 'flac', 'aac', 'ogg'].includes(extension)) return Microphone
  
  // 压缩文件
  if (['zip', 'rar', '7z', 'tar', 'gz'].includes(extension)) return Box
  
  // 代码文件
  if (['js', 'ts', 'jsx', 'tsx', 'json', 'xml'].includes(extension)) return Files
  if (['html', 'htm'].includes(extension)) return Monitor
  if (['css', 'scss', 'sass', 'less'].includes(extension)) return Setting
  if (['py', 'php', 'sql'].includes(extension)) return DocumentCopy
  if (['java'].includes(extension)) return Coffee
  if (['cpp', 'c', 'h'].includes(extension)) return Cpu
  
  // 其他常见类型
  if (['exe', 'msi'].includes(extension)) return Setting
  if (['iso', 'dmg'].includes(extension)) return Box
  if (['font', 'ttf', 'otf', 'woff'].includes(extension)) return Files
  
  // 默认文件图标
  return Document
}

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

const downloadFile = async (file) => {
  try {
    // 调用API获取下载URL
    const response = await resourceApi.getDownloadUrl(file)
    
    if (response.data && response.data.code === 200) {
      const downloadUrl = ensureHttps(response.data.data)
      
      // 创建临时链接并触发下载
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = file.objectName
      link.target = '_blank'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success(`文件 "${file.objectName}" 开始下载`)
      console.log('文件下载开始:', file.objectName)
    } else {
      throw new Error('获取下载链接失败')
    }
  } catch (error) {
    console.error('下载文件失败:', error)
    ElMessage.error(`下载文件 "${file.objectName}" 失败，请稍后再试`)
  }
}

const viewFile = (file) => {
  console.log('预览文件:', file.objectName || file.name)
  ElMessage.info('文件预览功能正在开发中，敬请期待！')
  // 这里应该实现文件预览逻辑
}

const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件 "${file.objectName || file.name}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 调用删除API
    const fileWithBucket = {
      ...file,
      bucketName: bucketName.value
    }
    const response = await resourceApi.deleteResource(fileWithBucket)
    
    if (response.data && response.data.code === 200) {
      // 删除成功后重新获取私人资源列表和计数
      await fetchPrivateCount()
      await fetchPrivateResources(privateCurrentPage.value)
      await fetchStorageUsage()
      
      ElMessage.success(`文件 "${file.objectName || file.name}" 删除成功！`)
    } else {
      throw new Error('删除文件失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
      ElMessage.error('删除文件失败，请稍后再试')
    }
  }
}

const shareFile = async (file) => {
  try {
    console.log('分享文件:', file.objectName || file.name)
    
    // 调用API获取分享URL（使用现有的getDownloadUrl方法）
    const response = await resourceApi.getDownloadUrl(file)
    
    if (response.data && response.data.code === 200) {
      const shareUrl = ensureHttps(response.data.data)
      
      // 复制到剪贴板
      await navigator.clipboard.writeText(shareUrl)
      
      ElMessage.success({
        message: `文件 "${file.objectName || file.name}" 的分享链接已复制到剪贴板！`,
        duration: 3000
      })
    } else {
      throw new Error('获取分享链接失败')
    }
  } catch (error) {
    console.error('生成分享链接失败:', error)
    ElMessage.error('生成分享链接失败，请稍后再试')
  }
}

// 删除公共资源文件
const deletePublicFile = async (file) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公共资源文件 "${file.objectName || file.name}" 吗？此操作不可恢复。`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    // 调用删除API
    const fileWithBucket = {
      ...file,
      bucketName: bucketName.value
    }
    const response = await resourceApi.deleteResource(fileWithBucket)
    
    if (response.data && response.data.code === 200) {
      // 删除成功后重新获取公共资源列表和计数
      await fetchPublicCount()
      await fetchPublicResources(publicCurrentPage.value)
      
      ElMessage.success(`公共资源文件 "${file.objectName || file.name}" 删除成功！`)
    } else {
      throw new Error('删除公共资源文件失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公共资源文件失败:', error)
      ElMessage.error('删除公共资源文件失败，请稍后再试')
    }
  }
}
</script>

<style scoped>
.resource-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.resource-header {
  text-align: center;
  margin-bottom: 30px;
}

.resource-header h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 10px;
}

.resource-header p {
  color: #666;
  font-size: 1.1rem;
}

.tab-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-button {
  padding: 12px 24px;
  border: none;
  background: none;
  font-size: 1.1rem;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.tab-button:hover {
  background-color: #f8f9fa;
}

.tab-button.active {
  color: #007bff;
  border-bottom-color: #007bff;
  font-weight: 600;
}

.content-section {
  margin-top: 20px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 5px;
}

.section-header p {
  color: #666;
}

.toolbar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.search-input {
  padding: 10px 15px;
  border: none;
  outline: none;
  min-width: 200px;
}

.search-btn {
  padding: 10px 15px;
  border: none;
  background-color: #f8f9fa;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: #e9ecef;
}

.filter-select {
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  outline: none;
}

.upload-btn {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.3s;
}

.upload-btn:hover {
  background-color: #218838;
}

.upload-btn:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.upload-btn:disabled:hover {
  background-color: #6c757d;
}

.storage-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.storage-bar {
  width: 100%;
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.storage-used {
  height: 100%;
  background-color: #007bff;
  transition: width 0.3s ease;
}

.storage-text {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.file-list {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background-color: white;
  overflow: hidden;
}

.file-list-header {
  display: grid;
  grid-template-columns: 40px 1fr 80px 120px;
  gap: 15px;
  padding: 15px 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  font-weight: 600;
  color: #495057;
  font-size: 0.9rem;
}

.file-list-header.private {
  grid-template-columns: 40px 1fr 100px 120px;
}

.file-item {
  padding: 15px 20px;
  border-bottom: 1px solid #f1f3f4;
  transition: background-color 0.2s ease;
}

.file-item:hover {
  background-color: #f8f9fa;
}

.file-item:last-child {
  border-bottom: none;
}

/* 桌面端样式 */
.file-item-header {
  display: grid;
  grid-template-columns: 40px 1fr 80px 120px;
  gap: 15px;
  align-items: center;
}

.file-item.private .file-item-header {
  grid-template-columns: 40px 1fr 100px 120px;
}

.item-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 10px;
}

/* 桌面端恢复原有布局 */
@media (min-width: 769px) {
  .file-item {
    display: grid;
    grid-template-columns: 40px 1fr 80px 120px 180px;
    gap: 15px;
    align-items: center;
    padding: 15px 20px;
  }
  
  .file-item.private {
    grid-template-columns: 40px 1fr 100px 120px 250px;
  }
  
  .file-item-header {
    display: contents;
  }
  
  .item-actions {
    display: flex;
    gap: 8px;
    margin-top: 0;
  }
  
  .file-list-header {
    grid-template-columns: 40px 1fr 80px 120px 180px;
  }
  
  .file-list-header.private {
    grid-template-columns: 40px 1fr 100px 120px 250px;
  }
}

.item-icon {
  font-size: 1.2rem;
  text-align: center;
}

.item-name {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.item-size {
  color: #666;
  font-size: 0.9rem;
}

.item-date {
  color: #666;
  font-size: 0.9rem;
}

.item-uploader {
  color: #666;
  font-size: 0.9rem;
}



.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: background-color 0.3s;
}

.download-btn {
  background-color: #007bff;
  color: white;
}

.download-btn:hover {
  background-color: #0056b3;
}

.view-btn {
  background-color: #6c757d;
  color: white;
}

.view-btn:hover {
  background-color: #545b62;
}

.delete-btn {
  background-color: #dc3545;
  color: white;
}

.delete-btn:hover {
  background-color: #c82333;
}

.share-btn {
  background-color: #28a745;
  color: white;
}

.share-btn:hover {
  background-color: #218838;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #666;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.empty-text p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #999;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .resource-container {
    padding: 15px;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-input {
    min-width: auto;
    flex: 1;
  }
  
  .file-list-header {
    grid-template-columns: 30px 1fr 60px 80px;
    gap: 10px;
    padding: 10px 15px;
    font-size: 0.8rem;
  }
  
  .file-list-header.private {
    grid-template-columns: 30px 1fr 60px 80px;
  }
  
  .file-list-header div:last-child {
    display: none;
  }
  
  .file-item {
    display: block;
    padding: 15px;
    border-bottom: 1px solid #f1f3f4;
  }
  
  .file-item.private {
    display: block;
  }
  
  .file-item-header {
    display: grid;
    grid-template-columns: 30px 1fr 60px 80px;
    gap: 10px;
    align-items: center;
    margin-bottom: 10px;
  }
  
  .item-actions {
    display: flex;
    flex-direction: row;
    gap: 8px;
    flex-wrap: wrap;
    justify-content: flex-start;
    margin-top: 10px;
  }
  
  .action-btn {
    font-size: 0.8rem;
    padding: 6px 12px;
    flex: 1;
    min-width: 60px;
    text-align: center;
  }
}

/* 黑夜模式样式 */
.dark-mode {
  .resource-container {
    background-color: var(--el-bg-color-page);
    color: var(--el-text-color-primary);
  }
  
  .tab-container {
    border-bottom-color: var(--el-border-color);
  }
  
  .tab-button {
    color: var(--el-text-color-regular);
    background-color: transparent;
  }
  
  .tab-button:hover {
    background-color: var(--el-fill-color-light);
  }
  
  .tab-button.active {
    color: var(--el-color-primary);
    border-bottom-color: var(--el-color-primary);
  }
  
  .search-box {
    border-color: var(--el-border-color);
  }
  
  .search-input {
    background-color: var(--el-bg-color);
    color: var(--el-text-color-primary);
    border-color: var(--el-border-color);
  }
  
  .search-input::placeholder {
    color: var(--el-text-color-placeholder);
  }
  
  .search-btn {
    background-color: var(--el-fill-color);
    color: var(--el-text-color-regular);
  }
  
  .search-btn:hover {
    background-color: var(--el-fill-color-dark);
  }
  
  .filter-select {
    background-color: var(--el-bg-color);
    color: var(--el-text-color-primary);
    border-color: var(--el-border-color);
  }
  
  .upload-btn {
    background-color: var(--el-color-success);
  }
  
  .upload-btn:hover {
    background-color: var(--el-color-success-dark);
  }
  
  .storage-info {
    background-color: var(--el-fill-color);
  }
  
  .storage-bar {
    background-color: var(--el-fill-color-dark);
  }
  
  .storage-used {
    background-color: var(--el-color-primary);
  }
  
  .storage-text {
    color: var(--el-text-color-secondary);
  }
  
  .file-list {
    background-color: var(--el-bg-color);
    border-color: var(--el-border-color);
  }
  
  .file-list-header {
    background-color: var(--el-fill-color-light);
    border-bottom-color: var(--el-border-color);
    color: var(--el-text-color-primary);
  }
  
  .file-item {
    border-bottom-color: var(--el-border-color-lighter);
  }
  
  .file-item:hover {
    background-color: var(--el-fill-color-light);
  }
  
  .item-name {
    color: var(--el-text-color-primary);
  }
  
  .item-size,
  .item-date,
  .item-uploader {
    color: var(--el-text-color-secondary);
  }
  
  .download-btn {
    background-color: var(--el-color-primary);
  }
  
  .download-btn:hover {
    background-color: var(--el-color-primary-dark);
  }
  
  .view-btn {
    background-color: var(--el-fill-color-darker);
  }
  
  .view-btn:hover {
    background-color: var(--el-border-color-darker);
  }
  
  .delete-btn {
    background-color: var(--el-color-danger);
  }
  
  .delete-btn:hover {
    background-color: var(--el-color-danger-dark);
  }
  
  .share-btn {
    background-color: var(--el-color-success);
  }
  
  .share-btn:hover {
    background-color: var(--el-color-success-dark);
  }
}
</style>