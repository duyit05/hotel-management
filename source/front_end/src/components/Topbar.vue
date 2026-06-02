<script setup>
defineProps({
  sidebarOpen: Boolean,
  currentItem: Object
})

defineEmits(['toggle-sidebar'])
</script>

<template>
  <div class="topbar">
    <button class="btn btn-icon" @click="$emit('toggle-sidebar')" style="border: none; color: var(--muted)">
      <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
        <path d="M4 6h16M4 12h16M4 18h16" />
      </svg>
    </button>
    <div>
      <div class="topbar-title">{{ currentItem.label }}</div>
      <div class="topbar-sub">{{ currentItem.subtitle }}</div>
    </div>
    <div class="topbar-actions">
      <button class="btn btn-icon" style="border: none; color: var(--muted); position: relative">
        <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <span style="position: absolute; top: 6px; right: 6px; width: 7px; height: 7px; background: var(--brand); border-radius: 50%"></span>
      </button>
      <div class="avatar">AD</div>
    </div>
  </div>
</template>
