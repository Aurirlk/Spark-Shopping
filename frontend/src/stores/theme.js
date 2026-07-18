import { ref, watch } from 'vue'

const THEME_KEY = 'shopping_theme'
const themes = {
  default: {
    '--primary': '#409eff',
    '--primary-light': '#ecf5ff',
    '--danger': '#f56c6c',
    '--gradient-start': '#1e3c72',
    '--gradient-end': '#2a5298',
    '--banner-bg': 'linear-gradient(135deg, #1e3c72, #2a5298)',
  },
  summer: {
    '--primary': '#07c160',
    '--primary-light': '#e8f8f0',
    '--danger': '#ff6b6b',
    '--gradient-start': '#11998e',
    '--gradient-end': '#38ef7d',
    '--banner-bg': 'linear-gradient(135deg, #11998e, #38ef7d)',
  },
  'double11': {
    '--primary': '#ff4757',
    '--primary-light': '#fff0f0',
    '--danger': '#ff4757',
    '--gradient-start': '#ff4757',
    '--gradient-end': '#ff6b81',
    '--banner-bg': 'linear-gradient(135deg, #ff4757, #ff6b81)',
  },
  spring: {
    '--primary': '#9b59b6',
    '--primary-light': '#f3e8f8',
    '--danger': '#e74c3c',
    '--gradient-start': '#8e44ad',
    '--gradient-end': '#9b59b6',
    '--banner-bg': 'linear-gradient(135deg, #8e44ad, #9b59b6)',
  }
}

const currentTheme = ref(localStorage.getItem(THEME_KEY) || 'default')

watch(currentTheme, (val) => {
  localStorage.setItem(THEME_KEY, val)
  const t = themes[val] || themes.default
  Object.entries(t).forEach(([key, value]) => {
    document.documentElement.style.setProperty(key, value)
  })
}, { immediate: true })

export function useTheme() {
  const setTheme = (name) => { if (themes[name]) currentTheme.value = name }
  const toggleTheme = () => {
    const keys = Object.keys(themes)
    const idx = keys.indexOf(currentTheme.value)
    currentTheme.value = keys[(idx + 1) % keys.length]
  }
  return { currentTheme, setTheme, toggleTheme, themes: Object.keys(themes) }
}
