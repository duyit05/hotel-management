<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  promotions: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchCode = ref('')
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
watch([searchCode, statusFilter], () => {
  page.value = 1
})

// Filtered & paged promotions
const filteredPromos = computed(() => {
  let r = props.promotions
  if (searchCode.value) {
    r = r.filter(x => 
      [x.code, x.discountType, x.status].join(' ').toLowerCase().includes(searchCode.value.toLowerCase())
    )
  }
  if (statusFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === statusFilter.value)
  }
  return r
})

const pagedPromos = computed(() => {
  const s = (page.value - 1) * pageSize
  return filteredPromos.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredPromos.value.length / pageSize))
})

// Badge helper
const statusBadge = (s) => {
  return s === 'Đang chạy' ? 'badge-green' : 'badge-gray'
}

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
  modal.title = mode === 'edit' ? 'Chỉnh sửa chương trình khuyến mãi' : 'Tạo chương trình khuyến mãi mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      code: '',
      discountType: 'Phần trăm (%)',
      discountValue: '',
      startDate: '',
      endDate: '',
      minStay: 1,
      status: 'Đang chạy'
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.code || !modal.data.discountValue || !modal.data.startDate || !modal.data.endDate) {
    emit('show-toast', 'Vui lòng điền đầy đủ các thông tin bắt buộc!', 'error')
    return
  }

  const val = Number(modal.data.discountValue)
  const isPercent = modal.data.discountType === 'Phần trăm (%)'
  const valFormatted = isPercent ? val + '%' : '₫' + val.toLocaleString('vi-VN')

  if (modal.isEdit) {
    const idx = props.promotions.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.promotions[idx] = {
        ...modal.data,
        discountValue: val,
        discountValueFmt: valFormatted,
        startDate: formatShowDate(modal.data.startDate),
        endDate: formatShowDate(modal.data.endDate)
      }
      emit('show-toast', 'Đã cập nhật chương trình khuyến mãi!', 'success')
    }
  } else {
    const newId = 'PR' + String(Date.now()).slice(-5)
    props.promotions.unshift({
      id: newId,
      ...modal.data,
      discountValue: val,
      discountValueFmt: valFormatted,
      startDate: formatShowDate(modal.data.startDate),
      endDate: formatShowDate(modal.data.endDate)
    })
    emit('show-toast', 'Đã thêm chương trình khuyến mãi mới thành công!', 'success')
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
  const idx = props.promotions.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.promotions.splice(idx, 1)
    emit('show-toast', 'Đã xóa mã khuyến mãi thành công!', 'success')
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
                <label class="form-label">Mã khuyến mãi (Voucher Code)</label>
                <input class="form-control" v-model="modal.data.code" placeholder="LUXSTAY20" style="text-transform: uppercase" />
              </div>
              <div class="form-group">
                <label class="form-label">Loại giảm giá</label>
                <select class="form-control" v-model="modal.data.discountType">
                  <option>Phần trăm (%)</option>
                  <option>Số tiền cố định (VNĐ)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Giá trị giảm</label>
                <input class="form-control" v-model="modal.data.discountValue" type="number" placeholder="20 hoặc 500000" />
              </div>
              <div class="form-group">
                <label class="form-label">Số ngày thuê tối thiểu</label>
                <input class="form-control" v-model="modal.data.minStay" type="number" placeholder="1" />
              </div>
              <div class="form-group">
                <label class="form-label">Ngày bắt đầu</label>
                <input class="form-control" v-model="modal.data.startDate" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Ngày kết thúc</label>
                <input class="form-control" v-model="modal.data.endDate" type="date" />
              </div>
              <div class="form-group form-full">
                <label class="form-label">Trạng thái ưu đãi</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Đang chạy</option>
                  <option>Hết hạn</option>
                </select>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeModal">Hủy</button>
            <button class="btn btn-primary" @click="saveModal">{{ modal.isEdit ? 'Cập nhật' : 'Tạo mới' }}</button>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa chương trình khuyến mãi này?</div>
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
          <input class="search-input" v-model="searchCode" placeholder="Tìm mã Voucher, phân loại..." />
        </div>
        <div style="display: flex; gap: 6px; flex-wrap: wrap">
          <button 
            v-for="f in ['Tất cả', 'Đang chạy', 'Hết hạn']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: statusFilter === f }" 
            @click="statusFilter = f"
            style="font-size: 11px; padding: 4px 10px"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Tạo Voucher</button>
      </div>
      
      <div class="table-scroll">
        <table v-if="pagedPromos.length">
          <thead>
            <tr>
              <th>ID</th>
              <th>Mã Voucher</th>
              <th>Loại giảm giá</th>
              <th>Giá trị giảm</th>
              <th>Số đêm tối thiểu</th>
              <th>Ngày bắt đầu</th>
              <th>Ngày kết thúc</th>
              <th>Trạng thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="promo in pagedPromos" :key="promo.id">
              <td style="font-family: monospace; font-size: 12px; color: #60b4ff">{{ promo.id }}</td>
              <td style="font-weight: 700; color: var(--brand); letter-spacing: 0.05em">
                {{ promo.code.toUpperCase() }}
              </td>
              <td style="color: var(--muted)">{{ promo.discountType }}</td>
              <td style="color: #34d399; font-weight: 600">{{ promo.discountValueFmt }}</td>
              <td style="color: var(--muted)">{{ promo.minStay }} đêm</td>
              <td style="color: var(--muted); font-size: 12px">{{ promo.startDate }}</td>
              <td style="color: var(--muted); font-size: 12px">{{ promo.endDate }}</td>
              <td><span class="badge" :class="statusBadge(promo.status)">{{ promo.status }}</span></td>
              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', promo)">Sửa</button>
                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(promo.id)">Xóa</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-else class="empty">
          <div class="empty-icon">🏷️</div>
          <div class="empty-text">Không tìm thấy mã khuyến mãi nào</div>
        </div>
      </div>
      
      <div class="pagination" v-if="filteredPromos.length > pageSize">
        <span class="page-info">
          {{ (page - 1) * pageSize + 1 }}–{{ Math.min(page * pageSize, filteredPromos.length) }} / {{ filteredPromos.length }}
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
