<template>
  <div class="comments-management">
    <h2>Manage Comments</h2>
    
    <div class="table-actions">
      <el-input
        v-model="searchQuery"
        placeholder="Search comments..."
        prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
      
      <el-select v-model="filterStatus" placeholder="Filter by status" @change="handleStatusFilter">
        <el-option label="All" value="" />
        <el-option label="Pending" value="pending" />
        <el-option label="Approved" value="approved" />
      </el-select>
    </div>
    
    <el-table
      v-loading="loading"
      :data="comments"
      style="width: 100%"
    >
      <el-table-column prop="content" label="Comment" min-width="300">
        <template #default="{ row }">
          <div class="comment-content">
            <p>{{ row.content }}</p>
            <router-link :to="`/blog/${row.blogId}`" class="blog-link">
              on: {{ row.blogTitle }}
            </router-link>
          </div>
        </template>
      </el-table-column>
      
      <el-table-column prop="author.name" label="Author" width="150" />
      
      <el-table-column prop="createdAt" label="Posted" width="150">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'approved' ? 'success' : 'warning'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="Actions" width="300" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'pending'"
            type="success"
            link
            @click="handleApprove(row)"
          >
            <el-icon><Check /></el-icon>
            Approve
          </el-button>
          
          <el-button
            v-if="canPinComment(row)"
            :type="row.isTop ? 'warning' : 'primary'"
            link
            @click="handleToggleTop(row)"
          >
            <el-icon><Top /></el-icon>
            {{ row.isTop ? '取消置顶' : '置顶' }}
          </el-button>
          
          <el-button 
            v-if="canManageComment(row)"
            type="danger" 
            link 
            @click="handleDelete(row)"
          >
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
import { Check, Delete, Top } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { useCommentStore } from '../../stores/comment'
import { useUserStore } from '../../stores/user'

const commentStore = useCommentStore()
const userStore = useUserStore()

// State
const loading = ref(false)
const searchQuery = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const comments = ref([])
const total = ref(0)

// Methods
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const fetchComments = async () => {
  console.log('🔄 开始获取评论数据...')
  loading.value = true
  try {
    const response = await commentStore.fetchAllComments({
      page: currentPage.value,
      limit: pageSize.value,
      search: searchQuery.value,
      status: filterStatus.value
    })
    console.log('📊 评论数据获取成功:', {
      commentsCount: response.comments?.length || 0,
      total: response.total,
      comments: response.comments
    })
    comments.value = response.comments
    total.value = response.total
  } catch (error) {
    console.error('❌ 评论数据获取失败:', error)
    ElMessage.error('Failed to fetch comments')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchComments()
}

const handleStatusFilter = () => {
  currentPage.value = 1
  fetchComments()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchComments()
}

const handleApprove = async (comment) => {
  try {
    await commentStore.approveComment(comment.id)
    ElMessage.success('Comment approved successfully')
    fetchComments()
  } catch (error) {
    ElMessage.error('Failed to approve comment')
  }
}

// 权限检查函数：只有博客作者或管理员才能管理评论
const canManageComment = (comment) => {
  console.log('=== canManageComment 权限检查 ===', {
    commentId: comment.id,
    isAdmin: userStore.isAdmin,
    currentUserId: userStore.user?.id,
    blogAuthorId: comment.blogAuthorId,
    commentAuthorId: comment.author?.id,
    comment: comment
  })
  
  // 管理员可以管理所有评论
  if (userStore.isAdmin) {
    console.log('✅ 管理员权限通过')
    return true
  }
  
  // 博客作者可以管理自己博客下的评论
  if (comment.blogAuthorId && userStore.user?.id === comment.blogAuthorId) {
    console.log('✅ 博客作者权限通过')
    return true
  }
  
  // 评论作者可以管理自己的评论（仅删除）
  if (comment.author?.id && userStore.user?.id === comment.author.id) {
    console.log('✅ 评论作者权限通过')
    return true
  }
  
  console.log('❌ 权限检查失败')
  return false
}

// 置顶权限检查：只有博客作者或管理员才能置顶评论
const canPinComment = (comment) => {
  console.log('=== canPinComment 置顶权限检查 ===', {
    commentId: comment.id,
    isAdmin: userStore.isAdmin,
    currentUserId: userStore.user?.id,
    blogAuthorId: comment.blogAuthorId,
    comment: comment
  })
  
  // 管理员可以置顶所有评论
  if (userStore.isAdmin) {
    console.log('✅ 管理员置顶权限通过')
    return true
  }
  
  // 博客作者可以置顶自己博客下的评论
  if (comment.blogAuthorId && userStore.user?.id === comment.blogAuthorId) {
    console.log('✅ 博客作者置顶权限通过')
    return true
  }
  
  console.log('❌ 置顶权限检查失败')
  return false
}

const handleToggleTop = async (comment) => {
  if (!canPinComment(comment)) {
    ElMessage.error('您没有权限执行此操作')
    return
  }
  
  try {
    const action = comment.isTop ? '取消置顶' : '置顶'
    await ElMessageBox.confirm(
      `确定要${action}这条评论吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    if (!userStore.user?.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    if (comment.isTop) {
      await commentStore.cancelCommentToTop(comment.id, userStore.user.id)
      ElMessage.success('评论取消置顶成功')
    } else {
      await commentStore.addCommentToTop(comment.id, userStore.user.id)
      ElMessage.success('评论置顶成功')
    }
    
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`操作失败: ${error.message || error}`)
    }
  }
}

const handleDelete = async (comment) => {
  if (!canManageComment(comment)) {
    ElMessage.error('您没有权限删除此评论')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to delete this comment?',
      'Warning',
      {
        confirmButtonText: '删除',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )
    
    if (!userStore.user?.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    await commentStore.deleteComment(comment.id, userStore.user.id)
    ElMessage.success('Comment deleted successfully')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Failed to delete comment')
    }
  }
}

onMounted(() => {
  console.log('🚀 CommentsManagement 组件已挂载')
  console.log('👤 当前用户信息:', {
    isLoggedIn: userStore.isLoggedIn,
    isAdmin: userStore.isAdmin,
    user: userStore.user
  })
  fetchComments()
})
</script>

<style scoped>
.comments-management {
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

.comment-content {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.comment-content p {
  margin: 0;
  color: #334155;
}

:deep(.dark-mode) .comment-content p {
  color: #e2e8f0;
}

.blog-link {
  font-size: 0.875rem;
  color: var(--el-color-primary);
  text-decoration: none;
}

.blog-link:hover {
  text-decoration: underline;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .table-actions {
    flex-direction: column;
  }
}
</style>
```