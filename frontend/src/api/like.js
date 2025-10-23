import axios from "axios"

const prefix = '/api/blogs'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const likeApi = {
  getLikeCount(params)
  {
    return axios.post(prefix + '/like/count', params, { headers: getHeaders() })
  },
  addLike(params)
  {
    return axios.post(prefix + '/like/add', params, { headers: getHeaders() })
  },
  deleteLike(params)
  {
    return axios.post(prefix + '/like/delete', params, { headers: getHeaders() })
  },
  isLiked(params)
  {
    return axios.post(prefix + '/like/is-liked', params, { headers: getHeaders() })
  }
}