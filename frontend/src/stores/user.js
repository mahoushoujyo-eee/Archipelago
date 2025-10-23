import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'
import { userApi } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.authorities.includes('ROLE_ADMIN'))
  
  // Fetch current user information
  async function fetchUser() {
    if (!token.value) console.log('No token found')
    if (!token.value) return
    console.log('Fetching user...')
    
    try {
      loading.value = true
      error.value = null
      const response = await userApi.getCurrentUser()
      user.value = response.data.data
    } catch (err) {
      error.value = err.message || 'Failed to fetch user data'
      // If token is invalid, logout
      if (err.response?.status === 401) {
        logout()
      }
    } finally {
      loading.value = false
    }
  }

  // Login user
  async function login(credentials) {
    try {
      loading.value = true
      error.value = null
      const response = await authApi.login(credentials)
      console.log(response.data.data)
      token.value = response.data.data
      localStorage.setItem('token', token.value)
      await fetchUser()
      return true
    } catch (err) {
      error.value = err.response?.data?.message || 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  // Register user
  async function register(userData) {
    try {
      loading.value = true
      error.value = null
      const response = await authApi.register(userData)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Registration failed'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Request password reset
  async function requestPasswordReset(email) {
    try {
      loading.value = true
      error.value = null
      const response = await authApi.requestPasswordReset(email)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to send reset email'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // Reset password
  async function resetPassword(data) {
    try {
      loading.value = true
      error.value = null
      const response = await authApi.resetPassword(data)
      return response.data
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to reset password'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  // OAuth login
  async function oauthLogin(provider, code) {
    try {
      loading.value = true
      error.value = null
      const response = await authApi.oauthLogin(provider, code)
      token.value = response.data.token
      localStorage.setItem('token', token.value)
      await fetchUser()
      return true
    } catch (err) {
      error.value = err.response?.data?.message || `${provider} login failed`
      return false
    } finally {
      loading.value = false
    }
  }

  // Logout user
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  // Initialize the store
  if (token.value) {
    fetchUser()
  }

  return {
    token,
    user,
    loading,
    error,
    isLoggedIn,
    isAdmin,
    login,
    register,
    requestPasswordReset,
    resetPassword,
    oauthLogin,
    logout,
    fetchUser
  }
})