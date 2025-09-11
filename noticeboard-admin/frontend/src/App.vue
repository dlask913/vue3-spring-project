<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue';
import { useUserStore } from 'stores/user';

const userStore = useUserStore();

onMounted(() => {
  // 새로고침 후에도 로그인 유지
  userStore.loadAuthInfo();
});
</script>
