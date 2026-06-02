<script setup>
import { ref, computed, watch, reactive } from 'vue'

const props = defineProps({
  rooms: {
    type: Array,
    required: true
  },
  housekeeping: {
    type: Array,
    required: false,
    default: () => []
  }
})

const emit = defineEmits(['show-toast'])

// Search / filter / page state
const searchRooms = ref('')
const roomFilter = ref('Tất cả')
const roomPage = ref(1)
const pageSize = 8
const viewMode = ref('floorplan') // 'list', 'grid', or 'floorplan'
const selectedFloorPlanFloor = ref(1)

// Drawer state
const drawerOpen = ref(false)
const activeDrawerRoom = ref(null)

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
watch([searchRooms, roomFilter], () => {
  roomPage.value = 1
})

// Filtered & paged rooms
const filteredRooms = computed(() => {
  let r = props.rooms
  if (searchRooms.value) {
    r = r.filter(x => 
      [x.number, x.type, x.floor].join(' ').toLowerCase().includes(searchRooms.value.toLowerCase())
    )
  }
  if (roomFilter.value !== 'Tất cả') {
    r = r.filter(x => x.status === roomFilter.value)
  }
  return r
})

const pagedRooms = computed(() => {
  const s = (roomPage.value - 1) * pageSize
  return filteredRooms.value.slice(s, s + pageSize)
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredRooms.value.length / pageSize))
})

const roomsByFloor = computed(() => {
  const floors = {}
  filteredRooms.value.forEach(room => {
    if (!floors[room.floor]) {
      floors[room.floor] = []
    }
    floors[room.floor].push(room)
  })
  return Object.keys(floors).sort().reduce((acc, key) => {
    acc[key] = floors[key].sort((a, b) => a.number - b.number)
    return acc
  }, {})
})

const roomsForSelectedFloor = computed(() => {
  return props.rooms.filter(r => r.floor === selectedFloorPlanFloor.value).sort((a, b) => a.number - b.number)
})

// Badge helpers
const statusBadge = (s) => {
  const maps = {
    'Trống': 'badge-green',
    'Đã đặt': 'badge-blue',
    'Bảo trì': 'badge-amber'
  }
  return maps[s] || 'badge-gray'
}

// Drawer handlers
const openRoomDrawer = (room) => {
  // Initialize IoT simulated status if undefined
  if (room.temperature === undefined) room.temperature = 22
  if (room.acStatus === undefined) room.acStatus = true
  if (room.lightMode === undefined) room.lightMode = 'Relax'
  if (room.doorLocked === undefined) room.doorLocked = true
  if (room.curtainLevel === undefined) room.curtainLevel = 50
  if (room.dndStatus === undefined) room.dndStatus = false
  if (room.cleanRequested === undefined) room.cleanRequested = (room.status === 'Bảo trì')

  activeDrawerRoom.value = room
  drawerOpen.value = true
}

const closeRoomDrawer = () => {
  drawerOpen.value = false
}

// IoT Controls handlers
const toggleDoorLock = (room) => {
  room.doorLocked = !room.doorLocked
  emit('show-toast', room.doorLocked ? `Phòng ${room.number}: Cửa đã KHÓA an toàn.` : `Phòng ${room.number}: Cửa đã được MỞ KHÓA.`, 'success')
}

const toggleAc = (room) => {
  room.acStatus = !room.acStatus
  emit('show-toast', room.acStatus ? `Phòng ${room.number}: Đã bật điều hòa thông minh.` : `Phòng ${room.number}: Đã tắt điều hòa.`, 'info')
}

const adjustTemp = (room, delta) => {
  if (!room.acStatus) return
  room.temperature = Math.max(16, Math.min(30, room.temperature + delta))
}

const setLightMode = (room, mode) => {
  room.lightMode = mode
  emit('show-toast', `Phòng ${room.number}: Đèn chuyển sang chế độ ${mode}.`, 'success')
}

