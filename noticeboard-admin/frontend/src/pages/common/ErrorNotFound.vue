<template>
  <div class="flex flex-center text-center">
    <div>
      <q-icon name="warning" color="red-8" size="8em" />
      <div class="text-h4 text-red-8 q-mt-md">404 페이지 없음</div>
      <div class="text-subtitle1 q-mt-sm">존재하지 않는 페이지입니다.</div>
      <div class="text-subtitle2 q-mt-md">
        경로를 다시 확인하시거나, 다른 메뉴를 이용해주세요.
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
  </div>
</template>
<script setup></script>
<style lang="scss" scoped>
.q-page {
  background-color: #f7f7f7; // 약간 회색 배경
}
</style>
