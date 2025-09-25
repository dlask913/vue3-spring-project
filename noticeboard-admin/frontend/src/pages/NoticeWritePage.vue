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
      <div class="row items-center q-col-gutter-md q-mb-md">
        <!-- 내용 모드 라벨 -->
        <div class="col-auto">
          <div class="text-subtitle2">내용 모드</div>
        </div>

        <!-- 옵션 그룹 -->
        <div class="col">
          <q-option-group
            v-model="form.contentType"
            :options="[
              { label: '일반 텍스트', value: 'text' },
              { label: 'HTML (에디터)', value: 'html' },
            ]"
            inline
            color="primary"
          />
        </div>

        <!-- (예: HTML 모드일 때만 미리보기 버튼) -->
        <div class="col-auto" v-if="form.contentType === 'html'">
          <q-btn
            label="미리보기"
            color="secondary"
            @click="previewDialog = true"
          />
        </div>
      </div>

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
      <q-file
        v-model="form.file"
        label="첨부파일"
        accept=".jpg,.png,.pdf,.docx,.txt"
        stack-label
        outlined
        clearable
        class="q-mb-md"
      />

      <div class="row justify-center q-mt-md">
        <q-btn type="submit" label="저장" color="primary" class="q-px-xl" />
      </div>
    </div>

    <!-- HTML 미리보기 다이얼로그 -->
    <q-dialog v-model="previewDialog" persistent>
      <q-card style="min-width: 70vw; max-width: 90vw">
        <q-card-section>
          <div class="text-h6">HTML 미리보기</div>
        </q-card-section>

        <q-separator />

        <q-card-section>
          <!-- HTML 렌더링 -->
          <div v-html="form.content"></div>
        </q-card-section>

        <q-separator />

        <q-card-actions align="right">
          <q-btn flat label="닫기" color="primary" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-form>
</template>

<script setup>
import { ref } from 'vue';
import { api } from 'boot/axios';
import { useUserStore } from 'stores/user';
import { Notify } from 'quasar';

const userStore = useUserStore();
const previewDialog = ref(false);

const form = ref({
  title: '',
  content: '',
  file: null,
});

const onSaveNotice = async () => {
  try {
    const formData = new FormData();
    formData.append('memberId', userStore.userId);
    formData.append('title', form.value.title);
    formData.append('content', form.value.content);

    if (form.value.file) {
      formData.append('file', form.value.file);
    }

    await api.post('/notice', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    Notify.create({
      message: '게시글이 성공적으로 저장되었습니다!',
      color: 'positive',
      position: 'top',
      timeout: 2000,
    });

    // 초기화
    form.value = { title: '', content: '', file: null };
  } catch (error) {
    console.error(error);
    Notify.create({
      message: '저장 중 오류가 발생했습니다.',
      color: 'negative',
      position: 'top',
    });
  }
};
</script>

<style lang="scss" scoped></style>
