<template>
  <q-page class="flex flex-center">
    <div style="width: 400px">
      <div class="text-h5 text-weight-bold q-mb-md text-center">
        인증 방법을 선택해 주세요.
      </div>
      <p class="text-subtitle1 text-grey-7 q-mb-md text-center">
        선호하는 인증 방식을 선택하여 다음 단계로 이동합니다.
      </p>
      <q-list bordered class="rounded-borders">
        <q-item clickable v-ripple @click="selectOption('email')">
          <q-item-section avatar>
            <q-icon color="primary" name="mail_outline" />
          </q-item-section>
          <q-item-section>
            <q-item-label>이메일 인증</q-item-label>
            <q-item-label caption>
              등록된 이메일 주소로 인증 번호를 받습니다.</q-item-label
            >
          </q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>

        <q-separator />

        <q-item clickable v-ripple @click="selectOption('qr_code')">
          <q-item-section avatar>
            <q-icon color="teal" name="qr_code_2" />
          </q-item-section>
          <q-item-section>
            <q-item-label>QR 코드 인증</q-item-label>
            <q-item-label caption>
              모바일 앱을 통해 QR 코드를 스캔합니다.
            </q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>
        <!--
        <q-separator />

        <q-item clickable v-ripple @click="selectOption('sms')">
          <q-item-section avatar>
            <q-icon color="deep-orange-8" name="sms" />
          </q-item-section>
          <q-item-section>
            <q-item-label>SMS (휴대폰) 인증</q-item-label>
            <q-item-label caption>
              등록된 휴대폰 번호로 인증 번호를 받습니다.
            </q-item-label>
          </q-item-section>
          <q-item-section side>
            <q-icon name="chevron_right" />
          </q-item-section>
        </q-item>
-->
      </q-list>

      <div class="q-mt-xl text-center">
        <q-btn flat color="grey" label="돌아가기" @click="goBack" />
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const email = route.query.email;
const username = route.query.username;

const selectOption = option => {
  // 선택된 옵션에 따라 해당 인증 페이지로 라우팅
  if (option === 'email') {
    router.push({ path: '/auth/email-verify', query: { email, username } });
  } else if (option === 'qr_code') {
    router.push({ path: '/auth/qr-verify', query: { email, username } });
  } else if (option === 'sms') {
    router.push({ path: '/auth/sms-verify' });
  }
};

const goBack = () => {
  // 이전 페이지로 돌아가기
  router.go(-1);
};
</script>

<style scoped>
/* 카드/리스트 전체에 약간의 그림자 및 중앙 정렬 */
.rounded-borders {
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}
.q-page {
  background-color: #f7f9fa;
}
</style>
