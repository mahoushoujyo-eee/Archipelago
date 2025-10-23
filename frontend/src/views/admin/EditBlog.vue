<template>
  <div class="edit-blog">
    <div class="container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <template v-else>
        <div class="page-header">
          <h1>编辑博客文章</h1>
        </div>
        
        <el-form
          ref="formRef"
          :model="blogForm"
          :rules="rules"
          label-position="top"
        >
          <el-form-item label="标题" prop="title">
            <el-input v-model="blogForm.title" placeholder="输入博客标题" />
          </el-form-item>
          
          <el-form-item label="封面图片" prop="cover">
            <el-upload
              class="cover-upload"
              drag
              :auto-upload="false"
              :on-change="handleFileChange"
              :show-file-list="false"
              accept="image/*"
            >
              <div v-if="!coverPreview" class="upload-placeholder">
                <el-icon class="el-icon--upload"><Upload /></el-icon>
                <div class="el-upload__text">
                  将文件拖到此处或<em>点击选择</em>
                </div>
              </div>
              <div v-else class="cover-preview">
                <img :src="coverPreview" alt="封面预览" class="preview-image" />
                <div class="preview-overlay">
                  <el-button type="primary" size="small" @click.stop="changeCover">更换图片</el-button>
                  <el-button type="danger" size="small" @click.stop="removeCover">删除</el-button>
                </div>
              </div>
            </el-upload>
          </el-form-item>
          
          <el-form-item label="标签" prop="tags">
            <div class="tag-input-container">
              <div class="tags-display">
                <el-tag
                  v-for="(tag, index) in blogForm.tags"
                  :key="index"
                  closable
                  @close="removeTag(index)"
                  class="tag-item"
                >
                  {{ tag }}
                </el-tag>
              </div>
              <el-input
                v-model="tagInput"
                placeholder="输入标签后按回车添加"
                @keyup.enter="addTag($event)"
                class="tag-input"
              />
            </div>
          </el-form-item>
          
          <el-form-item label="内容" prop="content">
            <div id="vditor-container" class="editor-container" v-if="!loading"></div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSubmit">
              保存更改
            </el-button>
            <el-button @click="handle取消">取消</el-button>
          </el-form-item>
        </el-form>
      </template>
    </div>
    
    <!-- 图片裁剪对话框 -->
    <el-dialog
      v-model="cropDialogVisible"
      title="裁剪封面图片"
      width="800px"
      :before-close="handleCropDialogClose"
    >
      <div class="crop-container">
        <div class="crop-info">
          <p>请按照 4:1 的比例裁剪图片作为文章封面</p>
        </div>
        <Cropper
          ref="cropperRef"
          :src="cropImageSrc"
          :stencil-props="{
            aspectRatio: 4/1
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
import { ref, reactive, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useThemeStore } from '../../stores/theme'
import { useBlogStore } from '../../stores/blog'
import { useUserStore } from '../../stores/user'
import { blogApi } from '../../api/blog'
import { ensureHttps } from '../../utils/url'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { Cropper } from 'vue-advanced-cropper'
import 'vue-advanced-cropper/dist/style.css'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const blogStore = useBlogStore()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(true)
const saving = ref(false)
let vditorInstance = null
const isDark = computed(() => themeStore.isDark)

// Form data
const blogForm = reactive({
  title: '',
  cover: '',
  tags: [],
  content: ''
})

// 原始博客数据，用于比较是否有变化
const originalBlog = ref(null)

// 标签输入相关
const tagInput = ref('')

// 封面图片相关
const coverPreview = ref('')
const coverFile = ref(null)
const originalCoverUrl = ref('') // 保存原始封面URL

// 图片裁剪相关
const cropDialogVisible = ref(false)
const cropImageSrc = ref('')
const cropperRef = ref(null)
const originalFile = ref(null)

