<template>
  <q-page class="flex flex-center text-center">
    <div>
      <q-icon name="warning" color="red-8" size="8em" />
      <div class="text-h4 text-red-8 q-mt-md">접근 권한 없음</div>
      <div class="text-subtitle1 q-mt-sm">
        이 페이지에 접근할 수 있는 권한이 없습니다.
      </div>
      <div class="text-subtitle2 q-mt-md">
        다른 메뉴를 이용하시거나, 관리자에게 문의해주세요.
      </div>

      <q-btn
        class="q-mt-xl"
        color="primary"
        text-color="white"
        unelevated
        to="/"
        label="홈으로 돌아가기"
        no-caps
      />
    </div>
  </q-page>
</template>
<script setup></script>
<style lang="scss" scoped>
.q-page {
  background-color: #f7f7f7; // 약간 회색 배경
}
</style>
