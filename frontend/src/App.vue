<template>
  <Navbar />
  <div>
    <router-view />
  </div>
  <Toast />
</template>

<script>
import Navbar from '@/components/common/Navbar.vue';
import Toast from '@/components/common/Toast.vue';
export default {
    components: {
        Navbar,
        Toast,
    }
}
</script>

<style>

</style>