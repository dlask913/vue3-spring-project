<template>
  <q-page class="flex flex-center bg-grey-1">
    <div
      class="column items-center q-gutter-y-lg"
      style="width: 100%; max-width: 400px"
    >
      <div
        class="text-h5 text-weight-bold text-primary flex items-center q-gutter-x-sm"
      >
        <q-icon name="analytics" size="md" />
        <span>실시간 서비스 현황</span>
      </div>

      <q-card
        class="my-card q-pa-md shadow-20 rounded-borders-xl full-width text-center"
      >
        <q-card-section>
          <div class="text-subtitle1 text-grey-7 text-weight-medium">
            현재 접속자 수
          </div>

          <div class="row justify-center items-center q-mt-md text-primary">
            <q-icon name="people" size="lg" class="q-mr-sm" />
            <div class="text-h2 text-weight-bolder">
              {{ activeUsers.toLocaleString() }}
            </div>
            <span class="text-h6 text-grey-8 q-ml-xs q-mt-md">명</span>
          </div>
        </q-card-section>
      </q-card>

      <q-chip
        :color="isConnected ? 'positive' : 'negative'"
        text-color="white"
        :icon="isConnected ? 'cloud_done' : 'cloud_off'"
        class="text-weight q-px-md shadow-2"
      >
        {{ isConnected ? '서버 연결됨' : '서버 연결 끊김' }}
      </q-chip>
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const activeUsers = ref(0)
const isConnected = ref(false);

// 현재 탭 전용 소켓 변수
let stompClient = null

onMounted(() => {
  // 탭이 열릴 때 소켓 클라이언트 설정
  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws-connection'),
    reconnectDelay: 500,
    connectHeaders: { 'client-type': 'ADMIN' },
    onConnect: () => {
      isConnected.value = true;
      stompClient.subscribe('/topic/active-users', (message) => {
        activeUsers.value = parseInt(message.body, 10)
      })
    },
  })

  stompClient.activate()
})

onUnmounted(() => {
  // 컴포넌트 해제 / 탭 종료 시 소켓 연결 끊기
  if (stompClient) {
    stompClient.deactivate()
  }
})
</script>

<style scoped>
.rounded-borders-xl {
  border-radius: 16px;
}
</style>
