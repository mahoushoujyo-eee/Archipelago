<template>
  <div class="blogs-management">
    <h2>Manage Blog Posts</h2>
    
    <div class="table-actions">
      <el-input
        v-model="searchQuery"
        placeholder="Search posts..."
        prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>
    
    <el-table
      v-loading="loading"
      :data="blogs"
      style="width: 100%"
    >
      <el-table-column prop="title" label="Title" min-width="200">
        <template #default="{ row }">
          <router-link :to="`/blog/${row.id}`" class="blog-title-link">
            {{ row.title }}
          </router-link>
        </template>
      </el-table-column>
      
      <el-table-column prop="author.name" label="Author" width="150" />
      
      <el-table-column prop="publishedAt" label="Published" width="150">
        <template #default="{ row }">
          {{ formatDate(row.publishedAt) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'published' ? 'success' : 'warning'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="Actions" width="200" fixed="right">
        <template #default="{ row }">
          <router-link :to="`/admin/blog/edit/${row.id}`">
            <el-button type="primary" link>
              <el-icon><Edit /></el-icon>
              Edit
            </el-button>
          </router-link>
          
          <el-button type="danger" link @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            Delete
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useBlogStore } from '../../stores/blog'

const blogStore = useBlogStore()

// State
const loading = ref(false)
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const blogs = ref([])
const total = ref(0)

// Methods
const formatDate = (date) => {
  return dayjs(date).format('MMM D, YYYY')
}

const fetchBlogs = async () => {
  // loading.value = true
  // try {
  //   const response = await blogStore.fetchBlogs({
  //     page: currentPage.value,
  //     limit: pageSize.value,
  //     search: searchQuery.value
  //   })
  //   blogs.value = response.blogs
  //   total.value = response.total
  // } catch (error) {
  //   ElMessage.error('Failed to fetch blogs')
  // } finally {
  //   loading.value = false
  // }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchBlogs()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBlogs()
}

const handleDelete = async (blog) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this blog post?',
      'Warning',
      {
        confirmButtonText: '删除',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )
    
    await blogStore.deleteBlog(blog.id)
    ElMessage.success('Blog post deleted successfully')
    fetchBlogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Failed to delete blog post')
    }
  }
}

onMounted(() => {
  fetchBlogs()
})
</script>

<style scoped>
.blogs-management {
  margin-top: 2rem;
}

h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #1e293b;
}

:deep(.dark-mode) h2 {
  color: #f1f5f9;
}

.table-actions {
  margin-bottom: 1rem;
  display: flex;
  gap: 1rem;
}

.blog-title-link {
  color: var(--el-color-primary);
  text-decoration: none;
}

.blog-title-link:hover {
  text-decoration: underline;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}
</style>
```