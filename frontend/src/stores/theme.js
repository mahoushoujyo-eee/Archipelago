import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { usePreferredDark } from '@vueuse/core'

export const useThemeStore = defineStore('theme', () => {
  const prefersDark = usePreferredDark()
  const storedTheme = localStorage.getItem('theme')
  
  // Initialize theme based on stored preference or system preference
  const isDark = ref(
    storedTheme ? storedTheme === 'dark' : prefersDark.value
  )
  
  // Toggle theme
  function toggleTheme() {
    isDark.value = !isDark.value
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
    updateVditorTheme()
  }
  
  // Set theme directly
  function setTheme(dark) {
    isDark.value = dark
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
    updateVditorTheme()
  }
  
  // Update Vditor theme
  function updateVditorTheme() {
    // 查找页面中的 Vditor 实例并更新主题
    const vditorElements = document.querySelectorAll('.vditor')
    vditorElements.forEach(element => {
      if (element.vditor) {
        element.vditor.setTheme(isDark.value ? 'dark' : 'classic')
      }
    })
  }
  
  // Watch for system preference changes
  watch(prefersDark, (newValue) => {
    // Only update if user hasn't explicitly set a preference
    if (!storedTheme) {
      isDark.value = newValue
      updateVditorTheme()
    }
  })
  
  // Watch for theme changes to update document class
  watch(isDark, (newValue) => {
    if (newValue) {
      document.documentElement.classList.add('dark-mode')
    } else {
      document.documentElement.classList.remove('dark-mode')
    }
  }, { immediate: true })
  
  return {
    isDark,
    toggleTheme,
    setTheme,
    updateVditorTheme
  }
})