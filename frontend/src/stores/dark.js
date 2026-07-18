import { ref, watch } from 'vue'

const DARK_KEY = 'shopping_dark'
const isDark = ref(localStorage.getItem(DARK_KEY) === 'true')

watch(isDark, (val) => {
  localStorage.setItem(DARK_KEY, val)
  document.documentElement.classList.toggle('dark', val)
}, { immediate: true })

export function useDarkMode() {
  const toggle = () => { isDark.value = !isDark.value }
  return { isDark, toggle }
}
