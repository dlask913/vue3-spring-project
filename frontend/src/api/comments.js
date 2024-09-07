import axios from '@/axios';

export function getCommentsByNoticeId(noticeId) {
  return axios.get(`/comment/${noticeId}`);
}

export function createComment(token, data) {
  return axios.post('/comment', data, {
    headers: { Authorization: token },
  });
}

export function updateComment(token, id, data) {
  return axios.put(`/comment/${id}`, data, {
    headers: { Authorization: token },
  });
}

export function deleteComment(token, id) {
  return axios.delete(`/comment/${id}`, {
    headers: { Authorization: token },
  });
}

export function getCommentsByMember(token) {
  return axios.get(`/member/comments`, {
    headers: { Authorization: token },
  });
}
