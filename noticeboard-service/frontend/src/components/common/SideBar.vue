<template>
  <div
    class="d-flex flex-column flex-shrink-0 p-3 mt-5 ms-5"
    style="width: 280px"
  >
    <div
      class="d-flex align-items-center mb-3 mb-md-0 me-md-auto link-dark text-decoration-none"
    >
      <span class="fs-4">MyPage</span>
    </div>
    <hr />
    <ul class="nav nav-pills flex-column mb-auto">
      <li v-for="menu in menus" :key="menu.id" class="nav-item">
        <a
          href="#"
          class="nav-link"
          :class="{
            'link-dark': selectedMenu !== menu.id,
            link: selectedMenu === menu.id,
            active: selectedMenu === menu.id,
          }"
          @click.prevent="$emit('update:selectedMenu', menu.id)"
        >
          {{ menu.label }}
        </a>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
defineProps({
  menus: {
    type: Array,
    required: true,
  },
  selectedMenu: {
    type: String,
    required: true,
  },
})
</script>

<style scoped></style>
