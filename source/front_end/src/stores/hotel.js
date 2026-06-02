import { defineStore } from 'pinia'
import { ref, reactive, computed } from 'vue'
import router from '../router'
import apiClient from '../api/client'

export const useHotelStore = defineStore('hotel', () => {
  // Watch route changes to keep currentPage in sync
  router.afterEach((to) => {
    if (to.name) {
      currentPage.value = String(to.name)
    }
  })

  // ── Shared UI State ──
  const isDark = ref(true)
  const sidebarOpen = ref(window.innerWidth >= 768)
  const isMobile = ref(window.innerWidth < 768)
  const loading = ref(true)
  const currentPage = ref('dashboard')

  // Toast state
  const toast = reactive({ show: false, msg: '', type: 'success' })
  let toastTimeout = null

  const showToast = (msg, type = 'success') => {
    if (toastTimeout) clearTimeout(toastTimeout)
    toast.msg = msg
    toast.type = type
    toast.show = true
    toastTimeout = setTimeout(() => {
      toast.show = false
    }, 2500)
  }

  // ── Mock Data ──
  const stats = ref([
    { label: 'Tổng doanh thu', value: '₫2.48 tỷ', change: '18.2%', up: true, sub: 'So với tháng trước' },
    { label: 'Tổng đặt phòng', value: '1,284', change: '9.1%', up: true, sub: 'Trong tháng này' },
    { label: 'Khách hàng', value: '3,621', change: '5.4%', up: true, sub: 'Tổng khách đã đăng ký' },
    { label: 'Tỷ lệ lấp đầy', value: '84.3%', change: '2.1%', up: false, sub: 'Trung bình 30 ngày' },
  ])

  const roomStatus = [
    { label: 'Trống', value: 18, color: '#10b981' },
    { label: 'Đã đặt', value: 24, color: '#0e87e8' },
    { label: 'Bảo trì', value: 4, color: '#f59e0b' },
    { label: 'Khác', value: 2, color: '#6b7280' }
  ]

  const names = ['Nguyễn Văn An', 'Trần Thị Bình', 'Lê Minh Cường', 'Phạm Thu Dung', 'Hoàng Văn Em', 'Vũ Thị Phương', 'Đặng Quốc Hùng', 'Bùi Thị Lan', 'Ngô Văn Minh', 'Lý Kim Ngân', 'Trương Đức Phúc', 'Đinh Thị Quỳnh', 'Phan Văn Nam', 'Đỗ Thị Oanh', 'Trịnh Minh Phát', 'Cao Thị Quyên', 'Lưu Văn Thắng', 'Mai Thị Uyên']

  const rooms = ref(Array.from({ length: 48 }, (_, i) => {
    const price = [1200000, 2500000, 800000, 1800000][i % 4]
    return {
      id: 'R' + (i + 1),
      number: 100 + i + 1,
      floor: Math.ceil((i + 1) / 12),
      type: ['Deluxe', 'Suite', 'Standard', 'Executive'][i % 4],
      status: ['Trống', 'Đã đặt', 'Bảo trì', 'Trống'][i % 4],
      beds: [1, 2, 1, 2][i % 4],
      size: [25, 45, 20, 35][i % 4],
      price,
      priceFmt: '₫' + price.toLocaleString('vi-VN'),
      amenities: [['WiFi', 'TV'], ['WiFi', 'TV', 'Bồn tắm'], ['WiFi'], ['WiFi', 'TV', 'Mini bar']][i % 4],
      desc: '',
      // IoT States initialized
      temperature: 22,
      acStatus: true,
      lightMode: 'Relax',
      doorLocked: true,
      curtainLevel: 50,
      dndStatus: false,
      cleanRequested: ['Trống', 'Đã đặt', 'Bảo trì', 'Trống'][i % 4] === 'Bảo trì'
    }
  }))

  const customers = ref(names.map((n, i) => ({
    id: 'KH' + String(i + 1).padStart(4, '0'),
    name: n,
    email: n.split(' ').pop().toLowerCase() + '@gmail.com',
    phone: '09' + String(10000000 + Math.floor(Math.random() * 89999999)),
    cccd: String(Math.floor(1e11 + Math.random() * 9e11)),
    dob: `${1970 + (i % 30)}-0${1 + (i % 9)}-${10 + (i % 18)}`,
    nation: i % 6 === 0 ? 'Hoa Kỳ' : i % 5 === 0 ? 'Nhật Bản' : 'Việt Nam',
    bookings: Math.floor(1 + Math.random() * 20),
    tier: ['Standard', 'Gold', 'VIP'][i % 3],
    status: i % 5 === 0 ? 'Không hoạt động' : 'Hoạt động',
    note: ''
  })))

  const bookings = ref(Array.from({ length: 40 }, (_, i) => {
    const room = rooms.value[i % rooms.value.length]
    const cust = customers.value[i % customers.value.length]
    return {
      id: 'BK' + String(i + 1).padStart(5, '0'),
      customerId: cust.id,
      name: cust.name,
      roomId: room.id,
      roomLabel: 'Phòng ' + room.number,
      checkin: `${String(1 + (i * 7) % 27).padStart(2, '0')}/0${1 + i % 9}/2024`,
      checkout: `${String(3 + (i * 7) % 25).padStart(2, '0')}/0${2 + i % 8}/2024`,
      booked: `${String(1 + i % 28).padStart(2, '0')}/0${1 + i % 6}/2024`,
      guests: 1 + (i % 4),
      status: ['Đã xác nhận', 'Đang ở', 'Đã trả phòng', 'Chờ xác nhận', 'Đã hủy'][i % 5],
      totalFmt: '₫' + Math.floor(800 + Math.random() * 4200) + 'k',
      note: ''
    }
  }))

  const invoices = ref(Array.from({ length: 30 }, (_, i) => {
    const cust = customers.value[i % customers.value.length]
    const book = bookings.value[i % bookings.value.length]
    const amount = Math.floor(800000 + Math.random() * 9200000)
    return {
      id: 'HD' + String(i + 1).padStart(6, '0'),
      customerId: cust.id,
      customerName: cust.name,
      bookingId: book.id,
      issued: `${String(1 + i % 28).padStart(2, '0')}/0${1 + i % 9}/2024`,
      due: `${String(5 + i % 24).padStart(2, '0')}/0${2 + i % 8}/2024`,
      status: ['Đã thanh toán', 'Chờ thanh toán', 'Quá hạn'][i % 3],
      amount,
      amountFmt: '₫' + amount.toLocaleString('vi-VN'),
      note: i % 3 === 0 ? 'Giảm giá thành viên 10%' : ''
    }
  }))

  const staff = ref([
    { id: 'S1', name: 'Nguyễn Thị Mai', dept: 'Lễ tân', shift: 'Ca sáng 6h-14h', since: '2022-01-15', email: 'mai@luxstay.vn', phone: '0912000001', salary: 12000000, salaryFmt: '₫12.000.000/tháng', active: true },
    { id: 'S2', name: 'Trần Văn Hùng', dept: 'Bếp', shift: 'Ca chiều 14h-22h', since: '2021-03-20', email: 'hung@luxstay.vn', phone: '0912000002', salary: 15000000, salaryFmt: '₫15.000.000/tháng', active: true },
    { id: 'S3', name: 'Lê Thị Hoa', dept: 'Buồng phòng', shift: 'Ca sáng 7h-15h', since: '2023-06-01', email: 'hoa@luxstay.vn', phone: '0912000003', salary: 10000000, salaryFmt: '₫10.000.000/tháng', active: false },
    { id: 'S4', name: 'Phạm Minh Tuấn', dept: 'Bảo vệ', shift: 'Ca đêm 22h-6h', since: '2020-11-10', email: 'tuan@luxstay.vn', phone: '0912000004', salary: 11000000, salaryFmt: '₫11.000.000/tháng', active: true },
    { id: 'S5', name: 'Hoàng Thu Hằng', dept: 'Marketing', shift: 'Hành chính 8h-17h', since: '2022-09-05', email: 'hang@luxstay.vn', phone: '0912000005', salary: 18000000, salaryFmt: '₫18.000.000/tháng', active: true },
    { id: 'S6', name: 'Vũ Đức Anh', dept: 'IT', shift: 'Hành chính 8h-17h', since: '2023-02-14', email: 'anh@luxstay.vn', phone: '0912000006', salary: 20000000, salaryFmt: '₫20.000.000/tháng', active: true },
    { id: 'S7', name: 'Đặng Thị Linh', dept: 'Kế toán', shift: 'Hành chính 8h-17h', since: '2021-07-01', email: 'linh@luxstay.vn', phone: '0912000007', salary: 16000000, salaryFmt: '₫16.000.000/tháng', active: true },
    { id: 'S8', name: 'Ngô Quang Minh', dept: 'Lễ tân', shift: 'Ca đêm 22h-6h', since: '2023-10-01', email: 'minh@luxstay.vn', phone: '0912000008', salary: 12500000, salaryFmt: '₫12.500.000/tháng', active: false },
  ])

  const reports = [
    { icon: '📊', title: 'Báo cáo doanh thu', desc: 'Phân tích doanh thu theo ngày, tháng, năm và theo loại phòng' },
    { icon: '🛏️', title: 'Báo cáo công suất phòng', desc: 'Tỷ lệ lấp đầy, thời gian trống trung bình, xu hướng theo mùa' },
    { icon: '👥', title: 'Báo cáo khách hàng', desc: 'Thống kê khách hàng mới, quay lại, phân khúc và đánh giá' },
    { icon: '💰', title: 'Báo cáo tài chính', desc: 'Tổng hợp thu chi, công nợ, dự báo dòng tiền' },
    { icon: '🧹', title: 'Báo cáo vận hành', desc: 'Hiệu suất bộ phận, lịch bảo trì, tiêu hao vật tư' },
    { icon: '⭐', title: 'Báo cáo đánh giá', desc: 'Điểm hài lòng khách hàng, phản hồi và cải thiện dịch vụ' },
  ]

  const settingGroups = ref([
    {
      title: 'Thông tin khách sạn', items: [
        { label: 'Tên khách sạn', desc: 'LuxStay Grand Hotel', type: 'select', options: ['LuxStay Grand Hotel'] },
        { label: 'Múi giờ', desc: 'Giờ hiển thị trong hệ thống', type: 'select', options: ['GMT+7 (Việt Nam)', 'GMT+8', 'GMT+0'] },
      ]
    },
    {
      title: 'Thông báo', items: [
        { label: 'Email thông báo đặt phòng', desc: 'Nhận email khi có đặt phòng mới', type: 'toggle', value: true },
        { label: 'Cảnh báo phòng trống', desc: 'Thông báo khi tỷ lệ lấp đầy dưới 60%', type: 'toggle', value: false },
        { label: 'Báo cáo hàng ngày', desc: 'Gửi tóm tắt lúc 7h sáng mỗi ngày', type: 'toggle', value: true },
      ]
    },
    {
      title: 'Bảo mật', items: [
        { label: 'Xác thực 2 bước', desc: 'Yêu cầu OTP khi đăng nhập', type: 'toggle', value: true },
        { label: 'Tự động đăng xuất', desc: 'Sau 30 phút không hoạt động', type: 'toggle', value: false },
      ]
    },
  ])

  const services = ref([
    { id: 'SV00001', name: 'Buffet sáng hải sản', type: 'Ẩm thực', price: 350000, priceFmt: '₫350.000', status: 'Hoạt động', desc: 'Buffet sáng tự chọn đẳng cấp 5 sao tại nhà hàng LuxGrand.' },
    { id: 'SV00002', name: 'Massage thảo dược toàn thân', type: 'Spa & Massage', price: 650000, priceFmt: '₫650.000', status: 'Hoạt động', desc: 'Liệu trình massage 60 phút giúp thư giãn cơ thể và giảm stress.' },
    { id: 'SV00003', name: 'Giặt sấy lấy nhanh', type: 'Giặt là', price: 90000, priceFmt: '₫90.000', status: 'Hoạt động', desc: 'Giặt hấp và sấy khô quần áo giao trả tận phòng trong vòng 4 tiếng.' },
    { id: 'SV00004', name: 'Đón tiễn sân bay bằng xe Limousine', type: 'Vận chuyển', price: 1200000, priceFmt: '₫1.200.000', status: 'Hoạt động', desc: 'Dịch vụ xe riêng đưa đón sân bay Nội Bài / Tân Sơn Nhất.' },
    { id: 'SV00005', name: 'Trang trí phòng trăng mật', type: 'Tiện ích phòng', price: 800000, priceFmt: '₫800.000', status: 'Tạm ngưng', desc: 'Setup nến, hoa hồng tươi và rượu vang chúc mừng các cặp đôi.' },
    { id: 'SV00006', name: 'Thức uống Welcome Mocktail', type: 'Ẩm thực', price: 0, priceFmt: '₫0', status: 'Hoạt động', desc: 'Nước uống chào mừng miễn phí khi làm thủ tục check-in.' },
  ])

  const housekeeping = ref([
    { id: 'HK00001', roomNumber: '101', staffName: 'Lê Thị Hoa', status: 'Sạch sẽ', lastCleaned: '01/06/2026 09:30', note: 'Phòng đã được dọn sạch và bổ sung đầy đủ nước uống.' },
    { id: 'HK00002', roomNumber: '102', staffName: 'Lê Thị Hoa', status: 'Đang dọn', lastCleaned: '01/06/2026 10:00', note: 'Đang thay ga giường và hút bụi.' },
    { id: 'HK00003', roomNumber: '103', staffName: 'Trần Văn Hùng', status: 'Chờ dọn', lastCleaned: '01/06/2026 08:15', note: 'Khách vừa check-out lúc 12h00.' },
    { id: 'HK00004', roomNumber: '104', staffName: 'Lê Thị Hoa', status: 'Đang kiểm tra', lastCleaned: '01/06/2026 09:45', note: 'Chờ giám sát buồng phòng duyệt để bàn giao.' },
    { id: 'HK00005', roomNumber: '105', staffName: 'Trần Văn Hùng', status: 'Sạch sẽ', lastCleaned: '01/06/2026 09:10', note: 'Phòng trống đã sẵn sàng đón khách.' },
  ])

  const promotions = ref([
    { id: 'PR00001', code: 'LUXWELCOME', discountType: 'Số tiền cố định (VNĐ)', discountValue: 500000, discountValueFmt: '₫500.000', minStay: 1, startDate: '01/01/2026', endDate: '31/12/2026', status: 'Đang chạy' },
    { id: 'PR00002', code: 'SUMMER20', discountType: 'Phần trăm (%)', discountValue: 20, discountValueFmt: '20%', minStay: 2, startDate: '01/06/2026', endDate: '31/08/2026', status: 'Đang chạy' },
    { id: 'PR00003', code: 'MIDWEEK30', discountType: 'Phần trăm (%)', discountValue: 30, discountValueFmt: '30%', minStay: 3, startDate: '01/02/2026', endDate: '30/11/2026', status: 'Đang chạy' },
    { id: 'PR00004', code: 'VIPMEMBER', discountType: 'Số tiền cố định (VNĐ)', discountValue: 1000000, discountValueFmt: '₫1.000.000', minStay: 1, startDate: '01/01/2026', endDate: '31/12/2026', status: 'Hết hạn' },
  ])

  // ── POS Cart State ──
  const cartItems = ref([])
  const selectedBookingId = ref('')
  const voucherCode = ref('')
  const activeDiscount = ref(0)
  const appliedVoucher = ref(null)

  // ── Shared UI Handlers ──
  const setPage = (id) => {
    router.push('/' + id)
    if (isMobile.value) {
      sidebarOpen.value = false
    }
  }

  const toggleDark = () => {
    isDark.value = !isDark.value
    const html = document.documentElement
    if (isDark.value) {
      html.classList.add('dark')
      html.classList.remove('light')
    } else {
      html.classList.add('light')
      html.classList.remove('dark')
    }
  }

  const toggleSidebar = () => {
    sidebarOpen.value = !sidebarOpen.value
  }

  const onResize = () => {
    isMobile.value = window.innerWidth < 768
    if (!isMobile.value) {
      sidebarOpen.value = true
    }
  }

  // ── Business Actions ──

  // POS operations
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
    showToast(`Đã thêm "${service.name}" vào giỏ hàng`, 'success')
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
    const promo = promotions.value.find(p => p.code.toUpperCase() === voucherCode.value.toUpperCase() && p.status === 'Đang chạy')
    if (promo) {
      appliedVoucher.value = promo
      activeDiscount.value = promo.discountValue
      showToast(`Đã áp dụng mã giảm giá ${promo.code}!`, 'success')
    } else {
      showToast('Mã giảm giá không hợp lệ hoặc đã hết hạn!', 'error')
      appliedVoucher.value = null
      activeDiscount.value = 0
    }
  }

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
    return Math.floor((cartSubtotal.value - discountAmount.value) * 0.1)
  })

  const cartTotal = computed(() => {
    return Math.max(0, cartSubtotal.value - discountAmount.value + cartTax.value)
  })

  const submitPOSOrder = () => {
    if (!selectedBookingId.value) {
      showToast('Vui lòng chọn phòng đang sử dụng dịch vụ!', 'error')
      return
    }
    if (!cartItems.value.length) {
      showToast('Giỏ hàng đang trống!', 'error')
      return
    }

    const booking = bookings.value.find(b => b.id === selectedBookingId.value)
    if (!booking) return

    let invoice = invoices.value.find(inv => inv.bookingId === booking.id)
    const orderNotes = cartItems.value.map(i => `${i.name} (x${i.qty})`).join(', ')

    if (invoice) {
      invoice.amount += cartTotal.value
      invoice.amountFmt = '₫' + invoice.amount.toLocaleString('vi-VN')
      invoice.note = (invoice.note ? invoice.note + '; ' : '') + `POS: ${orderNotes} [Tổng: ₫${cartTotal.value.toLocaleString('vi-VN')}]`
    } else {
      invoices.value.unshift({
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

    showToast(`Đã gọi dịch vụ cho Phòng ${booking.roomLabel}. Chi phí đã được tính vào hóa đơn!`, 'success')

    // Reset cart
    cartItems.value = []
    selectedBookingId.value = ''
    voucherCode.value = ''
    activeDiscount.value = 0
    appliedVoucher.value = null
  }

  // IoT Handlers
  const toggleDoorLock = (room) => {
    room.doorLocked = !room.doorLocked
    showToast(room.doorLocked ? `Phòng ${room.number}: Cửa đã KHÓA an toàn.` : `Phòng ${room.number}: Cửa đã được MỞ KHÓA.`, 'success')
  }

  const toggleAc = (room) => {
    room.acStatus = !room.acStatus
    showToast(room.acStatus ? `Phòng ${room.number}: Đã bật điều hòa thông minh.` : `Phòng ${room.number}: Đã tắt điều hòa.`, 'info')
  }

  const adjustTemp = (room, delta) => {
    if (!room.acStatus) return
    room.temperature = Math.max(16, Math.min(30, room.temperature + delta))
  }

  const setLightMode = (room, mode) => {
    room.lightMode = mode
    showToast(`Phòng ${room.number}: Đèn chuyển sang chế độ ${mode}.`, 'success')
  }

  const toggleDnd = (room) => {
    room.dndStatus = !room.dndStatus
    if (room.dndStatus) {
      room.cleanRequested = false
      const hk = housekeeping.value.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Đang kiểm tra'
        hk.note = 'Khách bật Không làm phiền (DND). Hãy hoãn dọn dẹp.'
      }
    }
    showToast(room.dndStatus ? `Phòng ${room.number}: Đã bật Không làm phiền (DND).` : `Phòng ${room.number}: Đã tắt chế độ Không làm phiền.`, 'info')
  }

  const toggleCleanRequest = (room) => {
    room.cleanRequested = !room.cleanRequested
    if (room.cleanRequested) {
      room.dndStatus = false
      const hk = housekeeping.value.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Chờ dọn'
        hk.note = 'Khách gửi yêu cầu dọn phòng khẩn cấp từ bảng điều khiển IoT.'
      } else {
        housekeeping.value.unshift({
          id: 'HK' + String(Date.now()).slice(-5),
          roomNumber: String(room.number),
          staffName: 'Chưa phân công',
          status: 'Chờ dọn',
          lastCleaned: 'Vừa yêu cầu',
          note: 'Khách gửi yêu cầu dọn phòng khẩn cấp từ bảng điều khiển IoT.'
        })
      }
      showToast(`Đã chuyển yêu cầu dọn phòng ${room.number} đến bộ phận Buồng phòng.`, 'success')
    } else {
      const hk = housekeeping.value.find(h => h.roomNumber === String(room.number))
      if (hk) {
        hk.status = 'Sạch sẽ'
        hk.note = 'Yêu cầu dọn dẹp đã được hủy từ phía phòng.'
      }
      showToast(`Đã hủy yêu cầu dọn phòng ${room.number}.`, 'info')
    }
  }

  const fetchRooms = async () => {
    try {
      const res = await apiClient.get('/rooms')
      rooms.value = res.data
    } catch (e) {
      console.warn('API /rooms offline, using local mock data.', e)
    }
  }

  const fetchCustomers = async () => {
    try {
      const res = await apiClient.get('/customers')
      customers.value = res.data
    } catch (e) {
      console.warn('API /customers offline, using local mock data.', e)
    }
  }

  const fetchBookings = async () => {
    try {
      const res = await apiClient.get('/bookings')
      bookings.value = res.data
    } catch (e) {
      console.warn('API /bookings offline, using local mock data.', e)
    }
  }

  const fetchInvoices = async () => {
    try {
      const res = await apiClient.get('/invoices')
      invoices.value = res.data
    } catch (e) {
      console.warn('API /invoices offline, using local mock data.', e)
    }
  }

  const fetchServices = async () => {
    try {
      const res = await apiClient.get('/services')
      services.value = res.data
    } catch (e) {
      console.warn('API /services offline, using local mock data.', e)
    }
  }

  const fetchHousekeeping = async () => {
    try {
      const res = await apiClient.get('/housekeeping')
      housekeeping.value = res.data
    } catch (e) {
      console.warn('API /housekeeping offline, using local mock data.', e)
    }
  }

  const fetchPromotions = async () => {
    try {
      const res = await apiClient.get('/promotions')
      promotions.value = res.data
    } catch (e) {
      console.warn('API /promotions offline, using local mock data.', e)
    }
  }

  const fetchStaff = async () => {
    try {
      const res = await apiClient.get('/staff')
      staff.value = res.data
    } catch (e) {
      console.warn('API /staff offline, using local mock data.', e)
    }
  }

  return {
    isDark, sidebarOpen, isMobile, loading, currentPage, toast, showToast,
    stats, roomStatus, rooms, customers, bookings, invoices, staff, reports, settingGroups, services, housekeeping, promotions,
    cartItems, selectedBookingId, voucherCode, activeDiscount, appliedVoucher,
    setPage, toggleDark, toggleSidebar, onResize,
    addToCart, updateCartQty, applyPromo, cartSubtotal, discountAmount, cartTax, cartTotal, submitPOSOrder,
    toggleDoorLock, toggleAc, adjustTemp, setLightMode, toggleDnd, toggleCleanRequest,
    fetchRooms, fetchCustomers, fetchBookings, fetchInvoices, fetchServices, fetchHousekeeping, fetchPromotions, fetchStaff
  }
})
