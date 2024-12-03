<template>
  <Navbar />
  <div>
    <router-view />
  </div>
  <Toast />
  <Footer />
</template>

<script setup>
import Navbar from '@/components/common/Navbar.vue'
import Toast from '@/components/common/Toast.vue'
import Footer from '@/components/common/Footer.vue'
</script>

<style></style>
