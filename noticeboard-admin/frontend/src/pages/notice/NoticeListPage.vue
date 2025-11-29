<template>
  <div class="q-gutter-md q-mx-auto" style="max-width: 80%">
    <div class="q-pa-md q-mt-xl">
      <h2 class="text-h5 text-center q-mb-md"><b>게시글 목록</b></h2>
      <q-table
        :rows="notices"
        :columns="columns"
        row-key="id"
        :loading="loading"
        no-data-label="게시글이 없습니다."
        @row-click="onRowClick"
        separator="cell"
        class="rounded-borders"
      >
        <template v-slot:body-cell-hidden="props">
          <q-td :props="props">
            <q-checkbox v-model="props.row.hidden" disable />
          </q-td>
        </template>

        <template v-slot:loading>
          <q-inner-loading showing color="teal" />
        </template>
      </q-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'boot/axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const columns = [
  {
    name: 'id',
    required: true,
    label: '순서',
    align: 'center',
    field: 'index',
    format: val => `${val}`,
    sortable: true,
    style: 'width: 10%;',
  },
  {
    name: 'title',
    align: 'center',
    label: '제목',
    field: 'title',
    sortable: true,
    style: 'font-weight: bold; font-size: 1.1em; width: 50%;',
  },
  {
    name: 'hidden',
    align: 'center',
    label: '숨김여부',
    field: 'hide_yn',
    sortable: true,
    style: 'width: 10%;',
  },
  {
    name: 'createdAt',
    align: 'center',
    label: '작성일',
    field: 'createdAt',
    sortable: true,
    format: val => new Date(val).toLocaleDateString('ko-KR'),
    style: 'width: 10%;',
  },
];

const notices = ref([]);
const loading = ref(true);

const fetchNotices = async () => {
  try {
    loading.value = true;
    const response = await api.get('/notices');
    notices.value = response.data.map((notice, index) => ({
      ...notice,
      index: index + 1,
      hidden: notice.hide_yn === 'N' || false,
      createdAt: notice.postDate || new Date().toISOString(),
    }));
    console.log(notices.value);
  } catch (error) {
    console.error('게시글 목록을 불러오는 중 오류 발생:', error);
  } finally {
    loading.value = false;
  }
};

const onRowClick = (evt, row) => {
  router.push(`/notice/${row.id}`);
};

onMounted(() => {
  fetchNotices();
});
</script>

<style lang="scss" scoped></style>
