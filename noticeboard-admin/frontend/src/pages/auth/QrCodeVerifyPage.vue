<template>
  <q-page class="flex flex-center">
    <q-form @submit.prevent="onSubmit">
      <div style="width: 400px">
        <h2 class="text-h5 text-center q-mb-lg">
          <b>OTP 인증 코드를 입력해주세요.</b>
        </h2>
        <div class="text-center q-mt-md q-mb-lg">
          <q-icon name="lock" class="q-mr-sm" />
          Authenticator 앱에서 생성된
          <span class="text-negative">6자리 코드</span>를 입력해주세요.
        </div>
        <div class="q-mx-auto" style="max-width: 400px">
          <q-input
            v-model="code"
            label="OTP 코드"
            class="q-mb-sm"
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
              label="다른 방법으로 인증하기"
              color="secondary"
              @click="chooseOtherMethod"
            />
          </div>
        </div>
      </div>
    </q-form>
  </q-page>
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

const onSubmit = async () => {
  if (!code.value) return;
  loading.value = true;
  try {
    const { data } = await api.post('/qr-verify', {
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
      data.email,
      data.role,
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

const chooseOtherMethod = () => {
  router.push({ path: '/2fa/select', query: { email, username } });
};
</script>

<style scoped>
.full-width {
  width: 100%;
}
</style>
