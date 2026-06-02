<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  services: {
    type: Array,
    required: true
  },
  bookings: {
    type: Array,
    required: false,
    default: () => []
  },
  invoices: {
    type: Array,
    required: false,
    default: () => []
  },
  rooms: {
    type: Array,
    required: false,
    default: () => []
  },
  promotions: {
    type: Array,
    required: false,
    default: () => []
  }
})

const emit = defineEmits(['show-toast'])

// Screen switcher
const posMode = ref(false) // Toggle between Service List Admin and POS System

// Search / filter / page state (Admin mode)
const searchServices = ref('')
const typeFilter = ref('Tất cả')
const statusFilter = ref('Tất cả')
const page = ref(1)
const pageSize = 8

// POS system states
const cartItems = ref([])
const selectedBookingId = ref('')
const voucherCode = ref('')
const activeDiscount = ref(0)
const appliedVoucher = ref(null)

// Modal state
const modal = reactive({
  open: false,
  title: '',
  isEdit: false,
  data: {}
})

const deleteModal = reactive({
  open: false,
  id: null
})

// Reset page on search/filter change
watch([searchServices, typeFilter, statusFilter], () => {
  page.value = 1
})

// Filtered & paged services
const filteredServices = computed(() => {
  let r = props.services
  if (searchServices.value) {
    r = r.filter(x => 
      [x.name, x.type, x.desc].join(' ').toLowerCase().includes(searchServices.value.toLowerCase())
    )
  }
  if (typeFilter.value !== 'Tất cả') {
    r = r.filter(x => x.type === typeFilter.value)
  }
  if (statusFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === statusFilter.value)
  }
  return r
})

const pagedServices = computed(() => {
  const s = (page.value - 1) * pageSize
  return filteredServices.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredServices.value.length / pageSize))
})

// Active bookings (staying at hotel right now) to charge bill to
const activeBookings = computed(() => {
  return props.bookings.filter(b => b.status === 'Đang ở' || b.status === 'Đã xác nhận')
})

// Food Menu items for POS (Active Culinary and Spa services)
const posMenuServices = computed(() => {
  return props.services.filter(s => s.status === 'Hoạt động')
})

// Badge helper
const statusBadge = (s) => {
  return s === 'Hoạt động' ? 'badge-green' : 'badge-gray'
}

const typeBadge = (t) => {
  const maps = {
    'Ẩm thực': 'badge-blue',
    'Spa & Massage': 'badge-vip',
    'Giặt là': 'badge-teal',
    'Vận chuyển': 'badge-amber',
    'Tiện ích phòng': 'badge-gold'
  }
  return maps[t] || 'badge-gray'
}

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa dịch vụ' : 'Thêm dịch vụ mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      name: '',
      type: 'Ẩm thực',
      price: '',
      status: 'Hoạt động',
      desc: ''
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.name || !modal.data.price) {
    emit('show-toast', 'Vui lòng nhập tên dịch vụ và mức giá!', 'error')
    return
  }

  const priceVal = Number(modal.data.price)
  const priceFormatted = '₫' + priceVal.toLocaleString('vi-VN')

  if (modal.isEdit) {
    const idx = props.services.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.services[idx] = {
        ...modal.data,
        price: priceVal,
        priceFmt: priceFormatted
      }
      emit('show-toast', 'Đã cập nhật dịch vụ thành công!', 'success')
    }
  } else {
    const newId = 'SV' + String(Date.now()).slice(-5)
    props.services.unshift({
      id: newId,
      ...modal.data,
      price: priceVal,
      priceFmt: priceFormatted
    })
    emit('show-toast', 'Đã thêm dịch vụ mới thành công!', 'success')
  }
  closeModal()
}

// Delete handlers
const openDelete = (id) => {
  deleteModal.id = id
  deleteModal.open = true
}

const closeDelete = () => {
  deleteModal.open = false
}

