<template>
  <q-page padding>
    <div class="row q-col-gutter-md items-center q-mb-md">
      <div class="col-12 col-md-4">
        <div class="text-h5 text-weight-bold">재고 등록 관리</div>
      </div>
      <div class="col-12 col-md-8 text-right">
        <q-input
          v-model="searchQuery"
          label="상품 검색 (이름 또는 주문번호)"
          outlined
          dense
          class="q-mb-md"
          clearable
        >
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>
    </div>
    <q-list separator class="bg-white">
      <div v-for="product in filteredProducts" :key="product.id">
        <q-item
          clickable
          v-ripple
          @click="toggleProduct(product.id)"
          class="q-py-md"
        >
          <q-item-section avatar>
            <q-icon name="shopping_bag" color="grey-7" />
          </q-item-section>

          <q-item-section>
            <q-item-label class="text-weight-bold">{{
              product.name
            }}</q-item-label>
            <q-item-label caption>{{ product.orderNo }}</q-item-label>
          </q-item-section>

          <q-item-section side>
            <q-icon
              :name="
                selectedProductId === product.id ? 'expand_less' : 'expand_more'
              "
              size="sm"
            />
          </q-item-section>
        </q-item>

        <q-slide-transition>
          <div
            v-if="selectedProductId === product.id"
            class="bg-grey-1 q-pb-lg"
          >
            <q-stepper
              :model-value="getCurrentStep(product.status)"
              color="primary"
              animated
              vertical
              flat
              class="bg-transparent"
            >
              <q-step
                v-for="(step, index) in stepConfig"
                :key="index"
                :name="index + 1"
                :title="step.title"
                :icon="step.icon"
                :done="isDone(product.status, step.status)"
              >
                <p class="text-body2 text-grey-8">{{ step.desc }}</p>

                <q-btn
                  v-if="product.status === step.status && step.btnLabel"
                  :label="step.btnLabel"
                  color="primary"
                  unelevated
                  @click="handleEvent(step.event, product.id)"
                />
              </q-step>
            </q-stepper>
          </div>
        </q-slide-transition>

        <q-separator inset v-if="index !== filteredProducts.length - 1" />
      </div>
    </q-list>
  </q-page>
</template>

<script setup>
import { ref, computed } from 'vue';

// --- 1. 8단계 설정 (이전의 4단계 스타일을 8개로 확장) ---
const stepConfig = [
  {
    title: '주문 생성',
    status: 'CREATED',
    icon: 'add_shopping_cart',
    desc: '주문이 생성되었습니다. 결제를 진행해주세요.',
    btnLabel: '결제하기',
    event: 'PAY',
  },
  {
    title: '결제 완료',
    status: 'PAID',
    icon: 'payment',
    desc: '결제가 완료되었습니다. 배송을 준비 중입니다.',
  },
  {
    title: '상품 준비',
    status: 'PROCESSING',
    icon: 'inventory_2',
    desc: '상품 검수 및 포장 단계입니다.',
  },
  {
    title: '배송 시작',
    status: 'SHIPPED_START',
    icon: 'local_shipping',
    desc: '상품이 택배사로 전달되었습니다.',
  },
  {
    title: '배송 중',
    status: 'SHIPPING',
    icon: 'directions_truck',
    desc: '상품이 고객님께 이동 중입니다.',
  },
  {
    title: '배송 완료',
    status: 'DELIVERED',
    icon: 'check_circle',
    desc: '상품 배송이 완료되었습니다. 이용해주셔서 감사합니다.',
  },
  {
    title: '구매 확정',
    status: 'CONFIRMED',
    icon: 'thumb_up',
    desc: '구매가 최종 확정되었습니다.',
  },
  {
    title: '리뷰 작성',
    status: 'REVIEWED',
    icon: 'rate_review',
    desc: '리뷰를 남겨주시면 포인트를 드립니다.',
    btnLabel: '리뷰쓰기',
    event: 'WRITE',
  },
];

// --- 2. 가상 데이터 ---
const searchQuery = ref('');
const selectedProductId = ref(null);
const products = ref([
  { id: 1, name: '삼성 갤럭시 S24', orderNo: 'ORD-001', status: 'PAID' },
  { id: 2, name: '맥북 에어 M3', orderNo: 'ORD-002', status: 'SHIPPING' },
  { id: 3, name: '소니 헤드셋', orderNo: 'ORD-003', status: 'CREATED' },
]);

// --- 3. 로직 (기존 computed/함수 구조 유지) ---
const filteredProducts = computed(() => {
  return products.value.filter(
    p =>
      p.name.includes(searchQuery.value) ||
      p.orderNo.includes(searchQuery.value),
  );
});

const toggleProduct = id => {
  selectedProductId.value = selectedProductId.value === id ? null : id;
};

// 현재 몇 번째 단계인지 반환
const getCurrentStep = status => {
  const idx = stepConfig.findIndex(s => s.status === status);
  return idx + 1;
};

// 기존 isDone 로직 그대로 활용 (인덱스 비교 방식)
const isDone = (currentStatus, stepStatus) => {
  const statusOrder = stepConfig.map(s => s.status);
  return statusOrder.indexOf(stepStatus) < statusOrder.indexOf(currentStatus);
};

const handleEvent = (event, productId) => {
  console.log(`이벤트 ${event} 전송 - 상품 ID: ${productId}`);
  alert(`${event} 처리가 시작되었습니다.`);
};
</script>
