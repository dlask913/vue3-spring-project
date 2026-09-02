<template>
  <Navbar />
  <div>
    <router-view />
  </div>
  <Toast />
  <Footer />
</template>

<script setup>
import Navbar from '@/components/common/Navbar.vue'
import Toast from '@/components/common/Toast.vue'
import Footer from '@/components/common/Footer.vue'

import { onMounted, onUnmounted } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let stompClient = null

onMounted(() => {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws-connection'),
    reconnectDelay: 5000,
    connectHeaders: { 'client-type': 'SERVICE' }
  })
  
  // 연결만 맺어두면 백엔드 SessionConnectedEvent가 자동으로 카운트함
  stompClient.activate()
})

onUnmounted(() => {
  if (stompClient) {
    stompClient.deactivate()
  }
})

</script>

<style></style>
