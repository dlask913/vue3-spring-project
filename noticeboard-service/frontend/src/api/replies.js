import axios from '@/axios'

export function createReply(data) {
  return axios.post('/reply', data, {
    needsAuth: true,
  })
}

export function updateReply(replyId, data) {
  return axios.put(`/reply/${replyId}`, data, {
    needsAuth: true,
  })
}

export function deleteReply(replyId) {
  return axios.delete(`/reply/${replyId}`, {
    needsAuth: true,
  })
}

export function getRepliesByComment(commentId) {
  return axios.get(`/replies/${commentId}`)
}
