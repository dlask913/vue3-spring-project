import axios from '@/axios'

export function getCommentsByNoticeId(noticeId) {
  return axios.get(`/comment/${noticeId}`)
}

export function createComment(data) {
  return axios.post('/comment', data, {
    needsAuth: true,
  })
}

export function updateComment(id, data) {
  return axios.put(`/comment/${id}`, data, {
    needsAuth: true,
  })
}

export function deleteComment(id) {
  return axios.delete(`/comment/${id}`, {
    needsAuth: true,
  })
}

export function getCommentsByMember() {
  return axios.get(`/member/comments`, {
    needsAuth: true,
  })
}
