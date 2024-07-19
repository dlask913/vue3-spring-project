<template>
  <Navbar />
  <div>
    <router-view />
  </div>
  <Toast />
  <Footer />
</template>

<script>
import Navbar from '@/components/common/Navbar.vue';
import Toast from '@/components/common/Toast.vue';
import Footer from '@/components/common/Footer.vue';
export default {
  components: {
    Navbar,
    Toast,
    Footer,
  },
};
</script>

<style></style>
