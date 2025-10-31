<template>
  <q-form @submit.prevent="onSubmit" class="q-gutter-md">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-lg">
        <b>인증 코드를 입력해주세요.</b>
      </h2>
      <div class="q-mx-auto" style="max-width: 400px">
        <q-input
          v-model="code"
          label="인증 코드"
          outlined
          maxlength="6"
          autofocus
          :rules="[val => !!val || '인증 코드를 입력하세요']"
        >
          <template #append>
            <q-icon name="key" />
          </template>
        </q-input>

        <q-btn
          label="확인"
          color="primary"
          unelevated
          class="full-width"
          type="submit"
          :loading="loading"
        />

        <div class="text-center q-mt-sm">
          <q-btn
            flat
            label="인증 코드 다시 보내기"
            color="secondary"
            @click="resendCode"
            :disable="resending"
          />
        </div>
      </div>
    </div>
  </q-form>
</template>

<script setup>
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useRoute } from 'vue-router';
import { useUserStore } from 'stores/user';
import { Notify } from 'quasar';
import { useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const email = route.query.email;
const username = route.query.username;
const userStore = useUserStore();
const code = ref('');
const loading = ref(false);
const resending = ref(false);

const onSubmit = async () => {
  if (!code.value) return;
  loading.value = true;
  try {
    const { data } = await api.post('/verify-code', {
      email: email,
      username: username,
      authenticationCode: code.value,
    });

    Notify.create({
      type: 'positive',
      message: '로그인 성공!',
      position: 'top',
      timeout: 2000,
    });
    userStore.setAuthInfo(
      data.memberId,
      data.username,
      data.accessToken,
      data.refreshToken,
    ); // 토큰 저장

    router.push({ path: '/' }); // 메인 페이지로 이동
  } catch (err) {
    console.log(err);
    Notify.create({
      type: 'negative',
      message: '인증에 실패했습니다. 다시 시도하세요.',
    });
  } finally {
    loading.value = false;
  }
};

const resendCode = async () => {
  resending.value = true;
  try {
    await api.post('/resend-code', {
      email: email,
      username: username,
    });
    Notify.create({
      type: 'info',
      message: '인증 코드가 이메일로 전송되었습니다.',
    });
  } catch (err) {
    console.log(err);
    Notify.create({
      type: 'negative',
      message: '코드 전송 중 오류가 발생했습니다.',
    });
  } finally {
    resending.value = false;
  }
};
</script>

<style scoped>
.full-width {
  width: 100%;
}
</style>
