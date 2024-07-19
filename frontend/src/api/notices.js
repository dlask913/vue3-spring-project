import axios from 'axios';

export function getNotices(option, value) {
  return axios.get(`http://localhost:8080/notices/all?${option}=${value}`);
}

export function getNoticesByPage(page, limit) {
  return axios.get(`http://localhost:8080/notices?page=${page}&limit=${limit}`);
}

export function getNoticesByKeyword(page, limit, option, value) {
  return axios.get(
    `http://localhost:8080/notices?page=${page}&limit=${limit}&${option}=${value}`
  );
}

export function getNoticeById(id) {
  return axios.get(`http://localhost:8080/notice/${id}`);
}

export function createNotice(token, data) {
  return axios.post('http://localhost:8080/notice', data, {
    headers: { Authorization: token },
  });
}

export function updateNotice(token, id, data) {
  return axios.put(`http://localhost:8080/notice/${id}`, data, {
    headers: { Authorization: token },
  });
}

export function deleteNotice(token, id) {
  return axios.delete(`http://localhost:8080/notice/${id}`, {
    headers: { Authorization: token },
  });
}
