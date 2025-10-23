// 全局头像配置
const AVATAR_BASE_URL = 'https://avatar-blog-5.oss-cn-beijing.aliyuncs.com'
const DEFAULT_AVATAR = 'https://avatar-blog-5.oss-cn-beijing.aliyuncs.com/default.jpg'

/**
 * 获取用户头像URL
 * @param {string|number} userId - 用户ID
 * @param {string} fallbackAvatar - 备用头像URL
 * @returns {Promise<string>} 头像URL
 */
export async function getUserAvatarUrl(userId, fallbackAvatar = null) {
  if (!userId) {
    return fallbackAvatar || DEFAULT_AVATAR
  }
  
  const avatarUrl = `${AVATAR_BASE_URL}/${userId}`
  
  try {
    // 检查头像是否存在
    const response = await fetch(avatarUrl, { method: 'HEAD' })
    if (response.ok) {
      return avatarUrl
    }
  } catch (error) {
    console.warn('头像URL验证失败:', avatarUrl, error)
  }
  
  // 如果头像不存在，返回备用头像或默认头像
  return fallbackAvatar || DEFAULT_AVATAR
}

/**
 * 验证头像URL是否有效
 * @param {string} avatarUrl - 头像URL
 * @returns {Promise<string|null>} 有效的头像URL或null
 */
export async function validateAvatarUrl(avatarUrl) {
  if (!avatarUrl) return null
  
  try {
    const response = await fetch(avatarUrl, { method: 'HEAD' })
    return response.ok ? avatarUrl : null
  } catch (error) {
    console.warn('头像URL验证失败:', avatarUrl, error)
    return null
  }
}

/**
 * 获取头像配置
 */
export const avatarConfig = {
  baseUrl: AVATAR_BASE_URL,
  defaultAvatar: DEFAULT_AVATAR
}