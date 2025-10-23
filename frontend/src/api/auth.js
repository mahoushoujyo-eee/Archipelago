import axios from 'axios';

const prefix = '/api/account/auth'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const authApi = {
  // Login
  login(credentials) {
    return axios.post(prefix + '/login', credentials, { headers: getHeaders() })
  },
  
  // Register
  register(userData) {
    return axios.post(prefix + '/register', userData, { headers: getHeaders() })
  },
  
  // Request password reset
  sendVerifyCode(email) {
    return axios.get(prefix + `/mail/send/${email}`, { email, headers: getHeaders() })
  },
  
  // Reset password
  resetPassword(data) {
    return axios.post(prefix + '/reset', data, { headers: getHeaders() })
  }
}