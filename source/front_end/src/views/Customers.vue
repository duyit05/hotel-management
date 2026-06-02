<script setup>
import { useCustomerStore } from '@/stores/customer'
import { onMounted, reactive } from 'vue'
const props = defineProps({
  customers: {
    type: Array,
    required: true
  }
})
const emit = defineEmits(['show-toast'])
const customerStore = useCustomerStore();

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

const tierBadge = (rank) => {
  const maps = {
    STANDARD: 'badge-gray',
    GOLD: 'badge-gold',
    VIP: 'badge-vip'
  }

  return maps[rank] || 'badge-gray'
}

const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit'
    ? 'Chỉnh sửa khách hàng'
    : 'Thêm khách hàng mới'

  modal.data = row
    ? JSON.parse(JSON.stringify(row))
    : {}

  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const openDelete = (id) => {
  deleteModal.id = id
  deleteModal.open = true
}

const closeDelete = () => {
  deleteModal.open = false
}

const convertGender = (gender) => {
  switch (gender) {
    case 0:
      return 'Male'
    case 1:
      return 'Female'
    case 2:
      return 'Other'
    default:
      return '-'
  }
}

const convertStatus = (status) => {
  switch (status) {
    case 0:
      return 'Active'
    case 1:
      return 'Inactive'
    case 2:
      return 'Block'
    default:
      return '-'
  }
}

const statusBadge = (status) => {
  const maps = {
    0: 'badge-green',   // INACTIVE
    1: 'badge-gray',  // ACTIVE
    2: 'badge-red',    // BLOCK
  }

  return maps[status] || 'badge-gray'
}

const saveModal = async () => {
  if (modal.isEdit) {

  } else {
    const result = await customerStore.createCustomer(modal.data);
    console.log("result:", result);
    if (result.code == 201) {
      emit('show-toast', { message: 'Thêm khách hàng thành công!', type: 'success' })
      closeModal();
    } else {
      emit('show-toast', { message: result.message || 'Có lỗi xảy ra!', type: 'error' })
    }
  }
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
                <label class="form-label">Họ</label>
                <input class="form-control" v-model="modal.data.lastName" placeholder="Họ" />
              </div>
              <div class="form-group">
                <label class="form-label">Tên</label>
                <input class="form-control" v-model="modal.data.firstName" placeholder="Tên" />
              </div>
              <div class="form-group">
                <label class="form-label">Tên đăng nhập</label>
                <input class="form-control" v-model="modal.data.username" placeholder="Tên đăng nhập" />
              </div>
              <div class="form-group">
                <label class="form-label">Mật khẩu</label>
                <input class="form-control" v-model="modal.data.password" type="password" placeholder="••••••" />
              </div>
              <div class="form-group">
                <label class="form-label">Email</label>
                <input class="form-control" v-model="modal.data.email" placeholder="email@gmail.com" />
              </div>
              <div class="form-group">
                <label class="form-label">Số điện thoại</label>
                <input class="form-control" v-model="modal.data.phoneNumber" placeholder="0912345678" />
              </div>
              <div class="form-group">
                <label class="form-label">CCCD/Passport</label>
                <input class="form-control" v-model="modal.data.idCard" placeholder="0123456789" />
              </div>
              <div class="form-group">
                <label class="form-label">Ngày sinh</label>
                <input class="form-control" v-model="modal.data.dateOrBirth" type="date" />
              </div>
              <div class="form-group">
                <label class="form-label">Giới tính</label>
                <select class="form-control" v-model="modal.data.gender">
                  <option :value="0">Male</option>
                  <option :value="1">Female</option>
                  <option :value="2">Other</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Quốc tịch</label>
                <input class="form-control" v-model="modal.data.national" placeholder="Việt Nam" />
              </div>
              <div class="form-group">
                <label class="form-label">Hạng thành viên</label>
                <select class="form-control" v-model="modal.data.rank">
                  <option>Standard</option>
                  <option>Gold</option>
                  <option>VIP</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Hoạt động</option>
                  <option>Không hoạt động</option>
                </select>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa khách hàng này?
            </div>
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
        <h3>Danh sách khách hàng</h3>

        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">
          + Thêm khách hàng
        </button>
      </div>

      <div class="table-scroll">
        <table v-if="customers?.length">
          <thead>
            <tr>
              <th>Khách hàng</th>
              <th>Email</th>
              <th>Số điện thoại</th>
              <th>CCCD</th>
              <th>Quốc tịch</th>
              <th>Giới Tính</th>
              <th>Hạng</th>
              <th>Trạng Thái</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in customers" :key="c.id">
              <td>
                <div style="display: flex; align-items: center; gap: 8px">
                  <div class="avt avt-blue">
                    {{ c.username?.charAt(0).toUpperCase() }}
                  </div>

                  <div>
                    <div style="font-weight: 500; color: var(--text)">
                      {{ c.firstName }} {{ c.lastName }}
                    </div>

                    <div style="font-size: 11px; color: var(--subtle)">
                      {{ c.username }}
                    </div>
                  </div>
                </div>
              </td>
              <td style="color: var(--muted)">
                {{ c.email || '-' }}
              </td>
              <td style="color: var(--muted)">
                {{ c.phoneNumber || '-' }}
              </td>
              <td style="color: var(--muted)">
                {{ c.idCard || '-' }}
              </td>
              <td style="color: var(--muted)">
                {{ c.national || '-' }}
              </td>
              <td style="color: var(--muted)">
                {{ convertGender(c.gender) }}
              </td>
              <td>
                <span class="badge" :class="tierBadge(c.rank)">
                  {{ c.rank || '-' }}
                </span>
              </td>
              <td>
                <span class="badge" :class="statusBadge(c.status)">
                {{ convertStatus(c.status) }}
                </span>
              </td>

              <td>
                <div style="display: flex; gap: 4px">
                  <button class="btn btn-sm" @click.stop="openModal('edit', c)">
                    Sửa
                  </button>

                  <button class="btn btn-sm btn-danger" @click.stop="openDelete(c.id)">
                    Xóa
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div v-else class="empty">
          <div class="empty-icon">👥</div>
          <div class="empty-text">Không tìm thấy khách hàng</div>
        </div>
      </div>

    </div>
  </div>
</template>
