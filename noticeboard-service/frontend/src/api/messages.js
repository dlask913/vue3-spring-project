import axios from '@/axios'

export function sendMessage(token, data) {
  return axios.post('/message', data, {
    headers: { Authorization: token },
  })
}

export function deleteMessage(token, id) {
  return axios.delete(`/message/${id}`, {
    headers: { Authorization: token },
  })
}

export function getMessageById(token, id) {
  return axios.get(`/message/${id}`, {
    headers: { Authorization: token },
  })
}

export function getReceivedMessagesByMemberId(token, memberId) {
  return axios.get(`/member/${memberId}/messages/received`, {
    headers: { Authorization: token },
  })
}

export function getSentMessagesByMemberId(token, memberId) {
  return axios.get(`/member/${memberId}/messages/sent`, {
    headers: { Authorization: token },
  })
}

export function updateReadStatus(token, memberId, otherId) {
  return axios.patch(`/message/${memberId}/${otherId}/read`, {
    headers: { Authorization: token },
  })
}

export function getMessagesByRoomId(token, roomId) {
  return axios.get(`/room/${roomId}/messages`, {
    headers: { Authorization: token },
  })
}

export function getRoomsByMemberId(token, memberId) {
  return axios.get(`/rooms/${memberId}`, {
    headers: { Authorization: token },
  })
}
