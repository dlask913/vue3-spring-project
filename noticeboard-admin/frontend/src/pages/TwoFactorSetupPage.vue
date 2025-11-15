<template>
  <q-page padding>
    <div class="row q-ml-lg">
      <div class="col-xs-12 col-sm-10 col-md-8 col-lg-7">
        <h4 class="text-h5 q-mb-lg text-weight-bold">2차 인증 설정</h4>
        <div class="q-mb-md">
          <label class="text-subtitle1 text-weight-medium">이메일</label>
          <p class="text-caption text-grey-7 q-mt-xs">
            현재 등록되어있는 이메일로 인증 코드가 발송됩니다.
          </p>
          <q-input outlined dense class="q-mt-sm" style="max-width: 400px" />
        </div>

        <q-separator class="q-my-lg" />
        <div class="q-mb-md">
          <label class="text-subtitle1 text-weight-medium"
            >QR 코드 생성하기</label
          >
          <p class="text-caption text-grey-7 q-mt-xs">
            2차 인증을 위한 QR 코드를 생성합니다.
          </p>
          <q-btn
            flat
            color="primary"
            label="QR 코드 생성 및 등록 요청"
            @click="generateQr"
            :disable="!email"
            :loading="loading"
            class="full-width"
          />
          <div v-if="qrCode" class="q-mt-xl text-center">
            <h5 class="text-subtitle1 text-primary q-mb-md">
              <q-icon name="warning" color="negative" class="q-mr-sm" />
              Google Authenticator로 아래 QR 코드를 스캔하세요
              <q-icon name="warning" color="negative" class="q-ml-sm" />
            </h5>

            <img
              :src="qrCode"
              alt="QR Code"
              class="q-mt-md"
              style="border: 2px solid #ccc; padding: 10px; border-radius: 8px"
            />

            <p class="text-caption q-mt-md text-grey-7">
              스캔 후 Google Authenticator 앱에 표시되는 6자리 코드를 사용하여
              다음 단계(인증)를 진행해야 합니다.
            </p>
          </div>
        </div>
        <q-separator class="q-my-lg" />
        <q-inner-loading :showing="loading">
          <q-spinner-gears size="50px" color="primary" />
        </q-inner-loading>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { api } from 'boot/axios'; // Quasar 설정에 따라 api 경로는 다를 수 있습니다.
import { Notify } from 'quasar';

// State 정의
const email = ref('test@example.com');
const qrCode = ref('');
const loading = ref(false); // 로딩 상태 추가

const generateQr = async () => {
  try {
    const response = await api.post('/qr-register', null, {
      params: { email: email.value },
    });
    qrCode.value = response.data.qrCodeBase64;
    Notify.create({
      message: 'QR 코드가 성공적으로 생성되었습니다!',
      color: 'positive',
      position: 'top',
      timeout: 2000,
    });
  } catch (err) {
    console.error(err);
    Notify.create({
      message: 'QR 코드 생성 중 오류가 발생했습니다. 다시 시도해주세요.',
      color: 'negative',
      position: 'top',
    });
  }
};
</script>

<style lang="scss" scoped>
img {
  max-width: 300px;
  height: auto;
  display: block;
  margin-left: auto;
  margin-right: auto;
}
</style>
