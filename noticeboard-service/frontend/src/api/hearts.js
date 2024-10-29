import axios from '@/axios';

export function getHeartStatus(token, memberId, commentId) {
  return axios.get(`/heart`, {
    params: {
      memberId: memberId,
      commentId: commentId,
    },
    headers: { Authorization: token },
  });
}

export function saveHeart(token, data) {
  return axios.post(`/heart`, data, {
    headers: { Authorization: token },
  });
}

export function removeHeart(token, id) {
  return axios.delete(`/heart/${id}`, {
    headers: { Authorization: token },
  });
}