const toggleDnd = (room) => {
  room.dndStatus = !room.dndStatus
  if (room.dndStatus) {
    room.cleanRequested = false
    // Clear cleaning demand in housekeeping if DND is active
    if (props.housekeeping) {
      const hk = props.housekeeping.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Đang kiểm tra'
        hk.note = 'Khách bật Không làm phiền (DND). Hãy hoãn dọn dẹp.'
      }
    }
  }
  emit('show-toast', room.dndStatus ? `Phòng ${room.number}: Đã bật Không làm phiền (DND).` : `Phòng ${room.number}: Đã tắt chế độ Không làm phiền.`, 'info')
}

const toggleCleanRequest = (room) => {
  room.cleanRequested = !room.cleanRequested
  if (room.cleanRequested) {
    room.dndStatus = false
    // Sync with Housekeeping
    if (props.housekeeping) {
      const hk = props.housekeeping.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Chờ dọn'
        hk.note = 'Khách gửi yêu cầu dọn phòng khẩn cấp từ bảng điều khiển IoT.'
      } else {
        props.housekeeping.unshift({
          id: 'HK' + String(Date.now()).slice(-5),
          roomNumber: String(room.number),
          staffName: 'Chưa phân công',
          status: 'Chờ dọn',
          lastCleaned: 'Vừa yêu cầu',
          note: 'Khách gửi yêu cầu dọn phòng khẩn cấp từ bảng điều khiển IoT.'
        })
      }
    }
    emit('show-toast', `Đã chuyển yêu cầu dọn phòng ${room.number} đến bộ phận Buồng phòng.`, 'success')
  } else {
    if (props.housekeeping) {
      const hk = props.housekeeping.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Sạch sẽ'
        hk.note = 'Yêu cầu dọn dẹp đã được hủy từ phía phòng.'
      }
    }
    emit('show-toast', `Đã hủy yêu cầu dọn phòng ${room.number}.`, 'info')
  }
}

// Modal handlers
const openModal = (mode, row = null) => {
  modal.isEdit = mode === 'edit'
  modal.title = mode === 'edit' ? 'Chỉnh sửa phòng' : 'Thêm phòng mới'
  
  if (mode === 'edit' && row) {
    modal.data = JSON.parse(JSON.stringify(row))
  } else {
    modal.data = {
      number: '',
      floor: 1,
      type: 'Standard',
      status: 'Trống',
      beds: 1,
      size: 25,
      price: '',
      amenities: 'WiFi, TV',
      desc: ''
    }
  }
  modal.open = true
}

const closeModal = () => {
  modal.open = false
}

