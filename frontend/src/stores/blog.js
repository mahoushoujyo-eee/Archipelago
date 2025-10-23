import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { blogApi } from '../api/blog'
import { getUserAvatarUrl } from '../utils/avatar'
import { ensureHttps } from '../utils/url'

export const useBlogStore = defineStore('blog', () => {
  const blogs = ref([])
  const currentBlog = ref(null)
  const pagination = ref({
    page: 1,
    limit: 12,
    total: 0
  })
  const loading = ref(false)
  const error = ref(null)
  const tags = ref([])

  // Get all blogs with pagination
  async function fetchBlogs(params = {}) 
  {
    try
     {
      loading.value = true
      error.value = null
      
      // 首先获取博客总数
      const countResponse = await blogApi.getBlogCount()
      let totalCount = 0
      if (countResponse.data && countResponse.data.code === 200) {
        totalCount = countResponse.data.data || 0
      }
      
      const response = await blogApi.getBlogList({
        pageIndex: params.pageIndex || params.page || pagination.value.page || 1,
        ...params
      })
      
      // 处理响应数据结构
      if (response.data && response.data.code === 200) {
        const blogData = response.data.data || []
        
        console.log("blogData:", blogData)

        // 处理博客数据，解析tags字段并转换为组件期望的格式
        const processedBlogs = await Promise.all(blogData.map(async blog => ({
          ...blog,
          id: blog.id,
          title: blog.title,
          cover: blog.imgUrl ? ensureHttps(blog.imgUrl) : '/default-cover.jpg',
          excerpt: blog.excerpt || '暂无摘要...',
          tags: JSON.parse(blog.tags),
          author: {
            id: blog.userId,
            name: blog.nickname || '匿名用户',
            avatar: await getUserAvatarUrl(blog.userId, blog.authorAvatar)
          },
          publishedAt: blog.creationTime,
          createdAt: blog.creationTime,
          isTop: blog.type === 1, // 添加置顶状态字段
          likes: blog.likes || 0, // 添加点赞数字段
          comments: blog.comments || 0, // 添加评论数字段
          // 保留原始数据
        })))
        
        // 按置顶状态排序：置顶文章在前，然后按创建时间倒序
        blogs.value = processedBlogs.sort((a, b) => {
          if (a.isTop && !b.isTop) return -1
          if (!a.isTop && b.isTop) return 1
          return new Date(b.creationTime) - new Date(a.creationTime)
        })

        console.log(blogs.value)
        
        // 更新分页信息，使用计数接口获取的总数
         pagination.value = {
           page: params.pageIndex || params.page || pagination.value.page || 1,
           limit: params.limit || pagination.value.limit || 12,
           total: totalCount // 使用计数接口获取的总数
         }
      } else {
        blogs.value = []
        throw new Error(response.data?.message || 'Failed to fetch blogs')
      }
    } catch (err) {
      error.value = err.message || 'Failed to fetch blogs'
      blogs.value = []
    } finally {
      loading.value = false
    }
  }

  // Get a single blog by ID
  async function fetchBlogById(id) {
    try {
      loading.value = true
      
      // 确保ID是数字类型
      const blogId = parseInt(id)
      console.log('Fetching blog with ID:', blogId)
      
      // 调用 getBlogDetail API 获取博客详情
      const response = await blogApi.getBlogDetail({ id: blogId })
      console.log('API response:', response)
      
      if (response.data && response.data.code === 200) {
        const blogData = response.data.data
        console.log('Blog data:', blogData)
        
        // 解析标签
        let parsedTags = []
        try {
          parsedTags = JSON.parse(blogData.tags || '[]')
        } catch (e) {
          console.log('Failed to parse tags:', e)
          parsedTags = []
        }
        
        // 拉取 markdown 文件内容
        const markdownResponse = await fetch(ensureHttps(blogData.contentUrl))
        const markdownContent = await markdownResponse.text()
        
        // 构建完整的博客对象
        currentBlog.value = {
          id: blogData.id,
          title: blogData.title,
          content: markdownContent, // 保存原始markdown内容，不在这里渲染
          cover: blogData.imgUrl ? ensureHttps(blogData.imgUrl) : '/default-cover.jpg',
          tags: parsedTags,
          author: {
            id: blogData.userId,
            name: blogData.nickname || '匿名用户',
            avatar: await getUserAvatarUrl(blogData.userId, blogData.authorAvatar)
          },
          publishedAt: blogData.creationTime || new Date().toISOString(),
          createdAt: blogData.creationTime || new Date().toISOString(),
          isTop: blogData.type === 1, // 添加置顶状态字段
          likes: blogData.likes || 0, // 使用后端返回的点赞数
          comments: blogData.comments || 0, // 使用后端返回的评论数
          liked: false // 默认未点赞，后续可通过API查询用户是否已点赞
        }
        
        console.log('Current blog set:', currentBlog.value)
        return currentBlog.value
      } else {
        console.log('API response error (ignored):', response.data)
        // 设置默认博客内容而不抛出错误
        currentBlog.value = {
          id: blogId,
          title: '博客标题',
          content: '<p>内容加载中...</p>',
          cover: '/default-cover.jpg',
          tags: [],
          author: {
            id: null,
            name: '匿名用户',
            avatar: 'https://img-bsy.txrpic.com/Element/00/88/63/12/549f4792_E886312_4b2c4691XZ.png?imageMogr2/quality/90/thumbnail/320x%3E'
          },
          publishedAt: new Date().toISOString(),
          createdAt: new Date().toISOString(),
          likes: 0,
          liked: false
        }
        return currentBlog.value
      }
    } catch (err) {
      console.log('Error in fetchBlogById (ignored):', err)
      // 设置默认博客内容而不抛出错误
      currentBlog.value = {
        id: parseInt(id),
        title: '博客标题',
        content: '<p>内容加载中...</p>',
        cover: '/default-cover.jpg',
        tags: [],
        author: {
          id: null,
          name: '匿名用户',
          avatar: 'https://img-bsy.txrpic.com/Element/00/88/63/12/549f4792_E886312_4b2c4691XZ.png?imageMogr2/quality/90/thumbnail/320x%3E'
        },
        publishedAt: new Date().toISOString(),
        createdAt: new Date().toISOString(),
        likes: 0,
        liked: false
      }
      return currentBlog.value
    } finally {
      loading.value = false
    }
  }

  // Create a new blog
  async function createBlog(blogData) {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.createBlog(blogData)
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to create blog'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Update an existing blog
  async function updateBlog(id, blogData) {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.updateBlog(id, blogData)
      
      // Update the blog in the list if it exists
      const index = blogs.value.findIndex(blog => blog.id === id)
      if (index !== -1) {
        blogs.value[index] = response.data
      }
      
      // Update current blog if it's the one being edited
      if (currentBlog.value && currentBlog.value.id === id) {
        currentBlog.value = response.data
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to update blog'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Delete a blog
  async function deleteBlog(id) {
    try {
      loading.value = true
      error.value = null
      await blogApi.deleteBlog(id)
      
      // Remove from list
      blogs.value = blogs.value.filter(blog => blog.id !== id)
      
      // Clear current blog if it's the one being deleted
      if (currentBlog.value && currentBlog.value.id === id) {
        currentBlog.value = null
      }
      
      return true
    } catch (err) {
      error.value = err.message || 'Failed to delete blog'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Like a blog
  async function likeBlog(blogId, userId) {
    try {
      loading.value = true
      error.value = null
      
      // 导入点赞API
      const { likeApi } = await import('../api/like')
      
      // 调用点赞API
      const response = await likeApi.addLike({ id: blogId, userId })
      
      if (response.data && response.data.code === 200) {
        // 更新博客列表中的点赞数
        const index = blogs.value.findIndex(blog => blog.id === blogId)
        if (index !== -1) {
          blogs.value[index].likes = (blogs.value[index].likes || 0) + 1
          blogs.value[index].liked = true
        }
        
        // 更新当前博客的点赞数
        if (currentBlog.value && currentBlog.value.id === blogId) {
          currentBlog.value.likes = (currentBlog.value.likes || 0) + 1
          currentBlog.value.liked = true
        }
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to like blog'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Unlike a blog
  async function unlikeBlog(blogId, userId) {
    try {
      loading.value = true
      error.value = null
      
      // 导入点赞API
      const { likeApi } = await import('../api/like')
      
      // 调用取消点赞API
      const response = await likeApi.deleteLike({ id: blogId, userId })
      
      if (response.data && response.data.code === 200) {
        // 更新博客列表中的点赞数
        const index = blogs.value.findIndex(blog => blog.id === blogId)
        if (index !== -1) {
          blogs.value[index].likes = Math.max((blogs.value[index].likes || 0) - 1, 0)
          blogs.value[index].liked = false
        }
        
        // 更新当前博客的点赞数
        if (currentBlog.value && currentBlog.value.id === blogId) {
          currentBlog.value.likes = Math.max((currentBlog.value.likes || 0) - 1, 0)
          currentBlog.value.liked = false
        }
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to unlike blog'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Check if user liked a blog
  async function checkLikeStatus(blogId, userId) {
    try {
      // 导入点赞API
      const { likeApi } = await import('../api/like')
      
      // 调用检查点赞状态API
      const response = await likeApi.isLiked({ id: blogId, userId })
      
      if (response.data && response.data.code === 200) {
        const isLiked = response.data.data
        
        // 更新博客列表中的点赞状态
        const index = blogs.value.findIndex(blog => blog.id === blogId)
        if (index !== -1) {
          blogs.value[index].liked = isLiked
        }
        
        // 更新当前博客的点赞状态
        if (currentBlog.value && currentBlog.value.id === blogId) {
          currentBlog.value.liked = isLiked
        }
        
        return isLiked
      }
      
      return false
    } catch (err) {
      console.error('Failed to check like status:', err)
      return false
    }
  }

  // Get like count for a blog
  async function getLikeCount(blogId) {
    try {
      // 导入点赞API
      const { likeApi } = await import('../api/like')
      
      // 调用获取点赞数API
      const response = await likeApi.getLikeCount({ id: blogId })
      
      if (response.data && response.data.code === 200) {
        const likeCount = response.data.data
        
        // 更新博客列表中的点赞数
        const index = blogs.value.findIndex(blog => blog.id === blogId)
        if (index !== -1) {
          blogs.value[index].likes = likeCount
        }
        
        // 更新当前博客的点赞数
        if (currentBlog.value && currentBlog.value.id === blogId) {
          currentBlog.value.likes = likeCount
        }
        
        return likeCount
      }
      
      return 0
    } catch (err) {
      console.error('Failed to get like count:', err)
      return 0
    }
  }


  // Get featured blogs
  async function fetchFeaturedBlogs() {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.getFeaturedBlogs()
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to fetch featured blogs'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Fetch tags from existing blogs
  async function fetchTags() {
    try {
      // 如果还没有博客数据，先获取博客列表
      if (blogs.value.length === 0) {
        await fetchBlogs()
      }
      
      // 从所有博客中提取唯一标签
      const allTags = new Set()
      blogs.value.forEach(blog => {
        if (blog.tags && Array.isArray(blog.tags)) {
          blog.tags.forEach(tag => allTags.add(tag))
        }
      })
      
      tags.value = Array.from(allTags)
      return tags.value
    } catch (err) {
      console.error('Failed to fetch tags:', err)
      return []
    }
  }

  // Toggle blog top status
  async function toggleTopBlog(blogId, isTop) {
    try {
      loading.value = true
      error.value = null
      const response = await blogApi.shiftTopBlog({ 
        id: blogId, 
        type: isTop ? 1 : 0 
      })
      
      // 更新本地博客列表中的置顶状态
      const index = blogs.value.findIndex(blog => blog.id === blogId)
      if (index !== -1) {
        blogs.value[index].isTop = isTop
        blogs.value[index].type = isTop ? 1 : 0
      }
      
      // 更新当前博客的置顶状态
      if (currentBlog.value && currentBlog.value.id === blogId) {
        currentBlog.value.isTop = isTop
        currentBlog.value.type = isTop ? 1 : 0
      }
      
      return response.data
    } catch (err) {
      error.value = err.message || 'Failed to toggle blog top status'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  return {
    blogs,
    currentBlog,
    pagination,
    loading,
    error,
    tags,
    fetchBlogs,
    fetchBlogById,
    createBlog,
    updateBlog,
    deleteBlog,
    likeBlog,
    unlikeBlog,
    checkLikeStatus,
    getLikeCount,
    fetchTags,
    fetchFeaturedBlogs,
    toggleTopBlog
  }
})