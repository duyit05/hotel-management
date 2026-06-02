<script setup>
defineProps({
  reports: {
    type: Array,
    required: true
  }
})

defineEmits(['show-toast'])
</script>

<template>
  <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px">
    <div 
      v-for="r in reports" 
      :key="r.title" 
      class="card" 
      style="cursor: pointer; transition: border-color .18s"
      @mouseenter="$event.currentTarget.style.borderColor = 'var(--brand-border)'"
      @mouseleave="$event.currentTarget.style.borderColor = 'var(--border)'"
    >
      <div style="font-size: 36px; margin-bottom: 12px">{{ r.icon }}</div>
      <div style="font-weight: 600; color: #fff; margin-bottom: 6px">{{ r.title }}</div>
      <div style="font-size: 12px; color: var(--muted); line-height: 1.6; margin-bottom: 14px">{{ r.desc }}</div>
      <button class="btn btn-sm" style="width: 100%; justify-content: center" @click="$emit('show-toast', 'Đang tạo báo cáo...')">
        Tạo báo cáo
      </button>
    </div>
  </div>
</template>
