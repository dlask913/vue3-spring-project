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
      :rows="rows"
      :columns="columns"
      row-key="code"
      flat
      bordered
      :pagination="pagination"
    >
      <template v-slot:body-cell-price="props">
        <q-td :props="props">
          {{ props.value.toLocaleString() }}원
        </q-td>
      </template>

      <template v-slot:no-data>
        <div class="full-width row flex-center q-gutter-sm q-pa-lg text-grey-8">
          <q-icon size="2em" name="sentiment_dissatisfied" />
          <span>업로드된 데이터가 없습니다. 엑셀 파일을 선택해주세요.</span>
        </div>
      </template>
    </q-table>

    <div class="row justify-end q-mt-md">
      <q-btn label="초기화" color="grey-7" flat class="q-mr-sm" @click="rows = []" />
      <q-btn label="최종 저장하기" color="primary" icon="save" @click="saveInventory" />
    </div>
  </q-page>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()
const excelFile = ref(null)

// 1. 컬럼 정의
const columns = [
  { name: 'productCode', label: '상품코드', field: 'productCode', align: 'left', sortable: true },
  { name: 'productName', label: '상품명', field: 'productName', align: 'left', sortable: true },
  { name: 'orderQty', label: '수량', field: 'orderQty', align: 'right', sortable: true },
  { name: 'unitPrice', label: '단가', field: 'unitPrice', align: 'right', sortable: true },
  { name: 'orderDate', label: '주문일자', field: 'orderDate', align: 'center', sortable: true },
]

// 2. 데이터 (초기값은 빈 배열)
const rows = ref([])

const pagination = ref({
  rowsPerPage: 10
})

// 3. 엑셀 업로드 핸들러 (로직 예시)
const handleFileUpload = (file) => {
  if (!file) return

  // 여기서는 UI 확인을 위한 Mock 데이터
  setTimeout(() => {
    rows.value = [
      { productCode: 'P001', productName: '갤럭시 S24', orderQty: 50, unitPrice: 1200000, orderDate: '2024-05-20' },
      { productCode: 'P002', productName: '아이폰 15', orderQty: 30, unitPrice: 1350000, orderDate: '2024-05-21' },
    ]
    $q.loading.hide()
    $q.notify({ color: 'positive', message: '엑셀 데이터가 성공적으로 로드되었습니다.', icon: 'check' })
  }, 1000)
}

const saveInventory = () => {
  if (rows.value.length === 0) {
    $q.notify({ color: 'negative', message: '저장할 데이터가 없습니다.' })
    return
  }
  // API 저장 로직 실행
}
</script>
