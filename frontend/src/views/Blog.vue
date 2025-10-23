<template>
  <div class="blog-page">
    <div class="container">
      <!-- Header -->
      <div class="page-header">
        <h1>博客文章</h1>
        <div class="filters">
          <div class="search-container">
            <el-input
              v-model="searchQuery"
              placeholder="搜索文章..."
              prefix-icon="Search"
              clearable
              size="large"
              @keyup.enter="handleSearch"
              class="search-input"
            />
            <el-button
              type="primary"
              size="large"
              @click="handleSearch"
              class="search-button"
            >
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </div>
      </div>

      <!-- Blog posts grid -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
        <el-skeleton :rows="3" animated />
      </div>

      <div v-else-if="blogs.length" class="blog-grid">
        <div v-for="blog in blogs" :key="blog.id" class="blog-item">
          <BlogPostCard :post="blog" @like-updated="handleLikeUpdated" />
          <el-button
            v-if="isAdmin"
            class="admin-toggle-btn"
            :type="blog.isTop ? 'info' : 'success'"
            size="small"
            :icon="blog.isTop ? 'Bottom' : 'Top'"
            @click="handleToggleTop(blog)"
            :style="blog.isTop ? 'background-color: #87CEEB; border-color: #87CEEB; color: white;' : 'background-color: #67C23A; border-color: #67C23A; color: white;'"
          >
            {{ blog.isTop ? '取消置顶' : '置顶' }}
          </el-button>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-empty description="未找到文章" />
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useBlogStore } from '../stores/blog'
import { useUserStore } from '../stores/user'
import BlogPostCard from '../components/blog/BlogPostCard.vue'

const blogStore = useBlogStore()
const userStore = useUserStore()

// State
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(9)
const loading = ref(false)

// Computed
const blogs = computed(() => blogStore.blogs)
const total = computed(() => blogStore.pagination.total)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const isAdmin = computed(() => userStore.isAdmin)

// Methods
const fetchBlogs = async () => {
  loading.value = true
  try {
    await blogStore.fetchBlogs({
      pageIndex: currentPage.value,
      limit: pageSize.value,
      search: searchQuery.value
    })
  } catch (error) {
    console.error('获取博客失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchBlogs()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBlogs()
}

// Handle like update event from BlogPostCard
const handleLikeUpdated = async () => {
  // 重新加载博客列表以获取最新的点赞状态
  await fetchBlogs()
  
  // 重新检查所有博客的点赞状态
  if (userStore.isLoggedIn && userStore.user?.id) {
    try {
      const userId = userStore.user.id
      for (const blog of blogs.value) {
        await blogStore.checkLikeStatus(blog.id, userId)
      }
    } catch (err) {
      console.error('检查点赞状态失败:', err)
    }
  }
}

// 处理置顶操作
const handleToggleTop = async (blog) => {
  try {
    const action = blog.isTop ? '取消置顶' : '置顶'
    await ElMessageBox.confirm(
      `确定要${action}这篇文章吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 调用后端API进行置顶操作
    await blogStore.toggleTopBlog(blog.id, !blog.isTop)
    
    ElMessage.success(`${action}操作成功！`)
    // 重新获取博客列表
    await fetchBlogs()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('置顶操作失败:', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

// Lifecycle
onMounted(async () => {
  await fetchBlogs()
  
  // 如果用户已登录，检查所有博客的点赞状态
  if (userStore.isLoggedIn && userStore.user?.id) {
    try {
      const userId = userStore.user.id
      for (const blog of blogs.value) {
        await blogStore.checkLikeStatus(blog.id, userId)
      }
    } catch (err) {
      console.error('检查点赞状态失败:', err)
    }
  }
})
</script>

<style scoped>
.blog-page {
  padding: 2rem 0;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1e293b;
}

:deep(.dark-mode) .page-header h1 {
  color: #f1f5f9;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-container {
  display: flex;
  gap: 0.75rem;
  align-items: center;
  width: 100%;
}

.search-input {
  flex: 1;
}

.search-button {
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.blog-item {
  position: relative;
  display: flex;
  flex-direction: column;
}

.admin-toggle-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
  font-size: 12px;
  padding: 4px 8px;
  backdrop-filter: blur(4px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.loading-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
  }
  
  .search-container {
    max-width: 100%;
  }
  
  .search-button {
    min-width: 80px;
  }
  
  .blog-grid {
    grid-template-columns: 1fr;
  }
  
  .admin-toggle-btn {
    position: static;
    margin-top: 8px;
    width: 100%;
    box-shadow: none;
  }
}
</style>