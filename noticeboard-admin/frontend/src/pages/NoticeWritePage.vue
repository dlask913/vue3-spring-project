<template>
  <q-form
    @submit="onSaveNotice"
    class="q-gutter-md q-mx-auto"
    style="max-width: 80%"
  >
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-md"><b>게시글 작성</b></h2>
      <q-input
        v-model="form.title"
        label="제목"
        placeholder="제목을 입력하세요"
        outlined
        required
        class="q-mb-md"
      />
      <q-input
        v-model="form.content"
        label="내용"
        placeholder="내용을 입력하세요"
        type="textarea"
        outlined
        rows="10"
        required
        class="q-mb-md"
      />
      <div class="row justify-center q-mt-md">
        <q-btn type="submit" label="저장" color="primary" class="q-px-xl" />
      </div>
    </div>
  </q-form>
</template>

<script setup>
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useUserStore } from 'stores/user';

const userStore = useUserStore();

const form = ref({
  title: '',
  content: '',
});

const onSaveNotice = async () => {
  try {
    const data = {
      memberId: userStore.userId,
      ...form.value,
    };
    await api.post('/notice', data);
  } catch (error) {
    console.error(error);
  }
};
</script>

<style lang="scss" scoped></style>
