import axios from '@/axios'

export function createReply(token, data) {
  return axios.post('/reply', data, {
    headers: { Authorization: token },
  })
}

export function updateReply(token, replyId, data) {
  return axios.put(`/reply/${replyId}`, data, {
    headers: { Authorization: token },
  })
}

export function deleteReply(token, replyId) {
  return axios.delete(`/reply/${replyId}`, {
    headers: { Authorization: token },
  })
}

export function getRepliesByComment(commentId) {
  return axios.get(`/replies/${commentId}`)
}
