import axios from 'axios'

const prefix = '/api/account/user'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const userApi = {
  // Get current user
  getCurrentUser() {
    return axios.get(prefix + '/me', { headers: getHeaders() })
  },
  updateUser(params)
  {
    return axios.post(prefix + '/update', params, { headers: getHeaders() })
  },
  updateAvatar(params)
  {
    return axios.post(prefix + '/avatar', params, { headers: getHeaders() })
  },
}