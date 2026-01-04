<template>
  <q-page padding>
    <div class="row q-col-gutter-md items-center q-mb-md">
      <div class="col-12 col-md-6">
        <div class="text-h5 text-weight-bold">재고 등록 관리</div>
      </div>
      <div class="col-12 col-md-6 text-right">
        <q-file
          v-model="excelFile"
          label="엑셀 파일 업로드 (.xlsx)"
          outlined
          dense
          accept=".xlsx, .xls"
          class="inline-block"
          style="width: 300px"
          @update:model-value="handleFileUpload"
        >
          <template v-slot:prepend>
            <q-icon name="upload_file" />
          </template>
        </q-file>

        <q-btn
          label="양식 다운로드"
          flat
          color="primary"
          icon="download"
          class="q-ml-sm"
        />
      </div>
    </div>

    <q-table
      title="등록 대기 목록"
      :rows="inventories"
      :columns="columns"
      row-key="code"
      flat
      bordered
      :pagination="pagination"
    >
      <template v-slot:body-cell-price="props">
        <q-td :props="props"> {{ props.value.toLocaleString() }}원 </q-td>
      </template>

      <template v-slot:no-data>
        <div class="full-width row flex-center q-gutter-sm q-pa-lg text-grey-8">
          <q-icon size="2em" name="sentiment_dissatisfied" />
          <span>업로드된 데이터가 없습니다. 엑셀 파일을 선택해주세요.</span>
        </div>
      </template>
    </q-table>

    <div class="row justify-end q-mt-md">
      <q-btn
        label="초기화"
        color="grey-7"
        flat
        class="q-mr-sm"
        @click="rows = []"
      />
      <q-btn
        label="최종 저장하기"
        color="primary"
        icon="save"
        @click="saveInventory"
      />
    </div>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useQuasar } from 'quasar';
import { api } from 'boot/axios';

const $q = useQuasar();
const excelFile = ref(null);

// 컬럼 정의
const columns = [
  {
    name: 'productCode',
    label: '상품코드',
    field: 'productCode',
    align: 'left',
    sortable: true,
  },
  {
    name: 'productName',
    label: '상품명',
    field: 'productName',
    align: 'left',
    sortable: true,
  },
  {
    name: 'orderQty',
    label: '수량',
    field: 'orderQty',
    align: 'right',
    sortable: true,
  },
  {
    name: 'unitPrice',
    label: '단가',
    field: 'unitPrice',
    align: 'right',
    sortable: true,
  },
  {
    name: 'orderDate',
    label: '주문일자',
    field: 'orderDate',
    align: 'center',
    sortable: true,
  },
];

const inventories = ref([]);
const excelInventories = ref([]);

const pagination = ref({
  rowsPerPage: 10,
});

const fetchInventories = async () => {
  try {
    const response = await api.get('/inventories');
    inventories.value = response.data.map(inventory => ({
      ...inventory,
    }));
  } catch (error) {
    console.error('인벤토리 목록을 불러오는 중 오류 발생:', error);
  }
};

// 엑셀 업로드 핸들러 (로직 예시)
const handleFileUpload = async () => {
  if (!excelFile.value) return;

  const formData = new FormData();
  formData.append('inventoryFile', excelFile.value);

  try {
    const response = await api.post('/inventories/parser', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    // 백엔드에서 파싱한 리스트 결과 저장
    excelInventories.value = [...response.data];
    // 기존 데이터 + 임시 업로드 데이터 -> 최종 저장 버튼 클릭 후 저장
    inventories.value = [...inventories.value, ...excelInventories.value];

    $q.notify({
      color: 'positive',
      message: '파싱 완료! 리스트를 확인하세요.',
    });
  } catch (error) {
    console.error(error);
    $q.notify({ color: 'negative', message: '업로드 중 오류가 발생했습니다.' });
  }
};

const saveInventory = async () => {
  if (excelInventories.value.length === 0) {
    $q.notify({ color: 'negative', message: '저장할 데이터가 없습니다.' });
    return;
  }

  const formData = new FormData();
  formData.append('inventoryFile', excelFile.value);

  try {
    await api.post('/inventories/bulk', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    $q.notify({ color: 'positive', message: '저장이 완료되었습니다.' });
  } catch (error) {
    console.error('인벤토리 목록을 불러오는 중 오류 발생:', error);
  }
};

onMounted(() => {
  fetchInventories();
});
</script>
