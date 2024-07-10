<template>
  <form class="d-flex" @submit.prevent="searchNotices">
    <div class="row w-100">
      <div class="col-3">
        <select class="form-select" v-model="searchOption">
          <option value="username">글쓴이</option>
          <option value="title">제목</option>
        </select>
      </div>
      <div class="col-7">
        <input class="form-control" type="search" placeholder="Search" aria-label="Search" v-model="searchValue">
      </div>
      <div class="col-2">
        <button class="btn btn-outline-success w-100" type="submit">검색하기</button>
      </div>
    </div>
  </form>
</template>
<script>
export default {
  props:{
    initSearchOption: {
      type: String,
      default: 'title',
    },
    initSearchValue: {
      type: String,
      default: '',
    },
  },
  emits:['search'],
  setup(props,{emits}){
    return {

    };
  }
  
}
</script>
<style>
  
</style>