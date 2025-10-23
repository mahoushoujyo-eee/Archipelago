/**
 * URL工具函数
 * 确保所有URL都使用HTTPS协议
 */

/**
 * 将HTTP URL转换为HTTPS URL
 * @param {string} url - 原始URL
 * @returns {string} 转换后的HTTPS URL
 */
export function ensureHttps(url) {
  if (!url || typeof url !== 'string') {
    return url
  }
  
  // 如果URL以http://开头，替换为https://
  if (url.startsWith('http://')) {
    return url.replace('http://', 'https://')
  }
  
  // 如果已经是https://或相对路径，直接返回
  return url
}

/**
 * 验证URL是否为HTTPS
 * @param {string} url - 要验证的URL
 * @returns {boolean} 是否为HTTPS URL
 */
export function isHttpsUrl(url) {
  if (!url || typeof url !== 'string') {
    return false
  }
  
  return url.startsWith('https://') || !url.startsWith('http://')
}

/**
 * 批量处理URL数组，确保都是HTTPS
 * @param {string[]} urls - URL数组
 * @returns {string[]} 处理后的HTTPS URL数组
 */
export function ensureHttpsArray(urls) {
  if (!Array.isArray(urls)) {
    return urls
  }
  
  return urls.map(url => ensureHttps(url))
}

/**
 * 处理API响应中的URL，确保使用HTTPS
 * @param {any} data - API响应数据
 * @param {string[]} urlFields - 需要处理的URL字段名数组
 * @returns {any} 处理后的数据
 */
export function processApiUrls(data, urlFields = ['url', 'downloadUrl', 'shareUrl', 'imageUrl']) {
  if (!data || typeof data !== 'object') {
    return data
  }
  
  const processedData = { ...data }
  
  urlFields.forEach(field => {
    if (processedData[field]) {
      processedData[field] = ensureHttps(processedData[field])
    }
  })
  
  return processedData
}