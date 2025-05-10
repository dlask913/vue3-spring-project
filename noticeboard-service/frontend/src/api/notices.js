import axios from '@/axios'

export function getNotices(option, value) {
  return axios.get(`/notices/all?${option}=${value}`)
}

export function getNoticesByPage(page, limit) {
  return axios.get(`/notices?page=${page}&limit=${limit}`)
}

export function getNoticesByKeyword(page, limit, option, value, sort, order) {
  return axios.get(
    `/notices?page=${page}&limit=${limit}&${option}=${value}&sort=${sort}&order=${order}`,
  )
}

export function getNoticeById(id) {
  return axios.get(`/notice/${id}`, {
    needsAuth: true,
  })
}

export function createNotice(data) {
  return axios.post('/notice', data, {
    needsAuth: true,
  })
}

export function updateNotice(id, data) {
  return axios.put(`/notice/${id}`, data, {
    needsAuth: true,
  })
}

export function deleteNotice(id) {
  return axios.delete(`/notice/${id}`, {
    needsAuth: true,
  })
}

export function getNoticesByMember() {
  return axios.get(`/member/notices`, {
    needsAuth: true,
  })
}
