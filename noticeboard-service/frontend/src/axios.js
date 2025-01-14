import axios from 'axios'
import { useToastStore } from '@/store/index'

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
})

// 예외 처리를 위한 response interceptor
axiosInstance.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 400 && data.message === '이미 사용중인 이메일입니다.') {
        useToastStore().setToast(data.message, 'danger')
      } else if (
        status === 400 &&
        data.message === '비밀번호를 확인해주세요.'
      ) {
        useToastStore().setToast(data.message, 'danger')
      }
    } else {
      useToastStore().setToast('서버와 연결할 수 없습니다.', 'danger')
    }
    return Promise.reject(error)
  },
)

export default axiosInstance
