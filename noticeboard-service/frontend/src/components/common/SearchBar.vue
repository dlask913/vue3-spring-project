<template>
  <form class="d-flex" @submit.prevent="handleSearch">
    <div class="row w-100">
      <div class="col-3">
        <select class="form-select" v-model="selectedOption">
          <option
            v-for="option in searchOptions"
            :key="option.key"
            :value="option.key"
          >
            {{ option.value }}
          </option>
        </select>
      </div>
      <div class="col-7">
        <input
          class="form-control"
          type="search"
          placeholder="Search"
          aria-label="Search"
          v-model="searchValue"
        />
      </div>
      <div class="col-2">
        <button class="btn btn-outline-success w-100" type="submit">
          검색하기
        </button>
      </div>
    </div>
  </form>
</template>
<script setup>
import { ref } from 'vue'

const emit = defineEmits(['handle-search'])
const props = defineProps({
  searchOptions: {
    type: Array,
    required: true,
  },
})

const searchValue = ref('')
const selectedOption = ref('title')

const handleSearch = () => {
  emit('handle-search', selectedOption.value, searchValue.value)
}
</script>
<style></style>
