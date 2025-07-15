import axios from 'axios'

// Axios 인스턴스 생성
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000, // 요청 타임아웃 설정
})

export default axiosInstance
