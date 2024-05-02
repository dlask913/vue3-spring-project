<template>
  <Navbar />
  <div>
    <router-view />
  </div>
</template>

<script>
import Navbar from '@/components/common/Navbar.vue';
export default {
    components: {
        Navbar
    }
}
</script>

<style>

</style>