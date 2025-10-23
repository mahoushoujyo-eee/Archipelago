<template>
  <div class="home-container">
    <!-- Hero section -->
    <section class="hero-section">
      <div class="container hero-content">
        <div class="hero-text">
          <h1>在 <span>Archipelago</span><br class="mobile-break"> 分享你的知识</h1>
          <p>一个专注于学习和技术交流的网站，记录学习过程和实践经验，促进知识分享与讨论。</p>
          <div class="hero-buttons">
            <router-link to="/blog" class="btn-primary">
              浏览文章 <el-icon><ArrowRight /></el-icon>
            </router-link>
            <router-link v-if="!userStore.isLoggedIn" to="/register" class="btn-secondary">
              开始使用
            </router-link>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Featured posts section -->
    <section class="featured-section">
      <div class="container section-header">
        <h2>热点内容</h2>
        <router-link to="/blog" class="view-all">
          查看全部 <el-icon><ArrowRight /></el-icon>
        </router-link>
      </div>
      
      <div class="container featured-posts">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
          <el-skeleton :rows="3" animated />
        </div>
        
        <template v-else>
          <BlogPostCard 
            v-for="post in featuredPosts" 
            :key="post.id" 
            :post="post" 
            @like-updated="handleLikeUpdated"
          />
        </template>
      </div>
    </section>
    
    <!-- Newsletter section -->
    <section class="newsletter-section">
      <div class="container newsletter-content">
        <h2>订阅我们的新闻通讯</h2>
        <p>及时了解最新故事和平台更新。</p>
        
        <form @submit.prevent="subscribeNewsletter" class="newsletter-form">
          <el-input
            v-model="newsletterEmail"
            placeholder="你的电子邮件地址"
            class="newsletter-input"
          />
          <el-button type="primary" native-type="submit">订阅</el-button>
        </form>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { useBlogStore } from '../stores/blog'
import BlogPostCard from '../components/blog/BlogPostCard.vue'
import { blogApi } from '../api/blog'
import { getUserAvatarUrl } from '../utils/avatar'

const userStore = useUserStore()
const blogStore = useBlogStore()
const featuredPosts = ref([])
const loading = ref(true)
const newsletterEmail = ref('')

// Load featured posts from API
const loadFeaturedPosts = async () => {
  try {
    loading.value = true
    
    // 调用 getTop3Blogs API 获取特色文章
    const response = await blogApi.getTop3Blogs({
      pageIndex: 1,
    })
    
    // 处理响应数据结构，参考 blog store 的处理方式
    if (response.data && response.data.code === 200) {
      const blogData = response.data.data || []
      
      // 处理博客数据，解析tags字段并转换为组件期望的格式
      featuredPosts.value = await Promise.all(blogData.map(async blog => {
        const blogPost = {
          ...blog,
          id: blog.id,
          title: blog.title,
          cover: blog.imgUrl || '/default-cover.jpg',
          excerpt: blog.excerpt || '暂无摘要...',
          tags: JSON.parse(blog.tags || '[]'),
          author: {
            name: blog.nickname || '匿名用户',
            avatar: await getUserAvatarUrl(blog.userId, blog.authorAvatar)
          },
          publishedAt: blog.creationTime,
          createdAt: blog.creationTime,
          likes: blog.likes || 0, // 直接使用API响应中的likes字段
          comments: blog.comments || 0, // 直接使用API响应中的comments字段
          liked: false
        }
        
        try {
          // 如果用户已登录，检查点赞状态
          if (userStore.isLoggedIn && userStore.user?.id) {
            const isLiked = await blogStore.checkLikeStatus(blog.id, userStore.user.id)
            blogPost.liked = isLiked
          }
        } catch (error) {
          console.error(`Failed to load like status for blog ${blog.id}:`, error)
        }
        
        return blogPost
      }))
      
      console.log('Featured posts loaded:', featuredPosts.value)
    } else {
      featuredPosts.value = []
      console.log('No featured posts found or API error')
    }
  } catch (err) {
    console.error('Failed to load featured posts:', err)
    featuredPosts.value = []
  } finally {
    loading.value = false
  }
}