// 自定义验证器：检查Vditor内容
const validateContent = (rule, value, callback) => {
  if (!vditorInstance) {
    callback(new Error('编辑器未初始化'))
    return
  }
  
  const content = vditorInstance.getValue()
  if (!content || content.trim() === '') {
    callback(new Error('请填写内容'))
    return
  }
  
  if (content.length < 100) {
    callback(new Error('内容至少需要 100 个字符'))
    return
  }
  
  callback()
}

// Form validation rules
const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 3, message: '标题至少需要 3 个字符', trigger: 'blur' }
  ],
  cover: [
    { required: true, message: '请上传封面图片', trigger: 'change' }
  ],
  tags: [
    { required: true, message: '请至少添加一个标签', trigger: 'change' },
    { type: 'array', min: 1, message: '请至少添加一个标签', trigger: 'change' }
  ],
  content: [
    { validator: validateContent, trigger: 'blur' }
  ]
}

// Methods
const fetchBlog = async () => {
  try {
    const blog = await blogStore.fetchBlogById(route.params.id)
    originalBlog.value = { ...blog }
    
    Object.assign(blogForm, {
      title: blog.title,
      cover: blog.cover,
      tags: Array.isArray(blog.tags) ? blog.tags : [],
      content: blog.content
    })
    
    // 设置封面预览
    if (blog.cover) {
      coverPreview.value = blog.cover
      originalCoverUrl.value = blog.cover
    }
    
  } catch (error) {
    ElMessage.error('获取博客文章失败')
    router.push('/admin/blogs')
  } finally {
    loading.value = false
  }
}

const handleFileChange = (file) => {
  const isImage = file.raw.type.startsWith('image/')
  const isLt5M = file.raw.size / 1024 / 1024 < 5
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  
  // 保存原始文件
  originalFile.value = file.raw
  
  // 读取文件并显示裁剪对话框
  const reader = new FileReader()
  reader.onload = (e) => {
    cropImageSrc.value = e.target.result
    cropDialogVisible.value = true
  }
  reader.readAsDataURL(file.raw)
}

const changeCover = () => {
  // 触发文件选择
  const uploadInput = document.querySelector('.cover-upload input[type="file"]')
  if (uploadInput) {
    uploadInput.click()
  }
}

const removeCover = () => {
  coverPreview.value = ''
  coverFile.value = null
  blogForm.cover = ''
}

