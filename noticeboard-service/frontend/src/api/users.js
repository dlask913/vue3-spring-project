import axios from '@/axios'

export function loginMember(data) {
  return axios.post('/login', data)
}

export function getMemberById(id) {
  return axios.get(`/member/${id}`, {
    needsAuth: true,
  })
}

export function createMember(data) {
  return axios.post('/member', data)
}

export function updateMember(id, memberDto, memberImg) {
  const formData = new FormData()
  formData.append(
    'memberDto',
    new Blob([JSON.stringify(memberDto)], { type: 'application/json' }),
  ) // JSON 데이터 추가
  if (memberImg.value) {
    formData.append('memberImg', memberImg.value) // 파일이 있는 경우 추가
  }

  return axios.patch(`/member/${id}`, formData, {
    needsAuth: true,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteMember(id) {
  return axios.delete(`/member/${id}`, {
    needsAuth: true,
  })
}

// 회원 주소 검색
export function searchAddressByKakao(query, page, limit) {
  return axios.get(`/map?query=${query}&page=${page}&limit=${limit}`)
}
