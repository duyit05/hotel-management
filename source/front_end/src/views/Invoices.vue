<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  invoices: {
    type: Array,
    required: true
  },
  customers: {
    type: Array,
    required: true
  },
  bookings: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchInvoices = ref('')
const invFilter = ref('Tất cả')
const invPage = ref(1)
const pageSize = 8

// Invoice print details state
const printInvoice = ref(null)

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
watch([searchInvoices, invFilter], () => {
  invPage.value = 1
})

// Filtered & paged invoices
const filteredInvoices = computed(() => {
  let r = props.invoices
  if (searchInvoices.value) {
    r = r.filter(x => 
      [x.id, x.customerName, x.bookingId].join(' ').toLowerCase().includes(searchInvoices.value.toLowerCase())
    )
  }
  if (invFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === invFilter.value)
  }
  return r
})

const pagedInvoices = computed(() => {
  const s = (invPage.value - 1) * pageSize
  return filteredInvoices.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredInvoices.value.length / pageSize))
})

// Badge helpers
const invBadge = (s) => {
  const maps = {
    'Đã thanh toán': 'badge-green',
    'Chờ thanh toán': 'badge-amber',
    'Quá hạn': 'badge-red'
  }
  return maps[s] || 'badge-gray'
}

// Format date displays
const formatShowDate = (dStr) => {
  if (!dStr) return ''
  if (dStr.includes('/')) return dStr
  const parts = dStr.split('-')
  if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`
  return dStr
}

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa hóa đơn' : 'Tạo hóa đơn mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      customerId: props.customers[0]?.id || '',
      bookingId: props.bookings[0]?.id || '',
      issued: '',
      due: '',
      amount: '',
      status: 'Chờ thanh toán',
      note: ''
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.customerId || !modal.data.amount) {
    emit('show-toast', 'Vui lòng chọn khách hàng và nhập số tiền!', 'error')
    return
  }

  const cust = props.customers.find(c => c.id === modal.data.customerId)
  const customerName = cust ? cust.name : ''
  const amountVal = Number(modal.data.amount)
  const amountFormatted = '₫' + amountVal.toLocaleString('vi-VN')

  if (modal.isEdit) {
    const idx = props.invoices.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.invoices[idx] = {
        ...modal.data,
        customerName,
        amount: amountVal,
        amountFmt: amountFormatted,
        issued: formatShowDate(modal.data.issued),
        due: formatShowDate(modal.data.due)
      }
      emit('show-toast', 'Đã cập nhật hóa đơn thành công!', 'success')
    }
  } else {
    const newId = 'HD' + String(Date.now()).slice(-6)
    props.invoices.unshift({
      id: newId,
      ...modal.data,
      customerName,
      amount: amountVal,
      amountFmt: amountFormatted,
      issued: formatShowDate(modal.data.issued),
      due: formatShowDate(modal.data.due)
    })
    emit('show-toast', 'Đã tạo hóa đơn mới thành công!', 'success')
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
  const idx = props.invoices.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.invoices.splice(idx, 1)
    emit('show-toast', 'Đã xóa hóa đơn thành công!', 'success')
  }
  closeDelete()
}

// Print Handler
const openPrint = (inv) => {
  printInvoice.value = inv
}

const closePrint = () => {
  printInvoice.value = null
}

const triggerPrint = () => {
  emit('show-toast', 'Đang kết nối máy in để xuất hóa đơn...', 'success')
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
              <div class="form-group">
                <label class="form-label">Khách hàng</label>
                <select class="form-control" v-model="modal.data.customerId">
                  <option v-for="c in customers" :key="c.id" :value="c.id">{{ c.name }}</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Mã đặt phòng</label>
                <select class="form-control" v-model="modal.data.bookingId">
                  <option v-for="b in bookings" :key="b.id" :value="b.id">{{ b.id }} – {{ b.name }}</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Ngày phát hành</label>
                <input class="form-control" v-model="modal.data.issued" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Hạn thanh toán</label>
                <input class="form-control" v-model="modal.data.due" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Số tiền (VNĐ)</label>
                <input class="form-control" v-model="modal.data.amount" type="number" placeholder="2500000" />
              </div>
              <div class="form-group">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Chờ thanh toán</option>
                  <option>Đã thanh toán</option>
                  <option>Quá hạn</option>
                </select>
              </div>
              <div class="form-group form-full">
                <label class="form-label">Ghi chú</label>
                <textarea class="form-control" v-model="modal.data.note" rows="2" placeholder="Ghi chú hóa đơn..."></textarea>
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

    <!-- Detailed Invoice Print Preview Modal -->
    <transition name="fade">
      <div v-if="printInvoice" class="modal-overlay" @click.self="closePrint">
        <div class="modal" style="max-width: 600px;">
          <div class="modal-header">
            <span class="modal-title">Xem trước hóa đơn - {{ printInvoice.id }}</span>
            <button class="btn btn-icon" @click="closePrint" style="border: none; color: var(--muted)">✕</button>
          </div>
          <div class="modal-body" style="background: #fff; color: #1e293b; padding: 30px; font-family: monospace; border-radius: 4px; box-shadow: inset 0 0 10px rgba(0,0,0,0.05)">
            <!-- Receipt Header -->
            <div style="text-align: center; border-bottom: 2px dashed #cbd5e1; padding-bottom: 16px; margin-bottom: 20px">
              <h2 style="font-family: 'Playfair Display', serif; font-size: 22px; color: #0f172a; margin-bottom: 4px">LUXSTAY GRAND HOTEL</h2>
              <div style="font-size: 11px; color: #64748b">128 Đường Lâm Viên, Đà Lạt, Lâm Đồng</div>
              <div style="font-size: 11px; color: #64748b">Hotline: 1900 6868 - Email: info@luxstay.vn</div>
              <h3 style="font-size: 16px; font-weight: 700; color: #0f172a; margin-top: 14px; letter-spacing: 0.05em">HÓA ĐƠN THANH TOÁN</h3>
            </div>
            
            <!-- Receipt Meta -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 11px; margin-bottom: 20px; line-height: 1.5">
              <div>
                <strong>Số hóa đơn:</strong> {{ printInvoice.id }}<br>
                <strong>Khách hàng:</strong> {{ printInvoice.customerName }}<br>
                <strong>Mã đặt phòng:</strong> {{ printInvoice.bookingId }}
              </div>
              <div style="text-align: right">
                <strong>Ngày xuất:</strong> {{ printInvoice.issued }}<br>
                <strong>Hạn thanh toán:</strong> {{ printInvoice.due }}<br>
                <strong>Trạng thái:</strong> <span style="font-weight: bold;" :style="{ color: printInvoice.status === 'Đã thanh toán' ? '#10b981' : '#f59e0b' }">{{ printInvoice.status.toUpperCase() }}</span>
              </div>
            </div>

            <!-- Receipt Items -->
            <table style="width: 100%; border-collapse: collapse; font-size: 11px; margin-bottom: 20px">
              <thead>
                <tr style="border-bottom: 1px solid #94a3b8">
                  <th style="text-align: left; padding: 6px 0; color: #64748b">MÔ TẢ KHOẢN CHI</th>
                  <th style="text-align: right; padding: 6px 0; color: #64748b">SỐ LƯỢNG</th>
                  <th style="text-align: right; padding: 6px 0; color: #64748b">THÀNH TIỀN</th>
                </tr>
              </thead>
              <tbody>
                <tr style="border-bottom: 1px dashed #e2e8f0">
                  <td style="padding: 8px 0">Tiền thuê phòng tiêu chuẩn (Phòng Deluxe)</td>
                  <td style="text-align: right; padding: 8px 0">2 đêm</td>
                  <td style="text-align: right; padding: 8px 0">₫{{ Math.floor(printInvoice.amount * 0.8).toLocaleString('vi-VN') }}</td>
                </tr>
                <tr style="border-bottom: 1px dashed #e2e8f0">
                  <td style="padding: 8px 0">Phụ thu Dịch vụ ăn uống & Tiện ích</td>
                  <td style="text-align: right; padding: 8px 0">1 đợt</td>
                  <td style="text-align: right; padding: 8px 0">₫{{ Math.floor(printInvoice.amount * 0.15).toLocaleString('vi-VN') }}</td>
                </tr>
                <tr style="border-bottom: 1px dashed #e2e8f0">
                  <td style="padding: 8px 0">Thuế giá trị gia tăng VAT (10%)</td>
                  <td style="text-align: right; padding: 8px 0">10%</td>
                  <td style="text-align: right; padding: 8px 0">₫{{ Math.floor(printInvoice.amount * 0.05).toLocaleString('vi-VN') }}</td>
                </tr>
              </tbody>
            </table>

            <!-- Receipt Total -->
            <div style="border-top: 2px dashed #cbd5e1; padding-top: 12px; display: flex; justify-content: space-between; font-size: 14px; font-weight: 700; color: #0f172a; margin-bottom: 24px">
              <span>TỔNG TIỀN THANH TOÁN:</span>
              <span style="font-size: 16px">{{ printInvoice.amountFmt }}</span>
            </div>

            <!-- Receipt Footer / Watermark Seal -->
            <div style="position: relative; text-align: center; font-size: 10px; color: #94a3b8; line-height: 1.6; margin-top: 20px">
              Cảm ơn quý khách đã lựa chọn LuxStay Hotel!<br>
              Hẹn gặp lại quý khách lần sau.
              
              <!-- Seal watermark design -->
              <div 
                style="position: absolute; right: 0; bottom: -5px; border: 3px double; border-radius: 6px; padding: 4px 10px; font-weight: bold; transform: rotate(-10deg); opacity: 0.85; font-size: 11px;"
                :style="{ 
                  borderColor: printInvoice.status === 'Đã thanh toán' ? '#10b981' : '#ef4444',
                  color: printInvoice.status === 'Đã thanh toán' ? '#10b981' : '#ef4444'
                }"
              >
                {{ printInvoice.status.toUpperCase() }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closePrint">Đóng</button>
            <button class="btn btn-primary" @click="triggerPrint">🖨️ In hóa đơn</button>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa hóa đơn này?</div>
            <div style="font-size: 13px; color: var(--muted)">Hành động này không thể hoàn tác.</div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeDelete">Hủy</button>
            <button class="btn btn-danger" @click="confirmDelete">Xóa</button>
          </div>
        </div>
      </div>
    </transition>

    <div class="table-wrap">
      <div class="table-toolbar">
        <div class="search-wrap">
          <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input class="search-input" v-model="searchInvoices" placeholder="Tìm số HĐ, tên khách hàng..." />
        </div>
        <div style="display: flex; gap: 6px">
          <button 
            v-for="f in ['Tất cả', 'Đã thanh toán', 'Chờ thanh toán', 'Quá hạn']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: invFilter === f }" 
            @click="invFilter = f"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Tạo hóa đơn</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedInvoices.length">
          <thead>
            <tr>
              <th>Số HĐ</th>
              <th>Khách hàng</th>
              <th>Mã đặt phòng</th>
              <th>Ngày phát hành</th>
              <th>Hạn TT</th>
              <th>Trạng thái</th>
              <th>Số tiền</th>
              <th>Ghi chú</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="inv in pagedInvoices" :key="inv.id">
              <td style="font-family: monospace; font-size: 12px; color: var(--muted)">{{ inv.id }}</td>
              <td style="font-weight: 500; color: var(--text)">{{ inv.customerName }}</td>
              <td style="font-family: monospace; font-size: 12px; color: #60b4ff">{{ inv.bookingId }}</td>
              <td style="color: var(--muted)">{{ inv.issued }}</td>
              <td style="color: var(--muted)">{{ inv.due }}</td>
              <td><span class="badge" :class="invBadge(inv.status)">{{ inv.status }}</span></td>
              <td style="font-weight: 600; color: var(--text)">{{ inv.amountFmt }}</td>
              <td style="color: var(--subtle); font-size: 12px; max-width: 140px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                {{ inv.note || '—' }}
              </td>
              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', inv)">Sửa</button>
                  <button class="btn btn-sm" @click.stop="openPrint(inv)">In</button>
                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(inv.id)">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-else class="empty">
          <div class="empty-icon">🧾</div>
          <div class="empty-text">Không có hóa đơn nào</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredInvoices.length > pageSize">
        <span class="page-info">
          {{ (invPage - 1) * pageSize + 1 }}–{{ Math.min(invPage * pageSize, filteredInvoices.length) }} / {{ filteredInvoices.length }}
        </span>
        <div class="page-btns">
          <button class="page-btn" :disabled="invPage === 1" @click="invPage--">‹</button>
          <button 
            v-for="p in totalPages" 
            :key="p" 
            class="page-btn"
            :class="{ active: invPage === p }" 
            @click="invPage = p"
          >
            {{ p }}
          </button>
          <button class="page-btn" :disabled="invPage === totalPages" @click="invPage++">›</button>
        </div>
      </div>
    </div>
  </div>
</template>
