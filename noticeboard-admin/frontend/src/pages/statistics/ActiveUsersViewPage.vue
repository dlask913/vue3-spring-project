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
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const activeUsers = ref(0);
const isConnected = ref(false);
let stompClient = null;

const connectWebSocket = () => {
  const socket = new SockJS('http://localhost:8080/ws-connect');
  stompClient = Stomp.over(socket);

  // 콘솔 로그가 너무 지저분하면 주석 해제하여 비활성화
  // stompClient.debug = null

  stompClient.connect(
    {},
    frame => {
      isConnected.value = true;
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/users', response => {
        activeUsers.value = parseInt(response.body, 10);
      });
    },
    error => {
      console.error('WebSocket error: ', error);
      isConnected.value = false;
      setTimeout(connectWebSocket, 5000);
    },
  );
};

const disconnectWebSocket = () => {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
  isConnected.value = false;
};

onMounted(() => {
  connectWebSocket();
});

onUnmounted(() => {
  disconnectWebSocket();
});
</script>

<style scoped>
/* 원하시면 커스텀 라운드나 스타일을 추가할 수 있습니다 */
.rounded-borders-xl {
  border-radius: 16px;
}
</style>