const confirmDelete = () => {
  const idx = props.services.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.services.splice(idx, 1)
    emit('show-toast', 'Đã xóa dịch vụ thành công!', 'success')
  }
  closeDelete()
}

// POS Operations
const addToCart = (service) => {
  const exist = cartItems.value.find(item => item.serviceId === service.id)
  if (exist) {
    exist.qty++
  } else {
    cartItems.value.push({
      serviceId: service.id,
      name: service.name,
      price: service.price,
      qty: 1,
      type: service.type
    })
  }
  emit('show-toast', `Đã thêm "${service.name}" vào giỏ hàng`, 'success')
}

const updateCartQty = (id, delta) => {
  const item = cartItems.value.find(x => x.serviceId === id)
  if (!item) return
  item.qty += delta
  if (item.qty <= 0) {
    cartItems.value = cartItems.value.filter(x => x.serviceId !== id)
  }
}

const applyPromo = () => {
  if (!voucherCode.value) return
  const promo = props.promotions.find(p => p.code.toUpperCase() === voucherCode.value.toUpperCase() && p.status === 'Đang chạy')
  if (promo) {
    appliedVoucher.value = promo
    if (promo.discountType.includes('Phần trăm')) {
      activeDiscount.value = promo.discountValue // e.g. 20
    } else {
      activeDiscount.value = promo.discountValue // VNĐ
    }
    emit('show-toast', `Đã áp dụng mã giảm giá ${promo.code}!`, 'success')
  } else {
    emit('show-toast', 'Mã giảm giá không hợp lệ hoặc đã hết hạn!', 'error')
    appliedVoucher.value = null
    activeDiscount.value = 0
  }
}

// POS computed calculations
const cartSubtotal = computed(() => {
  return cartItems.value.reduce((acc, item) => acc + (item.price * item.qty), 0)
})

const discountAmount = computed(() => {
  if (!appliedVoucher.value) return 0
  if (appliedVoucher.value.discountType.includes('Phần trăm')) {
    return Math.floor((cartSubtotal.value * activeDiscount.value) / 100)
  } else {
    return activeDiscount.value
  }
})

const cartTax = computed(() => {
  return Math.floor((cartSubtotal.value - discountAmount.value) * 0.1) // 10% VAT
})

const cartTotal = computed(() => {
  return Math.max(0, cartSubtotal.value - discountAmount.value + cartTax.value)
})

const submitPOSOrder = () => {
  if (!selectedBookingId.value) {
    emit('show-toast', 'Vui lòng chọn phòng đang sử dụng dịch vụ!', 'error')
    return
  }
  if (!cartItems.value.length) {
    emit('show-toast', 'Giỏ hàng đang trống!', 'error')
    return
  }

  const booking = props.bookings.find(b => b.id === selectedBookingId.value)
  if (!booking) return

  // Find or create invoice
  let invoice = props.invoices.find(inv => inv.bookingId === booking.id)
  
  const orderNotes = cartItems.value.map(i => `${i.name} (x${i.qty})`).join(', ')

  if (invoice) {
    invoice.amount += cartTotal.value
    invoice.amountFmt = '₫' + invoice.amount.toLocaleString('vi-VN')
    invoice.note = (invoice.note ? invoice.note + '; ' : '') + `POS: ${orderNotes} [Tổng: ₫${cartTotal.value.toLocaleString('vi-VN')}]`
  } else {
    // Generate new invoice
    props.invoices.unshift({
      id: 'HD' + String(Date.now()).slice(-6),
      customerId: booking.customerId,
      customerName: booking.name,
      bookingId: booking.id,
      issued: new Date().toLocaleDateString('vi-VN'),
      due: new Date().toLocaleDateString('vi-VN'),
      status: 'Chờ thanh toán',
      amount: cartTotal.value,
      amountFmt: '₫' + cartTotal.value.toLocaleString('vi-VN'),
      note: `POS: ${orderNotes}`
    })
  }

  // Visual success notifications
  emit('show-toast', `Đã gọi dịch vụ cho Phòng ${booking.roomLabel}. Chi phí đã được tính vào hóa đơn!`, 'success')
  
  // Reset cart
  cartItems.value = []
  selectedBookingId.value = ''
  voucherCode.value = ''
  activeDiscount.value = 0
  appliedVoucher.value = null
}
</script>

