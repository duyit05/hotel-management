<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  bookings: {
    type: Array,
    required: true
  },
  customers: {
    type: Array,
    required: true
  },
  rooms: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchBookings = ref('')
const bookFilter = ref('Tất cả')
const bookPage = ref(1)
const pageSize = 8

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
watch([searchBookings, bookFilter], () => {
  bookPage.value = 1
})

// Filtered & paged bookings
const filteredBookings = computed(() => {
  let r = props.bookings
  if (searchBookings.value) {
    r = r.filter(x => 
      [x.id, x.name, x.roomLabel, x.status].join(' ').toLowerCase().includes(searchBookings.value.toLowerCase())
    )
  }
  if (bookFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === bookFilter.value)
  }
  return r
})

const pagedBookings = computed(() => {
  const s = (bookPage.value - 1) * pageSize
  return filteredBookings.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredBookings.value.length / pageSize))
})

// Badge helpers
const statusBadge = (s) => {
  const maps = {
    'Đã xác nhận': 'badge-blue',
    'Đang ở': 'badge-green',
    'Đã trả phòng': 'badge-gray',
    'Chờ xác nhận': 'badge-amber',
    'Đã hủy': 'badge-red'
  }
  return maps[s] || 'badge-gray'
}

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa đặt phòng' : 'Thêm đặt phòng mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      customerId: props.customers[0]?.id || '',
      roomId: props.rooms[0]?.id || '',
      checkin: '',
      checkout: '',
      guests: 2,
      status: 'Chờ xác nhận',
      note: ''
    }
  }
  modal.open = true;
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.customerId || !modal.data.roomId) {
    emit('show-toast', 'Vui lòng chọn khách hàng và phòng!', 'error')
    return
  }

  const cust = props.customers.find(c => c.id === modal.data.customerId)
  const room = props.rooms.find(r => r.id === modal.data.roomId)
  
  const customerName = cust ? cust.name : ''
  const roomLabelText = room ? 'Phòng ' + room.number : ''
  
  // Format checkin / checkout to standard display if they are written in YYYY-MM-DD
  const formatShowDate = (dStr) => {
    if (!dStr) return ''
    if (dStr.includes('/')) return dStr
    const parts = dStr.split('-')
    if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`
    return dStr
  }

  if (modal.isEdit) {
    const idx = props.bookings.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.bookings[idx] = {
        ...modal.data,
        name: customerName,
        roomLabel: roomLabelText,
        checkin: formatShowDate(modal.data.checkin),
        checkout: formatShowDate(modal.data.checkout)
      }
      emit('show-toast', 'Đã cập nhật đặt phòng thành công!', 'success')
    }
  } else {
    const newId = 'BK' + String(Date.now()).slice(-5)
    props.bookings.unshift({
      id: newId,
      ...modal.data,
      name: customerName,
      roomLabel: roomLabelText,
      checkin: formatShowDate(modal.data.checkin),
      checkout: formatShowDate(modal.data.checkout),
      booked: new Date().toLocaleDateString('vi-VN'),
      totalFmt: '₫' + Math.floor(800 + Math.random() * 4200) + 'k'
    })
    emit('show-toast', 'Đã đặt phòng mới thành công!', 'success')
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
  const idx = props.bookings.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.bookings.splice(idx, 1)
    emit('show-toast', 'Đã xóa đơn đặt phòng thành công!', 'success')
  }
  closeDelete()
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
                <label class="form-label">Phòng</label>
                <select class="form-control" v-model="modal.data.roomId">
                  <!-- Show vacant rooms OR currently selected room of this booking -->
                  <option 
                    v-for="r in rooms.filter(r => r.status === 'Trống' || modal.data.roomId === r.id)" 
                    :key="r.id" 
                    :value="r.id"
                  >
                    Phòng {{ r.number }} – {{ r.type }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Check-in</label>
                <input class="form-control" v-model="modal.data.checkin" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Check-out</label>
                <input class="form-control" v-model="modal.data.checkout" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Số khách</label>
                <input class="form-control" v-model="modal.data.guests" type="number" placeholder="2" />
              </div>
              <div class="form-group">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Chờ xác nhận</option>
                  <option>Đã xác nhận</option>
                  <option>Đang ở</option>
                  <option>Đã trả phòng</option>
                  <option>Đã hủy</option>
                </select>
              </div>
              <div class="form-group form-full">
                <label class="form-label">Yêu cầu đặc biệt</label>
                <textarea class="form-control" v-model="modal.data.note" rows="2" placeholder="Yêu cầu đặc biệt của khách..."></textarea>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa đặt phòng này?</div>
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
          <input class="search-input" v-model="searchBookings" placeholder="Tìm mã đặt, tên khách, phòng..." />
        </div>
        <div style="display: flex; gap: 6px; flex-wrap: wrap">
          <button 
            v-for="f in ['Tất cả', 'Chờ xác nhận', 'Đã xác nhận', 'Đang ở', 'Đã trả phòng', 'Đã hủy']" 
            :key="f"
            class="filter-pill" 
            :class="{ active: bookFilter === f }" 
            @click="bookFilter = f"
            style="font-size: 11px; padding: 4px 10px"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Thêm đặt phòng</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedBookings.length">
          <thead>
            <tr>
              <th>Mã đặt</th>
              <th>Khách hàng</th>
              <th>Phòng</th>
              <th>Check-in</th>
              <th>Check-out</th>
              <th>Số khách</th>
              <th>Ngày đặt</th>
              <th>Trạng thái</th>
              <th>Tổng tiền</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="b in pagedBookings" :key="b.id">
              <td style="font-family: monospace; font-size: 12px; color: #60b4ff">{{ b.id }}</td>
              <td>
                <div style="display: flex; align-items: center; gap: 8px">
                  <div class="avt avt-teal">{{ b.name[0] }}</div>
                  <span style="font-weight: 500; color: var(--text)">{{ b.name }}</span>
                </div>
              </td>
              <td style="color: var(--muted)">{{ b.roomLabel }}</td>
              <td style="color: var(--muted)">{{ b.checkin }}</td>
              <td style="color: var(--muted)">{{ b.checkout }}</td>
              <td style="color: var(--muted)">{{ b.guests }}</td>
              <td style="color: var(--muted)">{{ b.booked }}</td>
              <td><span class="badge" :class="statusBadge(b.status)">{{ b.status }}</span></td>
              <td style="color: #34d399; font-weight: 600">{{ b.totalFmt }}</td>
              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', b)">Sửa</button>
                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(b.id)">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-else class="empty">
          <div class="empty-icon">📅</div>
          <div class="empty-text">Không có đặt phòng nào</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredBookings.length > pageSize">
        <span class="page-info">
          {{ (bookPage - 1) * pageSize + 1 }}–{{ Math.min(bookPage * pageSize, filteredBookings.length) }} / {{ filteredBookings.length }}
        </span>
        <div class="page-btns">
          <button class="page-btn" :disabled="bookPage === 1" @click="bookPage--">‹</button>
          <button 
            v-for="p in totalPages" 
            :key="p" 
            class="page-btn"
            :class="{ active: bookPage === p }" 
            @click="bookPage = p"
          >
            {{ p }}
          </button>
          <button class="page-btn" :disabled="bookPage === totalPages" @click="bookPage++">›</button>
        </div>
      </div>
    </div>
  </div>
</template>
