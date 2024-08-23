import axios from '@/axios';

export function getNotices(option, value) {
  return axios.get(`/notices/all?${option}=${value}`);
}

export function getNoticesByPage(page, limit) {
  return axios.get(`/notices?page=${page}&limit=${limit}`);
}

export function getNoticesByKeyword(page, limit, option, value) {
  return axios.get(`/notices?page=${page}&limit=${limit}&${option}=${value}`);
}

export function getNoticeById(id) {
  return axios.get(`/notice/${id}`);
}

export function createNotice(token, data) {
  return axios.post('/notice', data, {
    headers: { Authorization: token },
  });
}

export function updateNotice(token, id, data) {
  return axios.put(`/notice/${id}`, data, {
    headers: { Authorization: token },
  });
}

export function deleteNotice(token, id) {
  return axios.delete(`/notice/${id}`, {
    headers: { Authorization: token },
  });
}

export function getNoticesByMember(token) {
  return axios.get(`/member/notices`, {
    headers: { Authorization: token },
  });
}
