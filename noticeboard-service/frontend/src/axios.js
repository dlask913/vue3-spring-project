import axios from 'axios'
import router from '@/router'
import { useToastStore, useStorageStore } from '@/store/index'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
})

// 예외 처리를 위한 response interceptor
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
      }
    } else {
      toast.setToast('서버와 연결할 수 없습니다.', 'danger')
    }
    return Promise.reject(error)
  },
)

export default axiosInstance
