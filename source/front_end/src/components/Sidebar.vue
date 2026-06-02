<script setup>
defineProps({
  sidebarOpen: Boolean,
  isMobile: Boolean,
  currentPage: String,
  navItems: Array,
  isDark: Boolean
})

const emit = defineEmits(['set-page', 'toggle-dark'])

const setPage = (id) => {
  emit('set-page', id)
}

const toggleDark = () => {
  emit('toggle-dark')
}
</script>

<template>
  <div class="sidebar" :class="sidebarOpen ? (isMobile ? 'open' : 'open') : (isMobile ? 'closed' : 'rail')">
    <div class="sidebar-logo">
      <div class="logo-icon">
        <svg width="18" height="18" fill="white" viewBox="0 0 24 24">
          <path d="M19 3H5a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2V5a2 2 0 00-2-2zm-7 3a4 4 0 110 8 4 4 0 010-8zm0 14c-2.67 0-8 1.34-8 4v1h16v-1c0-2.66-5.33-4-8-4z" />
        </svg>
      </div>
      <span class="logo-text" v-show="sidebarOpen">LuxStay</span>
    </div>
    <nav class="sidebar-nav">
      <div 
        v-for="item in navItems" 
        :key="item.id" 
        class="nav-item" 
        :class="{ active: currentPage === item.id }"
        @click="setPage(item.id)"
      >
        <span class="nav-icon" v-html="item.icon"></span>
        <span class="nav-label" v-show="sidebarOpen">{{ item.label }}</span>
        <span v-if="item.badge && sidebarOpen" class="nav-badge">{{ item.badge }}</span>
      </div>
    </nav>
    <div class="sidebar-footer">
      <div class="nav-item" @click="toggleDark" style="margin: 0">
        <span class="nav-icon" style="font-size: 16px">{{ isDark ? '☀️' : '🌙' }}</span>
        <span class="nav-label" v-show="sidebarOpen">{{ isDark ? 'Light mode' : 'Dark mode' }}</span>
      </div>
    </div>
  </div>
</template>
