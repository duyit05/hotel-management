<script setup>
defineProps({
  toast: {
    type: Object,
    required: true
  }
})
</script>

<template>
  <transition name="fade">
    <div v-if="toast.show" :class="['toast', 'toast-' + toast.type]">
      <span>{{ toast.type === 'success' ? '✓' : '✕' }}</span>
      {{ toast.msg }}
    </div>
  </transition>
</template>
