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

export function updateMember(token, id, memberDto, memberImg) {
  const formData = new FormData();
  formData.append(
    'memberDto',
    new Blob([JSON.stringify(memberDto)], { type: 'application/json' })
  ); // JSON 데이터 추가
  if (memberImg.value) {
    formData.append('memberImg', memberImg.value); // 파일이 있는 경우 추가
  }

  return axios.patch(`http://localhost:8080/member/${id}`, formData, {
    headers: { Authorization: token, 'Content-Type': 'multipart/form-data' },
  });
}

export function deleteMember(token, id) {
  return axios.delete(`http://localhost:8080/member/${id}`, {
    headers: { Authorization: token },
  });
}
