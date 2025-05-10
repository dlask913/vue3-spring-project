import axios from '@/axios'

export function sendMessage(data) {
  return axios.post('/message', data, {
    needsAuth: true,
  })
}

export function deleteMessage(id) {
  return axios.delete(`/message/${id}`, {
    needsAuth: true,
  })
}

export function getMessageById(id) {
  return axios.get(`/message/${id}`, {
    needsAuth: true,
  })
}

export function getReceivedMessagesByMemberId(memberId) {
  return axios.get(`/member/${memberId}/messages/received`, {
    needsAuth: true,
  })
}

export function getSentMessagesByMemberId(memberId) {
  return axios.get(`/member/${memberId}/messages/sent`, {
    needsAuth: true,
  })
}

export function updateReadStatus(memberId, otherId) {
  return axios.patch(`/message/${memberId}/${otherId}/read`, {
    needsAuth: true,
  })
}

export function getMessagesByRoomId(roomId) {
  return axios.get(`/room/${roomId}/messages`, {
    needsAuth: true,
  })
}

export function getRoomsByMemberId(memberId) {
  return axios.get(`/rooms/${memberId}`, {
    needsAuth: true,
  })
}
