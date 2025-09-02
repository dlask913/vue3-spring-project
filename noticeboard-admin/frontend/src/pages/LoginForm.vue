<template>
  <q-form @submit="onLogin" class="q-gutter-md">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-md"><b>로그인</b></h2>

      <div class="q-mx-auto" style="max-width: 400px">
        <q-input
          v-model="form.username"
          label="아이디"
          placeholder="아이디를 입력하세요"
          outlined
          required
          class="q-mb-md"
          :rules="[val => !!val || '아이디를 입력해주세요']"
        />

        <q-input
          v-model="form.password"
          label="비밀번호"
          placeholder="비밀번호를 입력하세요"
          type="password"
          outlined
          required
          class="q-mb-md"
          :rules="[val => !!val || '비밀번호를 입력해주세요']"
        />
      </div>

      <div class="row justify-center q-mt-md">
        <q-btn type="submit" label="로그인" color="primary" class="q-px-xl" />
      </div>
    </div>
  </q-form>
</template>

<script setup>
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';
import { useUserStore } from 'stores/user';

const form = ref({
  userId: '',
  password: '',
});

const router = useRouter();
const userStore = useUserStore();

const onLogin = async () => {
  try {
    const request = {
      ...form.value,
    };

    const { data } = await api.post('/login', request);
    alert('로그인 성공!');

    userStore.setAuthInfo(data.memberId, data.accessToken, data.refreshToken); // 토큰 저장

    router.push('/');
  } catch (error) {
    console.error('로그인 중 오류 발생:', error);
    alert('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
  }
};
</script>

<style lang="scss" scoped></style>
