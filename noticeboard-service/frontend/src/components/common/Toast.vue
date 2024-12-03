<template>
  <div
    class="alert toast-box"
    :class="`alert-${toast.toastAlertType}`"
    role="alert"
  >
    {{ toast.toastMessage }}
  </div>
</template>

<script setup>
import { useToastStore } from '@/store/index'

const toast = useToastStore()
</script>

<style scoped>
.toast-box {
  position: fixed;
  top: 10px;
  right: 10px;
}
</style>