<template>
  <div>
    <!-- Modals -->
    <transition name="fade">
      <div v-if="modal.open" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <div class="modal-header">
            <span class="modal-title">{{ modal.title }}</span>
            <button class="btn btn-icon" @click="closeModal" style="border: none; color: var(--muted)">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-grid">
              <div class="form-group form-full">
                <label class="form-label">Tên dịch vụ</label>
                <input class="form-control" v-model="modal.data.name" placeholder="Thử món buffet sáng, Massage thảo dược..." />
              </div>
              <div class="form-group">
                <label class="form-label">Phân loại</label>
                <select class="form-control" v-model="modal.data.type">
                  <option>Ẩm thực</option>
                  <option>Spa & Massage</option>
                  <option>Giặt là</option>
                  <option>Vận chuyển</option>
                  <option>Tiện ích phòng</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Giá dịch vụ (VNĐ)</label>
                <input class="form-control" v-model="modal.data.price" type="number" placeholder="250000" />
              </div>
              <div class="form-group form-full">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Hoạt động</option>
                  <option>Tạm ngưng</option>
                </select>
              </div>
              <div class="form-group form-full">
                <label class="form-label">Mô tả chi tiết</label>
                <textarea class="form-control" v-model="modal.data.desc" rows="2" placeholder="Mô tả dịch vụ..."></textarea>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeModal">Hủy</button>
            <button class="btn btn-primary" @click="saveModal">{{ modal.isEdit ? 'Cập nhật' : 'Thêm mới' }}</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Delete Modal -->
    <transition name="fade">
      <div v-if="deleteModal.open" class="modal-overlay" @click.self="closeDelete">
        <div class="modal">
          <div class="modal-header">
            <span class="modal-title">Xác nhận xóa</span>
            <button class="btn btn-icon" @click="closeDelete" style="border: none; color: var(--muted)">✕</button>
          </div>
          <div class="modal-body" style="text-align: center; padding: 10px 0">
            <div style="font-size: 40px; margin-bottom: 12px">🗑️</div>
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa dịch vụ này?</div>
            <div style="font-size: 13px; color: var(--muted)">Hành động này không thể hoàn tác.</div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeDelete">Hủy</button>
            <button class="btn btn-danger" @click="confirmDelete">Xóa</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Header navigation switcher -->
    <div style="display: flex; gap: 8px; margin-bottom: 16px">
      <button 
        class="filter-pill" 
        :class="{ active: !posMode }" 
        @click="posMode = false; typeFilter = 'Tất cả'"
      >
        🗂️ Quản lý danh mục
      </button>
      <button 
        class="filter-pill" 
        :class="{ active: posMode }" 
        @click="posMode = true; typeFilter = 'Tất cả'"
      >
        🍽️ Room Service POS Gọi món
      </button>
    </div>

    <!-- Mode 1: Service Catalog Admin -->
    <div class="table-wrap" v-if="!posMode">
      <div class="table-toolbar">
        <div class="search-wrap">
          <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input class="search-input" v-model="searchServices" placeholder="Tìm tên dịch vụ, mô tả..." />
        </div>
        <div style="display: flex; gap: 6px; flex-wrap: wrap">
          <button 
            v-for="f in ['Tất cả', 'Ẩm thực', 'Spa & Massage', 'Giặt là', 'Vận chuyển', 'Tiện ích phòng']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: typeFilter === f }" 
            @click="typeFilter = f"
            style="font-size: 11px; padding: 4px 10px"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Thêm dịch vụ</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedServices.length">
          <thead>
            <tr>
              <th>Mã DV</th>
              <th>Tên dịch vụ</th>
              <th>Phân loại</th>
              <th>Giá dịch vụ</th>
              <th>Trạng thái</th>
              <th>Mô tả chi tiết</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in pagedServices" :key="s.id">
              <td style="font-family: monospace; font-size: 12px; color: #60b4ff">{{ s.id }}</td>
              <td style="font-weight: 600">{{ s.name }}</td>
              <td><span class="badge" :class="typeBadge(s.type)">{{ s.type }}</span></td>
              <td style="color: #34d399; font-weight: 600">{{ s.priceFmt }}</td>
              <td><span class="badge" :class="statusBadge(s.status)">{{ s.status }}</span></td>
              <td style="color: var(--muted); font-size: 12px; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                {{ s.desc || '—' }}
              </td>
              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', s)">Sửa</button>
                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(s.id)">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-else class="empty">
          <div class="empty-icon">🛎️</div>
          <div class="empty-text">Không tìm thấy dịch vụ nào</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredServices.length > pageSize">
        <span class="page-info">
          {{ (page - 1) * pageSize + 1 }}–{{ Math.min(page * pageSize, filteredServices.length) }} / {{ filteredServices.length }}
        </span>
        <div class="page-btns">
          <button class="page-btn" :disabled="page === 1" @click="page--">‹</button>
          <button 
            v-for="p in totalPages" 
            :key="p" 
            class="page-btn"
            :class="{ active: page === p }" 
            @click="page = p"
          >
            {{ p }}
          </button>
          <button class="page-btn" :disabled="page === totalPages" @click="page++">›</button>
        </div>
      </div>
    </div>

    <!-- Mode 2: Smart POS Room Service System -->
    <div class="pos-container" v-else>
      <!-- Menu catalog (Left Column) -->
      <div class="pos-menu">
        <div style="display: flex; gap: 6px; margin-bottom: 10px; flex-wrap: wrap">
          <button 
            v-for="cat in ['Tất cả', 'Ẩm thực', 'Spa & Massage', 'Giặt là', 'Vận chuyển']" 
            :key="cat"
            class="filter-pill"
            :class="{ active: typeFilter === cat }"
            @click="typeFilter = cat"
            style="font-size: 11px; padding: 4px 12px"
          >
            {{ cat }}
          </button>
        </div>

        <div class="pos-items-grid">
          <div 
            v-for="s in posMenuServices" 
            :key="s.id" 
            class="pos-item-card"
            @click="addToCart(s)"
            v-show="typeFilter === 'Tất cả' || s.type === typeFilter"
          >
            <div class="pos-item-thumb">
              <span v-if="s.type === 'Ẩm thực'">🍔</span>
              <span v-else-if="s.type === 'Spa & Massage'">💆</span>
              <span v-else-if="s.type === 'Giặt là'">🧺</span>
              <span v-else-if="s.type === 'Vận chuyển'">🚗</span>
              <span v-else>🛎️</span>
            </div>
            <div style="font-weight: 700; font-size: 13px; color: var(--text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
              {{ s.name }}
            </div>
            <div style="font-size: 11px; color: var(--muted); height: 32px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; line-height: 1.3">
              {{ s.desc || 'Mô tả dịch vụ khách sạn chất lượng cao' }}
            </div>
            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: auto; border-top: 1px dashed var(--border); padding-top: 6px">
              <span style="font-size: 12px; font-weight: 700; color: #34d399">{{ s.priceFmt }}</span>
              <span class="badge badge-blue" style="font-size: 9px; padding: 2px 6px">+ Chọn</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Service shopping cart (Right Column) -->
      <div class="pos-cart">
        <div style="font-weight: 700; font-size: 15px; color: var(--text); display: flex; align-items: center; gap: 6px">
          <span>🛒 Giỏ hàng gọi món</span>
          <span class="nav-badge" style="background: var(--brand); color: #fff">{{ cartItems.length }}</span>
        </div>

        <!-- Room Selector for Billing -->
        <div class="form-group" style="margin-top: 4px">
          <label class="form-label" style="font-size: 10px">Chọn phòng đang lưu trú</label>
          <select class="form-control" v-model="selectedBookingId">
            <option value="" disabled selected>-- Chọn phòng đang ở --</option>
            <option v-for="b in activeBookings" :key="b.id" :value="b.id">
              {{ b.roomLabel }} — {{ b.name }}
            </option>
          </select>
        </div>

        <!-- Cart items list -->
        <div class="pos-cart-items">
          <div v-if="!cartItems.length" style="text-align: center; color: var(--muted); padding: 40px 0; font-size: 13px">
            <div style="font-size: 32px; margin-bottom: 8px">📥</div>
            Chưa có món nào được chọn
          </div>
          <div v-else v-for="item in cartItems" :key="item.serviceId" class="pos-cart-item">
            <div style="flex: 1; min-width: 0; padding-right: 8px">
              <div style="font-size: 12px; font-weight: 600; color: var(--text); overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                {{ item.name }}
              </div>
              <div style="font-size: 11px; color: var(--muted)">
                ₫{{ item.price.toLocaleString('vi-VN') }}
              </div>
            </div>
            <div class="pos-cart-qty">
              <button class="pos-cart-btn" @click="updateCartQty(item.serviceId, -1)">-</button>
              <span style="font-size: 13px; font-weight: 600; width: 16px; text-align: center">{{ item.qty }}</span>
              <button class="pos-cart-btn" @click="updateCartQty(item.serviceId, 1)">+</button>
            </div>
          </div>
        </div>

        <!-- Voucher Code Input -->
        <div class="form-group">
          <label class="form-label" style="font-size: 10px">Mã giảm giá (Coupon)</label>
          <div style="display: flex; gap: 4px">
            <input class="form-control" v-model="voucherCode" placeholder="Mã e.g. SUMMER20" style="padding: 6px 8px" />
            <button class="btn btn-sm" @click="applyPromo" style="padding: 0 10px">Áp dụng</button>
          </div>
          <div v-if="appliedVoucher" style="font-size: 11px; color: #34d399; margin-top: 3px">
            ✓ Đã giảm: {{ appliedVoucher.discountValueFmt }}
          </div>
        </div>

        <!-- Totals breakdown -->
        <div style="display: flex; flex-direction: column; gap: 6px; font-size: 12px; border-top: 1px dashed var(--border); padding-top: 10px; color: var(--muted)">
          <div style="display: flex; justify-content: space-between">
            <span>Tạm tính:</span>
            <span style="color: var(--text)">₫{{ cartSubtotal.toLocaleString('vi-VN') }}</span>
          </div>
          <div style="display: flex; justify-content: space-between" v-if="discountAmount > 0">
            <span>Giảm giá:</span>
            <span style="color: #f87171">- ₫{{ discountAmount.toLocaleString('vi-VN') }}</span>
          </div>
          <div style="display: flex; justify-content: space-between">
            <span>Thuế VAT (10%):</span>
            <span style="color: var(--text)">₫{{ cartTax.toLocaleString('vi-VN') }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 14px; font-weight: 700; color: var(--text); border-top: 1px solid var(--border); padding-top: 6px; margin-top: 4px">
            <span>TỔNG CỘNG:</span>
            <span style="color: #34d399">₫{{ cartTotal.toLocaleString('vi-VN') }}</span>
          </div>
        </div>

        <!-- Charge Room Action Button -->
        <button 
          class="btn btn-primary" 
          style="width: 100%; margin-top: auto; padding: 10px; font-size: 13px"
          @click="submitPOSOrder"
          :disabled="!cartItems.length"
        >
          ⚡ Gửi Hóa đơn vào phòng (Charge)
        </button>
      </div>
    </div>
  </div>
</template>

