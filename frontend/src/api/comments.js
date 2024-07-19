import axios from 'axios';

export function getCommentsByNoticeId(noticeId) {
  return axios.get(`http://localhost:8080/comment/${noticeId}`);
}

export function createComment(token, data) {
  return axios.post('http://localhost:8080/comment', data, {
    headers: { Authorization: token },
  });
}

export function updateComment(token, id, data) {
  return axios.put(`http://localhost:8080/comment/${id}`, data, {
    headers: { Authorization: token },
  });
}

export function deleteComment(token, id) {
  return axios.delete(`http://localhost:8080/comment/${id}`, {
    headers: { Authorization: token },
  });
}
