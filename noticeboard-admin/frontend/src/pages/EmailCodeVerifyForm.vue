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
import { useQuasar } from 'quasar';
import { useRoute } from 'vue-router';
const route = useRoute();
const email = route.query.email;

const $q = useQuasar();
const code = ref('');
const loading = ref(false);
const resending = ref(false);

const onSubmit = async () => {
  if (!code.value) return;
  loading.value = true;
  try {
    await api.post('/verify-code', {
      email: email,
      authenticationCode: code.value,
    });
    $q.notify({ type: 'positive', message: '인증이 완료되었습니다!' });
  } catch (err) {
    console.log(err);

    $q.notify({
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
    // TODO: 실제 코드 재발송 API 호출
    $q.notify({
      type: 'info',
      message: '인증 코드가 이메일로 전송되었습니다.',
    });
  } catch (err) {
    console.log(err);
    $q.notify({
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
