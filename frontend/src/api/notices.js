import axios from 'axios';

export function getNotices() {
  return axios.get('http://localhost:8080/notices/all');
}

export function getNoticeById(id) {
  return axios.get(`http://localhost:8080/notice/${id}`);
}

export function createNotice(token, data) {
  return axios.post('http://localhost:8080/notice', data,
    {
      headers: {'Authorization': token }
    });
}

export function updateNotice(token, id, data) {
  return axios.put(`http://localhost:8080/notice/${id}`, data,
    {
      headers: {'Authorization': token }
    });
}

export function deleteNotice(token, id) {
  return axios.delete(`http://localhost:8080/notice/${id}`,
    {
      headers: {'Authorization': token }
    });
}
