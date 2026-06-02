import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Rooms from '../views/Rooms.vue'
import Housekeeping from '../views/Housekeeping.vue'
import Services from '../views/Services.vue'
import Customers from '../views/Customers.vue'
import Bookings from '../views/Bookings.vue'
import Promotions from '../views/Promotions.vue'
import Invoices from '../views/Invoices.vue'
import Staff from '../views/Staff.vue'
import Reports from '../views/Reports.vue'
import Settings from '../views/Settings.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: Dashboard
  },
  {
    path: '/rooms',
    name: 'rooms',
    component: Rooms
  },
  {
    path: '/housekeeping',
    name: 'housekeeping',
    component: Housekeeping
  },
  {
    path: '/services',
    name: 'services',
    component: Services
  },
  {
    path: '/customers',
    name: 'customers',
    component: Customers
  },
  {
    path: '/bookings',
    name: 'bookings',
    component: Bookings
  },
  {
    path: '/promotions',
    name: 'promotions',
    component: Promotions
  },
  {
    path: '/invoices',
    name: 'invoices',
    component: Invoices
  },
  {
    path: '/staff',
    name: 'staff',
    component: Staff
  },
  {
    path: '/reports',
    name: 'reports',
    component: Reports
  },
  {
    path: '/settings',
    name: 'settings',
    component: Settings
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
