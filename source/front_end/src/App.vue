<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { useHotelStore } from './stores/hotel'

// Import generic components
import Sidebar from './components/Sidebar.vue'
import Topbar from './components/Topbar.vue'
import Toast from './components/Toast.vue'

// Import view components
import Dashboard from './views/Dashboard.vue'
import Rooms from './views/Rooms.vue'
import Customers from './views/Customers.vue'
import Bookings from './views/Bookings.vue'
import Invoices from './views/Invoices.vue'
import Staff from './views/Staff.vue'
import Reports from './views/Reports.vue'
import Settings from './views/Settings.vue'

// Import new detailed components
import Services from './views/Services.vue'
import Housekeeping from './views/Housekeeping.vue'
import Promotions from './views/Promotions.vue'
import { useCustomerStore } from './stores/customer'

const store = useHotelStore();
const customerStore = useCustomerStore();

// ── Nav Menu Configuration ──
const navItems = [
  { id: 'dashboard', label: 'Dashboard', subtitle: 'Tổng quan hệ thống', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>' },
  { id: 'rooms', label: 'Quản lý phòng', subtitle: 'Danh sách & trạng thái phòng', badge: '48', icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>' },
  { id: 'housekeeping', label: 'Dọn dẹp phòng', subtitle: 'Theo dõi & Vận hành vệ sinh', badge: '5', icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>' },
  { id: 'services', label: 'Dịch vụ & Tiện ích', subtitle: 'Spa, Nhà hàng, Vận tải', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 01-3.46 0"/></svg>' },
  { id: 'customers', label: 'Khách hàng', subtitle: 'Hồ sơ & lịch sử khách', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>' },
  { id: 'bookings', label: 'Đặt phòng', subtitle: 'Quản lý đặt phòng', badge: '12', icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>' },
  { id: 'promotions', label: 'Khuyến mãi & Coupon', subtitle: 'Chiến dịch giảm giá & voucher', badge: '4', icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82zM7 7h.01"/></svg>' },
  { id: 'invoices', label: 'Hóa đơn', subtitle: 'Thanh toán & công nợ', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>' },
  { id: 'staff', label: 'Nhân viên', subtitle: 'Quản lý nhân sự', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>' },
  { id: 'reports', label: 'Báo cáo', subtitle: 'Thống kê & phân tích', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>' },
  { id: 'settings', label: 'Cài đặt', subtitle: 'Cấu hình hệ thống', badge: null, icon: '<svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>' },
]

const currentItem = computed(() => navItems.find(n => n.id === store.currentPage) || navItems[0])

onMounted(async () => {
  store.onResize()
  // Synchronize dynamic theme class
  const html = document.documentElement
  if (store.isDark) {
    html.classList.add('dark')
    html.classList.remove('light')
  } else {
    html.classList.add('light')
    html.classList.remove('dark')
  }
  window.addEventListener('resize', store.onResize)
  
  // Call API fetch endpoints
  store.loading = true
  await Promise.all([
    customerStore.getCustomers(),
    // store.fetchRooms(),
    // store.fetchCustomers(),
    // store.fetchBookings(),
    // store.fetchInvoices(),
    // store.fetchServices(),
    // store.fetchHousekeeping(),
    // store.fetchPromotions(),
    // store.fetchStaff()
  ])
  store.loading = false
})

onUnmounted(() => {
  window.removeEventListener('resize', store.onResize)
})
</script>

<template>
  <div id="app">
    <!-- Mobile overlay -->
    <div 
      v-if="store.sidebarOpen && store.isMobile" 
      @click="store.sidebarOpen = false"
      style="position: fixed; inset: 0; background: rgba(0, 0, 0, .6); z-index: 20; backdrop-filter: blur(2px)"
    ></div>

    <!-- Toast Component -->
    <Toast :toast="store.toast" />

    <!-- Sidebar Component -->
    <Sidebar 
      :sidebar-open="store.sidebarOpen" 
      :is-mobile="store.isMobile" 
      :current-page="store.currentPage" 
      :nav-items="navItems" 
      :is-dark="store.isDark"
      @set-page="store.setPage"
      @toggle-dark="store.toggleDark"
    />

    <!-- Main Content Container -->
    <div class="main">
      <Topbar 
        :sidebar-open="store.sidebarOpen" 
        :current-item="currentItem" 
        @toggle-sidebar="store.toggleSidebar"
      />

      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component 
              :is="Component" 
              :loading="store.loading" 
              :stats="store.stats" 
              :room-status="store.roomStatus" 
              :bookings="store.bookings" 
              :rooms="store.rooms" 
              :housekeeping="store.housekeeping"
              :services="store.services"
              :invoices="store.invoices"
              :promotions="store.promotions"
              :customers="customerStore.customers"
              :staff="store.staff"
              :reports="store.reports"
              :setting-groups="store.settingGroups"
              @set-page="store.setPage"
              @show-toast="store.showToast"
            />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>
