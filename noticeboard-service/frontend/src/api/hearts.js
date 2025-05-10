import axios from '@/axios'

export function getHeartStatus(memberId, commentId) {
  return axios.get(`/heart`, {
    params: {
      memberId: memberId,
      commentId: commentId,
    },
    needsAuth: true,
  })
}

export function saveHeart(data) {
  return axios.post(`/heart`, data, {
    needsAuth: true,
  })
}

export function removeHeart(id) {
  return axios.delete(`/heart/${id}`, {
    needsAuth: true,
  })
}
