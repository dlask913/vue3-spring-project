import axios from 'axios'
import router from '@/router'
import { useToastStore, useStorageStore } from '@/store/index'

// Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // 요청 타임아웃 설정
})

// 인증 처리를 위한 요청 인터셉터
axiosInstance.interceptors.request.use(
  config => {
    const storage = useStorageStore()
    if (config.needsAuth && storage.getToken) {
      config.headers.Authorization = `Bearer ${storage.getToken}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  },
)

// 예외 처리를 위한 응답 인터셉터
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    const storage = useStorageStore()
    const toast = useToastStore()
    if (error.response) {
      const { status, data } = error.response

      if (status === 401) {
        // 토큰 만료
        toast.setToast('세션이 만료되었습니다. 다시 로그인해주세요.', 'danger')
        storage.logout()
        router.push('/')
      } else if (status === 400) {
        // 유효성 검증 실패
        toast.setToast(data.message, 'danger')
      } else if (status === 403) {
        // 권한 없음
        toast.setToast('접근 권한이 없습니다.', 'danger')
      } else {
        // 기타 서버 에러
        toast.setToast(`에러 발생: ${data.message}`, 'danger')
      }
    } else if (error.request) {
      // 요청이 전송되었으나 응답이 없음
      toast.setToast(
        '서버로부터 응답이 없습니다. 잠시 후 다시 시도해주세요.',
        'danger',
      )
    } else {
      // 기타 에러
      toast.setToast(`에러 발생: ${error.message}`, 'danger')
    }
    return Promise.reject(error)
  },
)

export default axiosInstance
