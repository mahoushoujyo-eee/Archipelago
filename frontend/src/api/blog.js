import axios from "axios"

const prefix = '/api/blogs'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const blogApi = {
  // Get upload URLs for blog creation
  getUploadUrls(params)
  {
    return axios.post(prefix + '/blog/add', params, { headers: getHeaders() })
  },
  // Get all blogs
  getBlogList(params)
  {
    return axios.post(prefix + '/blog/list', params, { headers: getHeaders() })
  },
  getBlogDetail(params)
  {
    return axios.post(prefix + '/blog/get', params, { headers: getHeaders() })
  },
  getTop3Blogs(params)
  {
    return axios.post(prefix + '/blog/list-top', params, { headers: getHeaders() })
  },
  shiftTopBlog(params)
  {
    return axios.post(prefix + '/blog/shift-top', params, { headers: getHeaders() })
  },
  deleteBlog(params)
  {
    return axios.post(prefix + '/blog/delete', params, { headers: getHeaders() })
  },
  updateBlog(params)
  {
    return axios.post(prefix + '/blog/update', params, { headers: getHeaders() })
  },
  getBlogCount()
  {
    return axios.get(prefix + '/blog/get-count', { headers: getHeaders() })
  },
}