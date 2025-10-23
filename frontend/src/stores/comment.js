import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { commentApi } from '../api/comment'
import { getUserAvatarUrl } from '../utils/avatar'

export const useCommentStore = defineStore('comment', () => {
  const comments = ref([])
  const pagination = ref({
    page: 1,
    limit: 10,
    total: 0
  })
  const loading = ref(false)
  const error = ref(null)

  // Get comments for a blog
  async function fetchComments(blogId, params = {}) {
    try {
      loading.value = true
      error.value = null
      
      // 首先获取评论总数
      const countResponse = await commentApi.getCommentCount({
        blogId: parseInt(blogId)
      })
      let totalCount = 0
      if (countResponse.data && countResponse.data.code === 200) {
        totalCount = countResponse.data.data || 0
      }
      
      const response = await commentApi.getComments({
        blogId: parseInt(blogId),
        pageIndex: params.page || pagination.value.page,
      })
      
      if (response.data && response.data.code === 200) {
        const commentData = response.data.data || []
        
        // 处理评论数据，转换为组件期望的格式
        comments.value = await Promise.all(commentData.map(async comment => ({
          id: comment.id,
          content: comment.content,
          author: {
            id: comment.userId,
            name: comment.nickname || '匿名用户',
            avatar: await getUserAvatarUrl(comment.userId, comment.userAvatar)
          },
          createdAt: comment.creationTime,
          blogId: comment.blogId,
          isTop: comment.type === 1 // type为1表示置顶
        })))
        
        // Sort comments: pinned comments first
        comments.value.sort((a, b) => {
          if (a.isTop && !b.isTop) return -1
          if (!a.isTop && b.isTop) return 1
          return new Date(b.createdAt) - new Date(a.createdAt)
        })
        
        pagination.value = {
          page: params.page || pagination.value.page,
          limit: params.limit || pagination.value.limit,
          total: totalCount // 使用计数接口获取的总数
        }
      } else {
        comments.value = []
        pagination.value.total = 0
      }
      
      return { comments: comments.value, pagination: pagination.value }
    } catch (err) {
      error.value = err.message || 'Failed to fetch comments'
      comments.value = []
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Add a comment
  async function addComment(blogId, content, userId) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.addComment({
        blogId: parseInt(blogId),
        content: content,
        userId: parseInt(userId)
      })
      
      if (response.data && response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data?.message || 'Failed to add comment')
      }
    } catch (err) {
      error.value = err.message || 'Failed to add comment'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Delete a comment
  async function deleteComment(commentId, userId) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.deleteComment({
        id: parseInt(commentId),
        userId: parseInt(userId)
      })
      
      if (response.data && response.data.code === 200) {
        // 从本地列表中移除评论
        comments.value = comments.value.filter(c => c.id !== commentId)
        pagination.value.total -= 1
        return true
      } else {
        throw new Error(response.data?.message || 'Failed to delete comment')
      }
    } catch (err) {
      error.value = err.message || 'Failed to delete comment'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Get all comments (admin)
  async function fetchAllComments(params = {}) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.getAllComments({
        pageIndex: params.page || pagination.value.page,
        limit: params.limit || pagination.value.limit,
        search: params.search || '',
        status: params.status || ''
      })
      
      if (response.data && response.data.code === 200) {
        const commentData = response.data.data || []
        
        // 处理评论数据，转换为组件期望的格式
        const processedComments = await Promise.all(commentData.map(async comment => ({
          id: comment.id,
          content: comment.content,
          author: {
            id: comment.userId,
            name: comment.nickname || '匿名用户',
            avatar: await getUserAvatarUrl(comment.userId, comment.userAvatar)
          },
          createdAt: comment.creationTime,
          blogId: comment.blogId,
          blogTitle: comment.blogTitle || '未知博客',
          blogAuthorId: comment.blogAuthorId, // 博客作者ID，用于权限检查
          status: comment.status || 'approved',
          isTop: comment.isTop || false
        })))
        
        return {
          comments: processedComments,
          total: response.data.total || commentData.length,
          page: params.page || pagination.value.page,
          limit: params.limit || pagination.value.limit
        }
      } else {
        return {
          comments: [],
          total: 0,
          page: params.page || pagination.value.page,
          limit: params.limit || pagination.value.limit
        }
      }
    } catch (err) {
      error.value = err.message || 'Failed to fetch all comments'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Approve a comment
  async function approveComment(commentId) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.approveComment(commentId)
      
      if (response.data && response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data?.message || 'Failed to approve comment')
      }
    } catch (err) {
      error.value = err.message || 'Failed to approve comment'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Add comment to top (pin comment)
  async function addCommentToTop(commentId, userId) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.addCommentToTop({
        id: parseInt(commentId),
        userId: parseInt(userId)
      })
      
      if (response.data && response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data?.message || 'Failed to pin comment')
      }
    } catch (err) {
      error.value = err.message || 'Failed to pin comment'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Cancel comment to top (unpin comment)
  async function cancelCommentToTop(commentId, userId) {
    try {
      loading.value = true
      error.value = null
      const response = await commentApi.cancelCommentToTop({
        id: parseInt(commentId),
        userId: parseInt(userId)
      })
      
      if (response.data && response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data?.message || 'Failed to unpin comment')
      }
    } catch (err) {
      error.value = err.message || 'Failed to unpin comment'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  return {
    comments,
    pagination,
    loading,
    error,
    fetchComments,
    addComment,
    deleteComment,
    fetchAllComments,
    approveComment,
    addCommentToTop,
    cancelCommentToTop
  }
})