<template>
  <q-form @submit="onLogin" class="q-gutter-md">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-lg"><b>로그인</b></h2>

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
import { Notify } from 'quasar';

const form = ref({
  userId: '',
  password: '',
});

const router = useRouter();

const onLogin = async () => {
  try {
    const request = {
      ...form.value,
    };

    const { data } = await api.post('/login', request);
    router.push({
      path: '/auth/email-verify',
      query: { email: data.email, username: data.username },
    }); // 이메일 인증 페이지로 이동

  } catch (error) {
    let message = '로그인에 실패했습니다. 잠시 후 다시 시도해주세요.';
    if(error.response.data.errorCode === 'PASSWORD_MISMATCH') {
      message = '비밀번호가 일치하지 않습니다.';
    }else if(error.response.data.errorCode === 'MEMBER_NOT_FOUND') {
      message = '사용자를 찾을 수 없습니다. 회원가입 후 로그인해주세요.';
    }else if(error.response.data.errorCode === 'ACCOUNT_LOCKED'){
      message = '계정이 잠겨 있습니다. 관리자에게 문의해주세요.';
    } else {
      console.error('로그인 중 오류 발생:', error.response.data.errorCode);
    }

    Notify.create({
      message: message,
      color: 'negative',
      position: 'top',
    });
  }
};
</script>

<style lang="scss" scoped></style>