const subscribeNewsletter = () => {
  if (!newsletterEmail.value) {
    ElMessage.warning('请输入你的电子邮件地址')
    return
  }
  
  // Simulate API call
  ElMessage.success('感谢你订阅我们的新闻通讯！')
  newsletterEmail.value = ''
}

// Handle like update event from BlogPostCard
const handleLikeUpdated = async () => {
  // 重新加载特色博客数据以获取最新的点赞状态
  await loadFeaturedPosts()
}

onMounted(() => {
  loadFeaturedPosts()
})
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Hero section */
.hero-section {
  padding: 8rem 0 5rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4eaff 100%);
  position: relative;
}

.dark-mode .hero-section {
  background: url(../assets/title-img.png) no-repeat center/cover,
              linear-gradient(135deg, rgba(10, 10, 10, 0.8) 0%, rgba(20, 20, 20, 0.8) 100%);
  background-blend-mode: overlay;
}

.hero-content {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.hero-text {
  max-width: 800px;
}

.hero-text h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: #1e293b;
  font-family: 'Playfair Display', 'Georgia', 'Times New Roman', serif;
  background: url(../assets/title-img.png) no-repeat center/120% auto;
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  letter-spacing: 0.5px;
}

.dark-mode .hero-text h1 {
  background: none;
  -webkit-background-clip: unset;
  background-clip: unset;
  color: var(--el-text-color-primary);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.dark-mode .hero-text h1 span {
  color: transparent !important;
  background: linear-gradient(135deg, #6db33f 0%, #5cb85c 100%);
  background-clip: text;
  -webkit-background-clip: text;
  text-shadow: none;
}

.hero-text h1 span {
  color: transparent;
  background-clip: text;
  -webkit-background-clip: text;
  font-weight: 800;
}

.hero-text p {
  font-size: 1.25rem;
  color: #64748b;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.dark-mode .hero-text p {
  color: var(--el-text-color-secondary);
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.btn-primary {
  background-color: #0288d1;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  transition: background-color 0.3s;
}

.btn-primary:hover {
  background-color: #0277bd;
}

.btn-secondary {
  background-color: transparent;
  color: #0288d1;
  border: 1px solid #0288d1;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s, color 0.3s;
}

.btn-secondary:hover {
  background-color: rgba(2, 136, 209, 0.1);
}

/* Featured posts section */
.featured-section {
  padding: 4rem 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 600;
  margin: 0;
  color: #1e293b;
}

.dark-mode .section-header h2 {
  color: var(--el-text-color-primary);
}

.view-all {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #0288d1;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.view-all:hover {
  color: #0277bd;
}

.featured-posts {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

.loading-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
  width: 100%;
}

/* Newsletter section */
.newsletter-section {
  padding: 4rem 0;
  background-color: #f8fafc;
}

.dark-mode .newsletter-section {
  background-color: var(--el-fill-color);
}

.newsletter-content {
  text-align: center;
  max-width: 600px;
}

.newsletter-content h2 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1e293b;
}

.dark-mode .newsletter-content h2 {
  color: var(--el-text-color-primary);
}

.newsletter-content p {
  font-size: 1.1rem;
  color: #64748b;
  margin-bottom: 2rem;
}

.dark-mode .newsletter-content p {
  color: var(--el-text-color-secondary);
}

.newsletter-form {
  display: flex;
  gap: 0.5rem;
}

.newsletter-input {
  flex: 1;
}

/* 移动端换行控制 */
.mobile-break {
  display: none;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .mobile-break {
    display: block;
  }
  
  .hero-text h1 {
    font-size: 2.5rem;
    line-height: 1.2;
  }
  
  .hero-text p {
    font-size: 1.1rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }
  
  .btn-primary,
  .btn-secondary {
    text-align: center;
    justify-content: center;
  }
  
  .newsletter-form {
    flex-direction: column;
  }
  
  .newsletter-form button {
    margin-top: 0.5rem;
  }
}

@media (max-width: 480px) {
  .hero-text h1 {
    font-size: 2rem;
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>