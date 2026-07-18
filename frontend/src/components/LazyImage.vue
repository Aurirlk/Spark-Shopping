<template>
  <img v-if="loaded || !lazy" :src="src" v-bind="$attrs" :alt="alt" @error="onError" />
  <div v-else class="lazy-placeholder" :style="{ height, background: '#f0f0f0', borderRadius: '4px' }" ref="placeholder" />
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  src: String,
  alt: { type: String, default: '' },
  lazy: { type: Boolean, default: true },
  height: { type: String, default: '200px' },
  fallback: { type: String, default: '/images/default-product.png' }
})

const loaded = ref(false)
const placeholder = ref(null)

onMounted(() => {
  if (!props.lazy || !placeholder.value) { loaded.value = true; return }
  const observer = new IntersectionObserver(([entry]) => {
    if (entry.isIntersecting) {
      loaded.value = true
      observer.disconnect()
    }
  }, { rootMargin: '100px' })
  observer.observe(placeholder.value)
})

const onError = (e) => {
  if (props.fallback) e.target.src = props.fallback
}
</script>
