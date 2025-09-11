<template>
  <q-form @submit="onSignUp" class="q-gutter-md">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-md"><b>회원가입</b></h2>

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
        <q-btn type="submit" label="회원가입" color="primary" class="q-px-xl" />
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
  username: '',
  password: '',
});

const router = useRouter();

const onSignUp = async () => {
  try {
    const data = {
      ...form.value,
    };

    await api.post('/member', data);
    Notify.create({
      message: '회원가입이 성공적으로 완료되었습니다!',
      color: 'positive',
      position: 'top',
      timeout: 2000,
    });

    router.push('/login');
  } catch (error) {
    console.error('회원가입 중 오류 발생:', error);
    Notify.create({
      message: '회원가입에 실패했습니다. 다시 시도해주세요.',
      color: 'negative',
      position: 'top',
    });
  }
};
</script>

<style lang="scss" scoped></style>
