import axios from 'axios';

export function loginMember(data) {
  return axios.post('http://localhost:8080/login', data);
}

export function getMemberById(id) {
  return axios.get(`http://localhost:8080/member/${id}`);
}

export function createMember(data) {
  return axios.post('http://localhost:8080/member', data);
}

export function updateMember(token, id, data) {
  return axios.put(`http://localhost:8080/member/${id}`, data,
    {
      headers: {'Authorization': token }
    });
}

export function deleteMember(token, id) {
  return axios.delete(`http://localhost:8080/member/${id}`,
    {
      headers: {'Authorization': token }
    });
}