// 裁剪相关方法
const handleCropDialogClose = () => {
  cropDialogVisible.value = false
  cropImageSrc.value = ''
  originalFile.value = null
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
    canvas.toBlob((blob) => {
      if (!blob) {
        ElMessage.error('裁剪失败，请重试')
        return
      }
      
      // 创建新的File对象
      const fileName = originalFile.value.name
      const fileExtension = fileName.split('.').pop()
      const newFileName = `cropped_${Date.now()}.${fileExtension}`
      
      coverFile.value = new File([blob], newFileName, {
        type: blob.type,
        lastModified: Date.now()
      })
      
      // 创建预览URL
      const reader = new FileReader()
      reader.onload = (e) => {
        coverPreview.value = e.target.result
        blogForm.cover = 'pending' // 标记为待上传
      }
      reader.readAsDataURL(blob)
      
      // 关闭对话框
      cropDialogVisible.value = false
      cropImageSrc.value = ''
      originalFile.value = null
      
      ElMessage.success('图片裁剪完成')
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

const uploadCoverImage = async (imgUrl) => {
  if (!coverFile.value || !imgUrl) return ''
  
  try {
    // 确保上传URL使用HTTPS协议
    const secureImgUrl = ensureHttps(imgUrl)
    const contentType = getImageContentType(coverFile.value.name)
    console.log("Content-Type:", contentType)
    const response = await fetch(secureImgUrl, {
      method: 'PUT',
      body: coverFile.value,
      headers: {
        'Content-Type': contentType
      }
    })
    
    if (!response.ok) {
      throw new Error('图片上传失败')
    }
    
    // 返回不带查询参数的URL，并确保HTTPS
    return ensureHttps(secureImgUrl.split('?')[0])
  } catch (error) {
    console.error('封面上传失败:', error)
    throw new Error('封面图片上传失败')
  }
}

const uploadMarkdownContent = async (mdUrl, content) => {
  if (!mdUrl || !content) return ''
  
  try {
    // 确保上传URL使用HTTPS协议
    const secureMdUrl = ensureHttps(mdUrl)
    const response = await fetch(secureMdUrl, {
      method: 'PUT',
      body: content,
      headers: {
        'Content-Type': 'text/plain'
      }
    })
    
    if (!response.ok) {
      throw new Error('内容上传失败')
    }
    
    // 返回不带查询参数的URL，并确保HTTPS
    return ensureHttps(secureMdUrl.split('?')[0])
  } catch (error) {
    console.error('内容上传失败:', error)
    throw new Error('Markdown内容上传失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    // 先验证表单
    await formRef.value.validate()
    
    // 显示确认弹窗
    await ElMessageBox.confirm(
      '确定要保存这篇文章的修改吗？',
      '确认保存',
      {
        confirmButtonText: '确定保存',
        cancelButtonText: '取消',
        type: 'info',
        center: true
      }
    )
    
    saving.value = true
    
    // 获取内容
    const content = vditorInstance.getValue()
    
    // 准备请求参数
    const updateParams = {
      id: parseInt(route.params.id),
      title: blogForm.title,
      tags: JSON.stringify(blogForm.tags),
      userId: userStore.user?.id || userStore.userId
    }
    
    ElMessage.info('正在获取上传地址...')
    
    // 调用updateBlog API获取上传URL
    const response = await blogApi.updateBlog(updateParams)
    
    if (response.data.code !== 200 || !response.data.data) {
      throw new Error('获取上传地址失败')
    }
    
    const { contentUrl, imgUrl } = response.data.data
    
    // 上传Markdown内容
    if (contentUrl && content) {
      ElMessage.info('正在上传文章内容...')
      await uploadMarkdownContent(contentUrl, content)
    }
    
    // 上传封面图片（如果有新图片）
    if (imgUrl && coverFile.value) {
      ElMessage.info('正在上传封面图片...')
      await uploadCoverImage(imgUrl)
    }
    
    ElMessage.success('博客文章更新成功')
    
    // 跳转到博客详情页
    router.push(`/blog/${route.params.id}`)
    
  } catch (error) {
    // 如果是用户取消操作，不显示错误信息
    if (error === 'cancel') {
      return
    }
    console.error('更新失败:', error)
    ElMessage.error(error.message || '更新失败，请重试')
  } finally {
    saving.value = false
  }
}

const handle取消 = () => {
  router.back()
}

// 标签相关方法
const addTag = (event) => {
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  const tag = tagInput.value.trim()
  if (tag && !blogForm.tags.includes(tag)) {
    blogForm.tags.push(tag)
    tagInput.value = '' // 清空输入框
  } else if (blogForm.tags.includes(tag)) {
    ElMessage.warning('标签已存在')
  }
}

const removeTag = (index) => {
  blogForm.tags.splice(index, 1)
}

// 手动触发内容验证
const validateContentField = () => {
  if (formRef.value) {
    formRef.value.validateField('content')
  }
}

onMounted(async () => {
  // 先获取博客数据
  await fetchBlog()
  
  // 等待DOM完全渲染后再初始化Vditor
  await new Promise(resolve => setTimeout(resolve, 100))
  
  // Initialize Vditor
  try {
    vditorInstance = new Vditor('vditor-container', {
      height: 500,
      width: '100%',
      mode: 'ir',
      theme: isDark.value ? 'dark' : 'classic',
      placeholder: '请输入文章内容...',
      cache: {
        enable: false
      },
      toolbar: [
        'emoji',
        'headings',
        'bold',
        'italic',
        'strike',
        'link',
        '|',
        'list',
        'ordered-list',
        'check',
        'outdent',
        'indent',
        '|',
        'quote',
        'line',
        'code',
        'inline-code',
        'insert-before',
        'insert-after',
        '|',
        'upload',
        'table',
        '|',
        'undo',
        'redo',
        '|',
        'fullscreen',
        'preview'
      ],
      input: (value) => {
        // 当内容变化时触发验证
        validateContentField()
      },
      after: () => {
        console.log('Vditor initialized successfully')
        vditorInstance.setValue(blogForm.content)
        // 存储 vditor 实例到 DOM 元素上，以便主题切换时使用
        const container = document.querySelector('#vditor-container')
        if (container) {
          container.vditor = vditorInstance
        }
      }
    })
  } catch (error) {
    console.error('Vditor initialization failed:', error)
    ElMessage.error('编辑器初始化失败，请刷新页面重试')
  }
})

// 监听主题变化
watch(isDark, (newValue) => {
  if (vditorInstance) {
    vditorInstance.setTheme(newValue ? 'dark' : 'classic')
  }
})

// 组件卸载时清理vditor实例
onUnmounted(() => {
  if (vditorInstance) {
    vditorInstance.destroy()
    vditorInstance = null
  }
})
</script>

<style scoped>
.edit-blog {
  padding: 2rem 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 1rem;
}

.loading-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

h1 {
  font-size: 2rem;
  font-weight: 600;
  color: #1e293b;
}

:deep(.dark-mode) h1 {
  color: #f1f5f9;
}

.cover-upload {
  width: 100%;
  border: 1px dashed #d4d4d4;
  border-radius: 6px;
}

:deep(.dark-mode) .cover-upload {
  border-color: #4a4a4a;
}

.upload-placeholder {
  padding: 40px;
  text-align: center;
}

.cover-preview {
  position: relative;
  width: 100%;
  max-height: 300px;
  min-height: 150px;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  display: block;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cover-preview:hover .preview-overlay {
  opacity: 1;
}

:deep(.dark-mode) .cover-preview {
  background-color: #2d3748;
}

:deep(.dark-mode) .preview-overlay {
  background: rgba(0, 0, 0, 0.7);
}

.editor-container {
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
  min-height: 500px;
  width: 100%;
}

:deep(.dark-mode) .editor-container {
  border-color: #2d3748;
}

:deep(.vditor) {
  border: none !important;
  width: 100% !important;
}

:deep(.vditor-content) {
  min-height: 450px;
}

/* 确保vditor在夜间模式下正确显示 */
:deep(.dark-mode .vditor) {
  background-color: #1a202c;
  color: #e2e8f0;
}

:deep(.dark-mode .vditor-toolbar) {
  background-color: #2d3748;
  border-bottom-color: #4a5568;
}

:deep(.dark-mode .vditor-content) {
  background-color: #1a202c;
  color: #e2e8f0;
}

/* 标签输入组件样式 */
.tag-input-container {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px;
  min-height: 40px;
  background-color: #fff;
  transition: border-color 0.2s;
}

.tag-input-container:hover {
  border-color: #c0c4cc;
}

.tag-input-container:focus-within {
  border-color: #409eff;
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.tag-item {
  margin: 0;
}

.tag-input {
  border: none;
  box-shadow: none;
}

.tag-input :deep(.el-input__wrapper) {
  box-shadow: none;
  background-color: transparent;
}

/* 夜间模式样式 */
:deep(.dark-mode) .tag-input-container {
  background-color: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

:deep(.dark-mode) .tag-input-container:hover {
  border-color: #718096;
}

:deep(.dark-mode) .tag-input-container:focus-within {
  border-color: #409eff;
}

/* 图片裁剪对话框样式 */
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
  color: #666;
  font-size: 14px;
}

.cropper {
  width: 100%;
  height: 350px;
}

:deep(.dark-mode) .crop-info p {
  color: #ccc;
}

/* 确保裁剪器在暗黑模式下正常显示 */
:deep(.dark-mode) .cropper {
  background-color: #2d3748;
}
</style>
```