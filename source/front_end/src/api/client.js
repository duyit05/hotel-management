import axios from 'axios'

// Khởi tạo instance axios với các cấu hình mặc định
const apiClient = axios.create({
  // Sử dụng biến môi trường hoặc fallback về localhost
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // Thời gian chờ tối đa 10 giây
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Bộ đánh chặn yêu cầu (Request Interceptor)
// Tự động đính kèm Token JWT vào Header nếu có trong LocalStorage
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Bộ đánh chặn phản hồi (Response Interceptor)
// Xử lý các lỗi HTTP toàn cục (ví dụ: Token hết hạn - 401 Unauthorized)
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        console.warn('Token không hợp lệ hoặc đã hết hạn. Đang đăng xuất...')
        localStorage.removeItem('token')
        // Có thể thực hiện chuyển hướng về trang đăng nhập nếu cần
        // window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default apiClient
