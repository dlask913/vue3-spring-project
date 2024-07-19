import axios from 'axios';

export function getHeartStatus(token, memberId, commentId) {
  return axios.get(`http://localhost:8080/heart`, {
    params: {
      memberId: memberId,
      commentId: commentId,
    },

    headers: { Authorization: token },
  });
}

export function saveHeart(token, data) {
  return axios.post(`http://localhost:8080/heart`, data, {
    headers: { Authorization: token },
  });
}

export function removeHeart(token, id) {
  return axios.delete(`http://localhost:8080/heart/${id}`, {
    headers: { Authorization: token },
  });
}
