<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  housekeeping: {
    type: Array,
    required: true
  },
  staff: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchRoom = ref('')
const statusFilter = ref('Tất cả')
const page = ref(1)
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
watch([searchRoom, statusFilter], () => {
  page.value = 1
})

// Filtered & paged logs
const filteredLogs = computed(() => {
  let r = props.housekeeping
  if (searchRoom.value) {
    r = r.filter(x => 
      [x.roomNumber, x.staffName, x.note].join(' ').toLowerCase().includes(searchRoom.value.toLowerCase())
    )
  }
  if (statusFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === statusFilter.value)
  }
  return r
})

const pagedLogs = computed(() => {
  const s = (page.value - 1) * pageSize
  return filteredLogs.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredLogs.value.length / pageSize))
})

// Badge helper
const statusBadge = (s) => {
  const maps = {
    'Sạch sẽ': 'badge-green',
    'Chờ dọn': 'badge-red',
    'Đang dọn': 'badge-blue',
    'Đang kiểm tra': 'badge-amber'
  }
  return maps[s] || 'badge-gray'
}

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa phân công & trạng thái dọn dẹp' : 'Phân công dọn dẹp phòng mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      roomNumber: '',
      staffName: props.staff.filter(s => s.dept === 'Buồng phòng')[0]?.name || props.staff[0]?.name || '',
      status: 'Chờ dọn',
      note: ''
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.roomNumber || !modal.data.staffName) {
    emit('show-toast', 'Vui lòng nhập số phòng và nhân viên phụ trách!', 'error')
    return
  }

  const cleanTime = new Date().toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' })

  if (modal.isEdit) {
    const idx = props.housekeeping.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.housekeeping[idx] = {
        ...modal.data,
        lastCleaned: cleanTime
      }
      emit('show-toast', 'Cập nhật tình trạng buồng phòng thành công!', 'success')
    }
  } else {
    const newId = 'HK' + String(Date.now()).slice(-5)
    props.housekeeping.unshift({
      id: newId,
      ...modal.data,
      lastCleaned: cleanTime
    })
    emit('show-toast', 'Thêm mới nhật ký dọn phòng thành công!', 'success')
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
  const idx = props.housekeeping.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.housekeeping.splice(idx, 1)
    emit('show-toast', 'Đã xóa nhật ký dọn dẹp!', 'success')
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
                <label class="form-label">Số phòng</label>
                <input class="form-control" v-model="modal.data.roomNumber" placeholder="101" />
              </div>
              <div class="form-group">
                <label class="form-label">Nhân viên dọn dẹp</label>
                <select class="form-control" v-model="modal.data.staffName">
                  <!-- Prefer Staff from Housekeeping department -->
                  <option v-for="s in staff" :key="s.id" :value="s.name">{{ s.name }} ({{ s.dept }})</option>
                </select>
              </div>
              <div class="form-group form-full">
                <label class="form-label">Trạng thái buồng phòng</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Sạch sẽ</option>
                  <option>Chờ dọn</option>
                  <option>Đang dọn</option>
                  <option>Đang kiểm tra</option>
                </select>
              </div>
              <div class="form-group form-full">
                <label class="form-label">Ghi chú vệ sinh / vật tư thiếu</label>
                <textarea class="form-control" v-model="modal.data.note" rows="2" placeholder="Ghi chú (Ví dụ: Thiếu khăn tắm, Khách yêu cầu dọn trước 10h...)"></textarea>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeModal">Hủy</button>
            <button class="btn btn-primary" @click="saveModal">{{ modal.isEdit ? 'Cập nhật' : 'Phân công' }}</button>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa nhật ký buồng phòng này?</div>
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
          <input class="search-input" v-model="searchRoom" placeholder="Tìm số phòng, nhân viên..." />
        </div>
        <div style="display: flex; gap: 6px; flex-wrap: wrap">
          <button 
            v-for="f in ['Tất cả', 'Sạch sẽ', 'Chờ dọn', 'Đang dọn', 'Đang kiểm tra']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: statusFilter === f }" 
            @click="statusFilter = f"
            style="font-size: 11px; padding: 4px 10px"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Phân công dọn dẹp</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedLogs.length">
          <thead>
            <tr>
              <th>Mã NK</th>
              <th>Số phòng</th>
              <th>Nhân viên phụ trách</th>
              <th>Trạng thái vệ sinh</th>
              <th>Thời gian cập nhật</th>
              <th>Ghi chú vận hành</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="log in pagedLogs" :key="log.id">
              <td style="font-family: monospace; font-size: 12px; color: #60b4ff">{{ log.id }}</td>
              <td style="font-weight: 600">Phòng {{ log.roomNumber }}</td>
              <td>
                <div style="display: flex; align-items: center; gap: 8px">
                  <div class="avt avt-purple">{{ log.staffName[0] }}</div>
                  <span>{{ log.staffName }}</span>
                </div>
              </td>
              <td><span class="badge" :class="statusBadge(log.status)">{{ log.status }}</span></td>
              <td style="color: var(--muted); font-size: 12px">{{ log.lastCleaned }}</td>
              <td style="color: var(--muted); font-size: 12px; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                {{ log.note || '—' }}
              </td>
              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', log)">Cập nhật</button>
                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(log.id)">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-else class="empty">
          <div class="empty-icon">🧹</div>
          <div class="empty-text">Không có lịch dọn dẹp nào được tìm thấy</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredLogs.length > pageSize">
        <span class="page-info">
          {{ (page - 1) * pageSize + 1 }}–{{ Math.min(page * pageSize, filteredLogs.length) }} / {{ filteredLogs.length }}
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
  </div>
</template>
