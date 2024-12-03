import axios from '@/axios'

export function loginMember(data) {
  return axios.post('/login', data)
}

export function getMemberById(token, id) {
  return axios.get(`/member/${id}`, {
    headers: { Authorization: token },
  })
}

export function createMember(data) {
  return axios.post('/member', data)
}

export function updateMember(token, id, memberDto, memberImg) {
  const formData = new FormData()
  formData.append(
    'memberDto',
    new Blob([JSON.stringify(memberDto)], { type: 'application/json' }),
  ) // JSON 데이터 추가
  if (memberImg.value) {
    formData.append('memberImg', memberImg.value) // 파일이 있는 경우 추가
  }

  return axios.patch(`/member/${id}`, formData, {
    headers: { Authorization: token, 'Content-Type': 'multipart/form-data' },
  })
}

export function deleteMember(token, id) {
  return axios.delete(`/member/${id}`, {
    headers: { Authorization: token },
  })
}
