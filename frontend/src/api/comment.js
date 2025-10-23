import axios from 'axios'

const prefix = '/api/blogs'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const commentApi = 
{
  addComment(params)
  {
    return axios.post(prefix + '/comment/add', params, { headers: getHeaders() })
  },
  deleteComment(params)
  {
    return axios.post(prefix + '/comment/delete', params, { headers: getHeaders() })
  },
  getComments(params)
  {
    return axios.post(prefix + '/comment/list', params, { headers: getHeaders() })
  },
  getAllComments(params)
  {
    return axios.post(prefix + '/comment/admin/list', params, { headers: getHeaders() })
  },
  approveComment(commentId)
  {
    return axios.post(prefix + '/comment/approve', { id: commentId }, { headers: getHeaders() })
  },
  addCommentToTop(params)
  {
    return axios.post(prefix + '/comment/add-top', params, { headers: getHeaders() })
  },
  cancelCommentToTop(params)
  {
    return axios.post(prefix + '/comment/cancel-top', params, { headers: getHeaders() })
  },
  getCommentCount(params)
  {
    return axios.post(prefix + '/comment/get-count', params, { headers: getHeaders() })
  },

}