const saveModal = () => {
  if (!modal.data.number || !modal.data.price) {
    emit('show-toast', 'Vui lòng điền đầy đủ thông tin!', 'error')
    return
  }

  // Format amenities and price
  const amenitiesArr = typeof modal.data.amenities === 'string'
    ? modal.data.amenities.split(',').map(s => s.trim()).filter(Boolean)
    : modal.data.amenities
  
  const priceNum = Number(modal.data.price)
  const priceFormatted = '₫' + priceNum.toLocaleString('vi-VN')

  if (modal.isEdit) {
    const idx = props.rooms.findIndex(x => x.id === modal.data.id)
    if (idx !== -1) {
      props.rooms[idx] = {
        ...modal.data,
        amenities: amenitiesArr,
        price: priceNum,
        priceFmt: priceFormatted
      }
      emit('show-toast', 'Đã cập nhật phòng thành công!', 'success')
    }
  } else {
    const newId = 'RM' + String(Date.now()).slice(-5)
    props.rooms.unshift({
      id: newId,
      ...modal.data,
      amenities: amenitiesArr,
      price: priceNum,
      priceFmt: priceFormatted
    })
    emit('show-toast', 'Đã thêm phòng mới thành công!', 'success')
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
  const idx = props.rooms.findIndex(x => x.id === deleteModal.id)
  if (idx !== -1) {
    props.rooms.splice(idx, 1)
    emit('show-toast', 'Đã xóa phòng thành công!', 'success')
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
                <input class="form-control" v-model="modal.data.number" placeholder="101" />
              </div>
              <div class="form-group">
                <label class="form-label">Tầng</label>
                <input class="form-control" v-model="modal.data.floor" type="number" placeholder="1" />
              </div>
              <div class="form-group">
                <label class="form-label">Loại phòng</label>
                <select class="form-control" v-model="modal.data.type">
                  <option>Standard</option>
                  <option>Deluxe</option>
                  <option>Suite</option>
                  <option>Executive</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Trạng thái</label>
                <select class="form-control" v-model="modal.data.status">
                  <option>Trống</option>
                  <option>Đã đặt</option>
                  <option>Bảo trì</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Số giường</label>
                <input class="form-control" v-model="modal.data.beds" type="number" placeholder="1" />
              </div>
              <div class="form-group">
                <label class="form-label">Diện tích (m²)</label>
                <input class="form-control" v-model="modal.data.size" type="number" placeholder="25" />
              </div>
              <div class="form-group">
                <label class="form-label">Giá/đêm (VNĐ)</label>
                <input class="form-control" v-model="modal.data.price" placeholder="1200000" />
              </div>
              <div class="form-group">
                <label class="form-label">Tiện nghi</label>
                <input class="form-control" v-model="modal.data.amenities" placeholder="WiFi, TV, Mini bar" />
              </div>
              <div class="form-group form-full">
                <label class="form-label">Mô tả</label>
                <textarea class="form-control" v-model="modal.data.desc" rows="2" placeholder="Mô tả phòng..."></textarea>
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
            <div style="font-size: 15px; color: var(--text); margin-bottom: 6px">Bạn có chắc muốn xóa phòng này?</div>
            <div style="font-size: 13px; color: var(--muted)">Hành động này không thể hoàn tác.</div>
          </div>
          <div class="modal-footer">
            <button class="btn" @click="closeDelete">Hủy</button>
            <button class="btn btn-danger" @click="confirmDelete">Xóa</button>
          </div>
        </div>
      </div>
    </transition>

    <!-- Drawer Overlay -->
    <div v-if="drawerOpen" class="drawer-overlay" @click="closeRoomDrawer"></div>

    <!-- Right Drawer for Room detail & IoT Control Panel -->
    <div class="drawer" :class="{ 'open': drawerOpen }">
      <div class="drawer-header" v-if="activeDrawerRoom">
        <span style="font-weight: 700; font-size: 16px; color: var(--text)">Phòng {{ activeDrawerRoom.number }} — Chi tiết & IoT</span>
        <button class="btn btn-icon" @click="closeRoomDrawer" style="border: none; color: var(--muted); font-size: 18px">✕</button>
      </div>
      
      <div class="drawer-body" v-if="activeDrawerRoom">
        <!-- Room Specs Card -->
        <div class="iot-card" style="background: rgba(14, 135, 232, 0.04); border-color: rgba(14, 135, 232, 0.2)">
          <div style="display: flex; justify-content: space-between; align-items: center">
            <span style="font-weight: 600; font-size: 14px">{{ activeDrawerRoom.type }}</span>
            <span class="badge" :class="statusBadge(activeDrawerRoom.status)">{{ activeDrawerRoom.status }}</span>
          </div>
          <div style="font-size: 12px; color: var(--muted); display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-top: 4px">
            <span>🏢 Tầng: {{ activeDrawerRoom.floor }}</span>
            <span>🛏️ Số giường: {{ activeDrawerRoom.beds }} giường</span>
            <span>📐 Diện tích: {{ activeDrawerRoom.size }}m²</span>
            <span>💵 Giá/Đêm: <strong style="color: #60b4ff">{{ activeDrawerRoom.priceFmt }}</strong></span>
          </div>
          <div style="margin-top: 6px">
            <div style="font-size: 11px; text-transform: uppercase; letter-spacing: 0.05em; color: var(--subtle); font-weight: bold; margin-bottom: 4px">Tiện nghi</div>
            <div style="display: flex; gap: 4px; flex-wrap: wrap">
              <span v-for="a in activeDrawerRoom.amenities" :key="a" style="font-size: 10px; background: var(--hover); padding: 2px 6px; border-radius: 4px; color: var(--muted)">
                {{ a }}
              </span>
            </div>
          </div>
        </div>

        <!-- IoT Controls Section -->
        <div class="iot-section">
          <div style="font-weight: 600; font-size: 13px; text-transform: uppercase; color: var(--muted); letter-spacing: 0.05em; margin-bottom: 4px">
            ⚙️ Giả lập thiết bị thông minh (IoT Panel)
          </div>

          <!-- Smart Lock -->
          <div class="iot-card">
            <div class="iot-toggle">
              <div>
                <div style="font-weight: 600; font-size: 13px">🔑 Khóa cửa thông minh (Smart Door Lock)</div>
                <div style="font-size: 11px; color: var(--muted)">
                  Trạng thái: {{ activeDrawerRoom.doorLocked ? 'Đang Khóa' : 'Đang Mở' }}
                </div>
              </div>
              <button 
                class="btn btn-sm" 
                :class="activeDrawerRoom.doorLocked ? 'btn-primary' : ''"
                @click="toggleDoorLock(activeDrawerRoom)"
                style="padding: 6px 12px;"
              >
                {{ activeDrawerRoom.doorLocked ? '🔓 Mở khóa' : '🔒 Khóa lại' }}
              </button>
            </div>
          </div>

          <!-- Thermostat (Air Conditioner) -->
          <div class="iot-card">
            <div class="iot-toggle">
              <div>
                <div style="font-weight: 600; font-size: 13px">❄️ Điều hòa không khí (AC Control)</div>
                <div style="font-size: 11px; color: var(--muted)">
                  Chế độ: {{ activeDrawerRoom.acStatus ? 'Đang chạy' : 'Đã tắt' }}
                </div>
              </div>
              <label class="switch">
                <input type="checkbox" :checked="activeDrawerRoom.acStatus" @change="toggleAc(activeDrawerRoom)">
                <span class="slider"></span>
              </label>
            </div>
            
            <div v-if="activeDrawerRoom.acStatus" style="margin-top: 8px; border-top: 1px dashed var(--border); padding-top: 10px">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px">
                <span style="font-size: 12px; color: var(--muted)">Nhiệt độ phòng</span>
                <span class="temp-value">{{ activeDrawerRoom.temperature }}°C</span>
              </div>
              <div class="ac-buttons">
                <button class="ac-btn" @click="adjustTemp(activeDrawerRoom, -1)">-</button>
                <button class="ac-btn" @click="adjustTemp(activeDrawerRoom, 1)">+</button>
              </div>
            </div>
          </div>

          <!-- Lighting Modes -->
          <div class="iot-card">
            <div style="font-weight: 600; font-size: 13px; margin-bottom: 6px">💡 Hệ thống ánh sáng (Smart Lighting)</div>
            <div class="light-modes">
              <button 
                v-for="mode in ['Work', 'Relax', 'Romance', 'Off']" 
                :key="mode" 
                class="light-mode-btn"
                :class="{ 'active': activeDrawerRoom.lightMode === mode }"
                @click="setLightMode(activeDrawerRoom, mode)"
              >
                {{ mode === 'Work' ? '⚪ Sáng' : mode === 'Relax' ? '🟡 Ấm' : mode === 'Romance' ? '🔮 Tím' : '⚫ Tắt' }}
              </button>
            </div>
          </div>

          <!-- Curtain control -->
          <div class="iot-card">
            <div style="display: flex; justify-content: space-between; font-weight: 600; font-size: 13px; margin-bottom: 4px">
              <span>🌅 Rèm cửa thông minh (Smart Curtain)</span>
              <span style="color: var(--brand)">{{ activeDrawerRoom.curtainLevel }}%</span>
            </div>
            <input 
              type="range" 
              min="0" 
              max="100" 
              v-model="activeDrawerRoom.curtainLevel" 
              style="width: 100%; accent-color: var(--brand); cursor: pointer;"
            />
            <div style="display: flex; justify-content: space-between; font-size: 10px; color: var(--subtle)">
              <span>Đóng kính</span>
              <span>Mở 50%</span>
              <span>Mở hoàn toàn</span>
            </div>
          </div>

          <!-- Custom Signals (DND / Clean Room) -->
          <div class="iot-card">
            <div style="font-weight: 600; font-size: 13px; margin-bottom: 8px">📡 Tín hiệu buồng phòng</div>
            <div style="display: flex; gap: 8px">
              <button 
                class="btn btn-sm" 
                :class="activeDrawerRoom.dndStatus ? 'btn-danger' : ''"
                @click="toggleDnd(activeDrawerRoom)"
                style="flex: 1; padding: 8px;"
              >
                🚫 DND (Không làm phiền)
              </button>
              <button 
                class="btn btn-sm" 
                :class="activeDrawerRoom.cleanRequested ? 'btn-primary' : ''"
                @click="toggleCleanRequest(activeDrawerRoom)"
                style="flex: 1; padding: 8px;"
              >
                🧹 Yêu cầu dọn phòng
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="drawer-footer" v-if="activeDrawerRoom">
        <button class="btn btn-sm" style="flex: 1" @click="closeRoomDrawer(); openModal('edit', activeDrawerRoom)">Sửa thông tin</button>
        <button class="btn btn-sm btn-primary" style="flex: 1" @click="closeRoomDrawer()">Đóng lại</button>
      </div>
    </div>

    <!-- Table content container -->
    <div class="table-wrap">
      <div class="table-toolbar">
        <!-- View mode toggles -->
        <div style="display: flex; gap: 4px; margin-right: 8px; border-right: 1px solid var(--border); padding-right: 8px;">
          <button 
            class="btn btn-sm btn-icon" 
            :class="{ active: viewMode === 'floorplan' }" 
            @click="viewMode = 'floorplan'"
            title="Sơ đồ 2D mặt bằng"
            style="width: 28px; height: 28px; display: flex; align-items: center; justify-content: center"
          >
            🏢
          </button>
          <button 
            class="btn btn-sm btn-icon" 
            :class="{ active: viewMode === 'grid' }" 
            @click="viewMode = 'grid'"
            title="Sơ đồ phòng lưới"
            style="width: 28px; height: 28px; display: flex; align-items: center; justify-content: center"
          >
            🔲
          </button>
          <button 
            class="btn btn-sm btn-icon" 
            :class="{ active: viewMode === 'list' }" 
            @click="viewMode = 'list'"
            title="Danh sách bảng"
            style="width: 28px; height: 28px; display: flex; align-items: center; justify-content: center"
          >
            📋
          </button>
        </div>

        <div class="search-wrap">
          <svg fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <circle cx="11" cy="11" r="8" />
            <path d="m21 21-4.35-4.35" />
          </svg>
          <input class="search-input" v-model="searchRooms" placeholder="Tìm số phòng, loại, tầng..." />
        </div>
        <div style="display: flex; gap: 6px; flex-wrap: wrap">
          <button 
            v-for="f in ['Tất cả', 'Trống', 'Đã đặt', 'Bảo trì']" 
            :key="f" 
            class="filter-pill"
            :class="{ active: roomFilter === f }" 
            @click="roomFilter = f"
          >
            {{ f }}
          </button>
        </div>
        <button class="btn btn-primary btn-sm" style="margin-left: auto" @click="openModal('add')">+ Thêm phòng</button>
      </div>
      
      <!-- 2D Floor Plan Mode -->
      <template v-if="viewMode === 'floorplan'">
        <div style="padding: 20px">
          <div class="floor-plan-container">
            <!-- Floor Selection Toggles -->
            <div style="display: flex; gap: 8px; justify-content: center; margin-bottom: 8px">
              <button 
                v-for="floorNum in [1, 2, 3, 4]" 
                :key="floorNum" 
                class="filter-pill"
                :class="{ active: selectedFloorPlanFloor === floorNum }"
                @click="selectedFloorPlanFloor = floorNum"
              >
                Tầng {{ floorNum }}
              </button>
            </div>

            <!-- Floor layout schematic -->
            <div class="floor-layout">
              <!-- Top utilities block -->
              <div style="display: flex; gap: 8px; margin-bottom: 12px">
                <div class="floor-utility">
                  <span style="font-size: 16px">🛗</span>
                  <span style="font-weight: 600">Thang máy</span>
                </div>
                <div class="floor-utility">
                  <span style="font-size: 16px">🪜</span>
                  <span style="font-weight: 600">Thang bộ</span>
                </div>
                <div class="floor-corridor">
                  <span v-if="selectedFloorPlanFloor === 1">🛎️ Sảnh chờ & Lễ tân chính</span>
                  <span v-else>🛋️ Khu nghỉ ngơi hành lang chính</span>
                </div>
              </div>

              <!-- Main corridor & Rooms wings -->
              <div style="display: flex; flex-direction: column; gap: 8px">
                <div class="floor-corridor" style="background: rgba(255, 255, 255, 0.02); height: 28px; font-weight: 600">
                  ⚡ HÀNH LANG TẦNG {{ selectedFloorPlanFloor }} ⚡
                </div>
                
                <div class="floor-rooms">
                  <div 
                    v-for="r in roomsForSelectedFloor" 
                    :key="r.id" 
                    class="floor-room-box"
                    :class="{
                      'status-empty': r.status === 'Trống',
                      'status-occupied': r.status === 'Đã đặt',
                      'status-maintenance': r.status === 'Bảo trì'
                    }"
                    @click="openRoomDrawer(r)"
                  >
                    <div style="display: flex; justify-content: space-between; align-items: center; width: 100%">
                      <span class="floor-room-number">P.{{ r.number }}</span>
                      <span v-if="r.cleanRequested" class="floor-room-clean-indicator" title="Yêu cầu dọn dẹp">🧹</span>
                      <span v-else-if="r.dndStatus" class="floor-room-clean-indicator" title="DND Không làm phiền">🚫</span>
                    </div>
                    <span class="floor-room-type">{{ r.type }}</span>
                    <div 
                      class="floor-room-status-dot"
                      :class="{
                        'status-empty': r.status === 'Trống',
                        'status-occupied': r.status === 'Đã đặt',
                        'status-maintenance': r.status === 'Bảo trì'
                      }"
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Floor plan legend -->
            <div style="display: flex; gap: 14px; justify-content: center; font-size: 11px; color: var(--muted); margin-top: 10px">
              <div style="display: flex; align-items: center; gap: 4px">
                <span style="width: 8px; height: 8px; border-radius: 50%; background: #10b981"></span> Trống
              </div>
              <div style="display: flex; align-items: center; gap: 4px">
                <span style="width: 8px; height: 8px; border-radius: 50%; background: #0e87e8"></span> Đang có khách
              </div>
              <div style="display: flex; align-items: center; gap: 4px">
                <span style="width: 8px; height: 8px; border-radius: 50%; background: #f59e0b"></span> Bảo trì/Dọn dẹp
              </div>
              <div style="display: flex; align-items: center; gap: 4px">
                <span>🧹</span> Khách gọi dọn
              </div>
              <div style="display: flex; align-items: center; gap: 4px">
                <span>🚫</span> Không làm phiền (DND)
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- List View (Standard Table) -->
      <template v-else-if="viewMode === 'list'">
        <div class="table-scroll">
          <table v-if="pagedRooms.length">
            <thead>
              <tr>
                <th>Số phòng</th>
                <th>Tầng</th>
                <th>Loại</th>
                <th>Giường</th>
                <th>Diện tích</th>
                <th>Tiện nghi</th>
                <th>Giá/đêm</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in pagedRooms" :key="r.id" @click="openRoomDrawer(r)">
                <td style="font-weight: 600; color: var(--text)">{{ r.number }}</td>
                <td style="color: var(--muted)">{{ r.floor }}</td>
                <td style="color: var(--muted)">{{ r.type }}</td>
                <td style="color: var(--muted)">{{ r.beds }}</td>
                <td style="color: var(--muted)">{{ r.size }}m²</td>
                <td>
                  <span 
                    v-for="a in r.amenities.slice(0, 2)" 
                    :key="a"
                    style="font-size: 11px; background: var(--hover); padding: 2px 6px; border-radius: 4px; color: var(--muted); margin-right: 4px"
                  >
                    {{ a }}
                  </span>
                  <span v-if="r.amenities.length > 2" style="font-size: 11px; color: var(--subtle)">...</span>
                </td>
                <td style="color: #60b4ff; font-weight: 600">{{ r.priceFmt }}</td>
                <td><span class="badge" :class="statusBadge(r.status)">{{ r.status }}</span></td>
                <td>
                  <div style="display: flex; gap: 4px">
                    <button class="btn btn-sm" @click.stop="openModal('edit', r)">Sửa</button>
                    <button class="btn btn-sm btn-danger" @click.stop="openDelete(r.id)">Xóa</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
          
          <div v-else class="empty">
            <div class="empty-icon">🛏️</div>
            <div class="empty-text">Không tìm thấy phòng nào</div>
          </div>
        </div>
        
        <div class="pagination" v-if="filteredRooms.length > pageSize">
          <span class="page-info">
            {{ (roomPage - 1) * pageSize + 1 }}–{{ Math.min(roomPage * pageSize, filteredRooms.length) }} / {{ filteredRooms.length }}
          </span>
          <div class="page-btns">
            <button class="page-btn" :disabled="roomPage === 1" @click="roomPage--">‹</button>
            <button 
              v-for="p in totalPages" 
              :key="p" 
              class="page-btn"
              :class="{ active: roomPage === p }" 
              @click="roomPage = p"
            >
              {{ p }}
            </button>
            <button class="page-btn" :disabled="roomPage === totalPages" @click="roomPage++">›</button>
          </div>
        </div>
      </template>

      <!-- Grid View (Interactive Visual Room Map) -->
      <template v-else>
        <div style="padding: 20px; display: flex; flex-direction: column; gap: 24px">
          <div v-for="(roomsOnFloor, floorNum) in roomsByFloor" :key="floorNum" style="border-bottom: 1px solid var(--border); padding-bottom: 20px;">
            <div style="font-size: 14px; font-weight: 600; color: var(--text); margin-bottom: 14px; display: flex; align-items: center; gap: 8px">
              <span>🏢 Tầng {{ floorNum }}</span>
              <span style="font-size: 11px; color: var(--muted); font-weight: normal">({{ roomsOnFloor.length }} phòng)</span>
            </div>
            <div class="room-grid">
              <div 
                v-for="r in roomsOnFloor" 
                :key="r.id" 
                class="room-card"
                @click="openRoomDrawer(r)"
                :style="{
                  borderLeft: '4px solid ' + (r.status === 'Trống' ? '#10b981' : r.status === 'Đã đặt' ? '#0e87e8' : '#f59e0b')
                }"
              >
                <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px">
                  <span style="font-size: 15px; font-weight: 700; color: var(--text)">Phòng {{ r.number }}</span>
                  <span class="badge" :class="statusBadge(r.status)" style="font-size: 9px; padding: 1px 5px">{{ r.status }}</span>
                </div>
                <div style="font-size: 11px; color: var(--muted); margin-bottom: 8px; line-height: 1.4">
                  {{ r.type }} • {{ r.beds }} giường<br>{{ r.size }}m²
                </div>
                <div style="font-size: 12px; font-weight: 600; color: #60b4ff; display: flex; justify-content: space-between; align-items: center; border-top: 1px dashed var(--border); padding-top: 6px; margin-top: 4px">
                  <span>Giá/đêm</span>
                  <span>{{ r.priceFmt }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="!filteredRooms.length" class="empty">
            <div class="empty-icon">🛏️</div>
            <div class="empty-text">Không tìm thấy phòng nào phù hợp bộ lọc</div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

