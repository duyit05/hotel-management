<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  staff: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchStaff = ref('')
const staffFilter = ref('Tất cả')
const staffPage = ref(1)
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
watch([searchStaff, staffFilter], () => {
  staffPage.value = 1
})

// Filtered & paged staff
const filteredStaff = computed(() => {
  let r = props.staff
  if (searchStaff.value) {
    r = r.filter(x => 
      [x.name, x.dept, x.shift].join(' ').toLowerCase().includes(searchStaff.value.toLowerCase())
    )
  }
  if (staffFilter.value === 'Đang làm') {
    r = r.filter(x => x.active)
  }
  if (staffFilter.value === 'Nghỉ phép') {
    r = r.filter(x => !x.active)
  }
  return r
})

const pagedStaff = computed(() => {
  const s = (staffPage.value - 1) * pageSize
  return filteredStaff.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredStaff.value.length / pageSize))
})

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      name: '',
      dept: 'Lễ tân',
      shift: '',
      since: '',
      salary: '',
      email: '',
      phone: '',
      active: true
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.name || !modal.data.salary) {
    emit('show-toast', 'Vui lòng nhập họ tên và mức lương!', 'error')
    return
  }

  const salaryVal = Number(modal.data.salary)
  const salaryFormatted = '₫' + salaryVal.toLocaleString('vi-VN') + '/tháng'

  if (modal.isEdit) {
    const idx = props.staff.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.staff[idx] = {
        ...modal.data,
        salary: salaryVal,
        salaryFmt: salaryFormatted
      }
      emit('show-toast', 'Đã cập nhật thông tin nhân viên!', 'success')
    }
  } else {
    const newId = 'ST' + String(Date.now()).slice(-4)
    props.staff.unshift({
      id: newId,
      ...modal.data,
      salary: salaryVal,
      salaryFmt: salaryFormatted
    })
    emit('show-toast', 'Đã thêm nhân viên mới thành công!', 'success')
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
  const idx = props.staff.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.staff.splice(idx, 1)
    emit('show-toast', 'Đã xóa nhân viên thành công!', 'success')
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
                <label class="form-label">Họ tên</label>
                <input class="form-control" v-model="modal.data.name" placeholder="Nguyễn Thị Mai" />
              </div>
              <div class="form-group">
                <label class="form-label">Bộ phận</label>
                <select class="form-control" v-model="modal.data.dept">
                  <option>Lễ tân</option>
                  <option>Bếp</option>
                  <option>Buồng phòng</option>
                  <option>Bảo vệ</option>
                  <option>Marketing</option>
                  <option>IT</option>
                  <option>Kế toán</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Ca làm việc</label>
                <input class="form-control" v-model="modal.data.shift" placeholder="Ca sáng 6h-14h" />
              </div>
              <div class="form-group">
                <label class="form-label">Ngày vào làm</label>
                <input class="form-control" v-model="modal.data.since" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Lương (VNĐ/tháng)</label>
                <input class="form-control" v-model="modal.data.salary" type="number" placeholder="12000000" />
              </div>
              <div class="form-group">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.active">
                  <option :value="true">Đang làm</option>
                  <option :value="false">Nghỉ phép</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Email</label>
                <input class="form-control" v-model="modal.data.email" placeholder="nhanvien@luxstay.vn" />
              </div>
              <div class="form-group">
                <label class="form-label">Số điện thoại</label>
                <input class="form-control" v-model="modal.data.phone" placeholder="0912345678" />
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa nhân viên này?</div>
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
          <input class="search-input" v-model="searchStaff" placeholder="Tìm tên, bộ phận, ca làm việc..." />
        </div>
        <div style="display: flex; gap: 6px">
          <button 
            v-for="f in ['Tất cả', 'Đang làm', 'Nghỉ phép']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: staffFilter === f }" 
            @click="staffFilter = f"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Thêm nhân viên</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedStaff.length">
          <thead>
            <tr>
              <th>Nhân viên</th>
              <th>Bộ phận</th>
              <th>Ca làm việc</th>
              <th>Từ ngày</th>
              <th>Email</th>
              <th>SĐT</th>
              <th>Lương/tháng</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in pagedStaff" :key="s.id">
              <td>
                <div style="display: flex; align-items: center; gap: 8px">
                  <div class="avt avt-purple">{{ s.name[0] }}</div>
                  <span style="font-weight: 500; color: var(--text)">{{ s.name }}</span>
                </div>
              </td>
              <td style="color: var(--muted)">{{ s.dept }}</td>
              <td style="color: var(--muted)">{{ s.shift }}</td>
              <td style="color: var(--muted)">{{ s.since }}</td>
              <td style="color: var(--muted)">{{ s.email }}</td>
              <td style="color: var(--muted)">{{ s.phone }}</td>
              <td style="color: #34d399; font-weight: 600">{{ s.salaryFmt }}</td>
              <td>
                <span class="badge" :class="s.active ? 'badge-green' : 'badge-gray'">
                  {{ s.active ? 'Đang làm' : 'Nghỉ phép' }}
                </span>
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
          <div class="empty-icon">👤</div>
          <div class="empty-text">Không tìm thấy nhân viên</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredStaff.length > pageSize">
        <span class="page-info">
          {{ (staffPage - 1) * pageSize + 1 }}–{{ Math.min(staffPage * pageSize, filteredStaff.length) }} / {{ filteredStaff.length }}
        </span>
        <div class="page-btns">
          <button class="page-btn" :disabled="staffPage === 1" @click="staffPage--">‹</button>
          <button 
            v-for="p in totalPages" 
            :key="p" 
            class="page-btn"
            :class="{ active: staffPage === p }" 
            @click="staffPage = p"
          >
            {{ p }}
          </button>
          <button class="page-btn" :disabled="staffPage === totalPages" @click="staffPage++">›</button>
        </div>
      </div>
    </div>
  </div>
</template>
