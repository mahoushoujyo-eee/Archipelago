<template>
  <div class="blog-card">
    <router-link :to="`/blog/${post.id}`" class="blog-link">
      <div class="blog-cover">
        <img :src="post.cover" :alt="post.title" loading="lazy" />
        <!-- <div v-if="post.isTop" class="top-badge">
          <el-icon><Top /></el-icon>
          <span>置顶</span>
        </div> -->
        <div class="blog-tags">
          <span v-for="tag in post.tags.slice(0, 3)" :key="tag" class="tag">
            {{ tag }}
          </span>
        </div>
      </div>
      
      <div class="blog-content">
        <h3 class="blog-title">{{ post.title }}</h3>
        
        <div class="blog-meta">
          <div class="author-info">
            <img :src="authorAvatar" :alt="post.author.name" class="author-avatar" />
            <span class="author-name">{{ post.author.name }}</span>
          </div>
          
          <div class="blog-date">{{ formatDate(post.publishedAt) }}</div>
        </div>
      </div>
    </router-link>
    
    <div class="blog-stats">
      <div class="stat">
        <el-icon><ChatDotRound /></el-icon>
        <span>{{ post.comments }}</span>
        评论
      </div>
      <div 
        class="stat like-stat" 
        :class="{ 'liked': post.liked }"
        @click="handleLike"
      >
        <el-icon><Star /></el-icon>
        <span>{{ post.likes }}</span>
        {{ post.liked ? '已赞' : '赞' }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, computed, watch, ref } from 'vue'
import { Top, ChatDotRound, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useBlogStore } from '../../stores/blog'
import { useUserStore } from '../../stores/user'
import { getUserAvatarUrl } from '../../utils/avatar'

// Define props
const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['like-updated'])

// Stores
const blogStore = useBlogStore()
const userStore = useUserStore()

// Avatar handling
const authorAvatar = ref('')

// Watch for post changes to update avatar
watch(() => props.post, async (newPost) => {
  if (newPost && newPost.author) {
    authorAvatar.value = await getUserAvatarUrl(newPost.author.id, newPost.author.avatar)
  }
}, { immediate: true })

// Format date
const formatDate = (date) => {
  return dayjs(date).format('MMM D, YYYY')
}

// Handle like
const handleLike = async (event) => {
  event.preventDefault()
  event.stopPropagation()
  
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再点赞')
    return
  }
  
  try {
    const userId = userStore.user.id
    const blogId = props.post.id
    
    if (props.post.liked) {
      await blogStore.unlikeBlog(blogId, userId)
      ElMessage.success('取消点赞成功')
    } else {
      await blogStore.likeBlog(blogId, userId)
      ElMessage.success('点赞成功')
    }
    
    // 通知父组件点赞状态已更新
    emit('like-updated', { blogId, liked: !props.post.liked })
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}
</script>

<style scoped>
.blog-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  background-color: white;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.blog-link {
  display: block;
  text-decoration: none;
  color: inherit;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dark-mode .blog-card {
  background-color: var(--el-bg-color);
  box-shadow: var(--el-box-shadow-light);
}

.blog-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1), 0 4px 6px rgba(0, 0, 0, 0.05);
}

:deep(.dark-mode) .blog-card:hover {
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.3), 0 4px 6px rgba(0, 0, 0, 0.2);
}

.blog-link:hover .blog-cover img {
  transform: scale(1.05);
}

.blog-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.blog-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.blog-tags {
  position: absolute;
  bottom: 12px;
  left: 12px;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.top-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: rgba(255, 193, 7, 0.95);
  color: #856404;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.25rem;
  backdrop-filter: blur(4px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.top-badge :deep(svg) {
  font-size: 0.875rem;
}

.tag {
  background-color: rgba(2, 136, 209, 0.9);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 50px;
  font-size: 0.75rem;
  font-weight: 500;
}

.blog-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.blog-title {
  margin: 0 0 0.75rem;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.dark-mode .blog-title {
  color: var(--el-text-color-primary);
}

.blog-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.author-name {
  font-size: 0.85rem;
  font-weight: 500;
  color: #4b5563;
}

.dark-mode .author-name {
  color: var(--el-text-color-regular);
}

.blog-date {
  font-size: 0.85rem;
  color: #94a3b8;
}

.blog-stats {
  display: flex;
  gap: 1rem;
  padding: 0 1.5rem 1.5rem;
}

.stat {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #94a3b8;
  font-size: 0.85rem;
}

.stat :deep(svg) {
  font-size: 1rem;
}

.like-stat {
  cursor: pointer;
  transition: color 0.3s, transform 0.2s;
  user-select: none;
}

.like-stat:hover {
  color: #f59e0b;
  transform: scale(1.05);
}

.like-stat.liked {
  color: #f59e0b;
}

.like-stat.liked :deep(svg) {
  color: #f59e0b;
}
</style>