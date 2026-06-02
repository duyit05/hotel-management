<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import Chart from 'chart.js/auto'

const props = defineProps({
  loading: Boolean,
  stats: Array,
  roomStatus: Array,
  bookings: Array
})

const emit = defineEmits(['set-page'])

const chartPeriod = ref('12T')
const canvasRevenue = ref(null)
const canvasRoom = ref(null)

let revChart = null
let roomChartInst = null

// Data
const rev12 = [820, 950, 1100, 980, 1240, 1380, 1200, 1450, 1320, 1580, 1720, 1900]
const rev6 = [1200, 1450, 1320, 1580, 1720, 1900]

const redrawChart = () => {
  if (!revChart) return
  const d = chartPeriod.value === '12T' ? rev12 : rev6
  const labels = chartPeriod.value === '12T' 
    ? ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'] 
    : ['T7', 'T8', 'T9', 'T10', 'T11', 'T12']
  
  revChart.data.labels = labels
  revChart.data.datasets[0].data = d
  revChart.update()
}

const drawCharts = () => {
  if (!canvasRevenue.value || !canvasRoom.value) return
  
  if (revChart) revChart.destroy()
  if (roomChartInst) roomChartInst.destroy()

  revChart = new Chart(canvasRevenue.value, {
    type: 'line',
    data: { 
      labels: ['T1', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12'], 
      datasets: [{ 
        label: 'Doanh thu', 
        data: rev12, 
        borderColor: '#0e87e8', 
        backgroundColor: 'rgba(14,135,232,.08)', 
        borderWidth: 2, 
        fill: true, 
        tension: .4, 
        pointBackgroundColor: '#0e87e8', 
        pointRadius: 3, 
        pointHoverRadius: 6 
      }] 
    },
    options: { 
      responsive: true, 
      maintainAspectRatio: false, 
      plugins: { 
        legend: { display: false }, 
        tooltip: { 
          backgroundColor: '#161e2e', 
          borderColor: '#1e2d45', 
          borderWidth: 1, 
          titleColor: '#94a3b8', 
          bodyColor: '#fff', 
          padding: 10 
        } 
      }, 
      scales: { 
        x: { 
          grid: { color: 'rgba(30,45,69,.2)' }, 
          ticks: { color: '#475569', font: { size: 11 } } 
        }, 
        y: { 
          grid: { color: 'rgba(30,45,69,.2)' }, 
          ticks: { color: '#475569', font: { size: 11 } } 
        } 
      } 
    }
  })

  roomChartInst = new Chart(canvasRoom.value, {
    type: 'doughnut',
    data: { 
      labels: ['Trống', 'Đã đặt', 'Bảo trì', 'Khác'], 
      datasets: [{ 
        data: [18, 24, 4, 2], 
        backgroundColor: ['#10b981', '#0e87e8', '#f59e0b', '#6b7280'], 
        borderWidth: 0, 
        hoverOffset: 4 
      }] 
    },
    options: { 
      responsive: true, 
      maintainAspectRatio: false, 
      plugins: { 
        legend: { display: false }, 
        tooltip: { 
          backgroundColor: '#161e2e', 
          borderColor: '#1e2d45', 
          borderWidth: 1, 
          titleColor: '#94a3b8', 
          bodyColor: '#fff', 
          padding: 10 
        } 
      }, 
      cutout: '72%' 
    }
  })
}

onMounted(() => {
  nextTick(() => {
    drawCharts()
  })
})

onUnmounted(() => {
  if (revChart) revChart.destroy()
  if (roomChartInst) roomChartInst.destroy()
})

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
</script>

<template>
  <div>
    <!-- Quick Actions Hub -->
    <div class="card" style="margin-bottom: 18px; padding: 14px 18px;">
      <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 10px">
        <div style="display: flex; align-items: center; gap: 8px">
          <span style="font-size: 18px">⚡</span>
          <span style="font-weight: 600; font-size: 14px; color: var(--text)">Thao tác nhanh hệ thống:</span>
        </div>
        <div style="display: flex; gap: 8px; flex-wrap: wrap">
          <button class="btn btn-sm btn-primary" @click="$emit('set-page', 'bookings')">
            <span>➕</span> Đặt phòng nhanh
          </button>
          <button class="btn btn-sm" @click="$emit('set-page', 'services')" style="border-color: var(--brand-border); background: var(--brand-light)">
            <span>🍽️</span> POS Room Service
          </button>
          <button class="btn btn-sm" @click="$emit('set-page', 'housekeeping')">
            <span>🧹</span> Phân công dọn phòng
          </button>
          <button class="btn btn-sm" @click="$emit('set-page', 'promotions')">
            <span>🎫</span> Tạo khuyến mãi
          </button>
        </div>
      </div>
    </div>

    <div class="stats-grid">
      <template v-if="loading">
        <div v-for="i in 4" :key="i" class="skeleton" style="height: 100px"></div>
      </template>
      <template v-else>
        <div v-for="s in stats" :key="s.label" class="stat-card">
          <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px">
            <span class="stat-label">{{ s.label }}</span>
            <span class="stat-badge" :class="s.up ? 'up' : 'down'">{{ s.up ? '▲' : '▼' }} {{ s.change }}</span>
          </div>
          <div style="display: flex; align-items: center; justify-content: space-between">
            <div class="stat-value">{{ s.value }}</div>
            <div style="font-size: 24px; opacity: 0.85">
              <span v-if="s.label.includes('doanh thu')">💰</span>
              <span v-else-if="s.label.includes('đặt phòng')">📅</span>
              <span v-else-if="s.label.includes('hàng')">👥</span>
              <span v-else>📈</span>
            </div>
          </div>
          <div class="stat-sub">{{ s.sub }}</div>
        </div>
      </template>
    </div>
    
    <div class="charts-grid">
      <div class="card">
        <div class="card-header">
          <div>
            <div class="card-title">Doanh thu theo tháng</div>
            <div class="card-sub">Năm 2024 (triệu ₫)</div>
          </div>
          <div style="display: flex; gap: 6px">
            <button 
              v-for="p in ['6T', '12T']" 
              :key="p" 
              class="filter-pill" 
              :class="{ active: chartPeriod === p }"
              @click="chartPeriod = p; redrawChart()"
            >
              {{ p }}
            </button>
          </div>
        </div>
        <div style="height: 210px">
          <canvas ref="canvasRevenue"></canvas>
        </div>
      </div>
      
      <div class="card">
        <div class="card-header">
          <div class="card-title">Tình trạng phòng</div>
        </div>
        <div style="position: relative; height: 160px; display: flex; align-items: center; justify-content: center">
          <canvas ref="canvasRoom"></canvas>
          <div style="position: absolute; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none">
            <span style="font-size: 20px; font-weight: 700; color: var(--text)">84.3%</span>
            <span style="font-size: 10px; color: var(--muted)">Lấp đầy</span>
          </div>
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-top: 12px">
          <div v-for="r in roomStatus" :key="r.label" style="display: flex; align-items: center; gap: 6px">
            <span style="width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0" :style="{ background: r.color }"></span>
            <span style="font-size: 11px; color: var(--muted)">{{ r.label }}</span>
            <span style="font-size: 12px; font-weight: 600; color: var(--text); margin-left: auto">{{ r.value }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Recent bookings table -->
    <div class="table-wrap">
      <div class="table-toolbar">
        <span class="card-title">Đặt phòng mới nhất</span>
        <button class="btn btn-sm" style="margin-left: auto" @click="$emit('set-page', 'bookings')">Xem tất cả →</button>
      </div>
      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th>Khách hàng</th>
              <th>Phòng</th>
              <th>Check-in</th>
              <th>Check-out</th>
              <th>Trạng thái</th>
              <th>Tổng tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="b in bookings.slice(0, 6)" :key="b.id">
              <td>
                <div style="display: flex; align-items: center; gap: 8px">
                  <div class="avt avt-blue">{{ b.name[0] }}</div>
                  <span style="font-weight: 500; color: var(--text)">{{ b.name }}</span>
                </div>
              </td>
              <td style="color: var(--muted)">{{ b.roomLabel }}</td>
              <td style="color: var(--muted)">{{ b.checkin }}</td>
              <td style="color: var(--muted)">{{ b.checkout }}</td>
              <td><span class="badge" :class="statusBadge(b.status)">{{ b.status }}</span></td>
              <td style="color: #34d399; font-weight: 600">{{ b.totalFmt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
