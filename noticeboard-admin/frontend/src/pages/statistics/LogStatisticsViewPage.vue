<template>
  <VueApexCharts
    type="bar"
    height="400"
    :options="chartConfig"
    :series="series"
  />
</template>

<script setup>
import { ref, computed } from 'vue';
import VueApexCharts from 'vue3-apexcharts';

// LogStatisticsResponseDto 기반 데이터 예시
const logData = ref([
  { uri: '/api/login', method: 'POST', logDate: '2023-12-07', count: 150 },
  { uri: '/api/users', method: 'GET', logDate: '2023-12-08', count: 200 },
  { uri: '/api/notice', method: 'POST', logDate: '2023-12-09', count: 330 },
  { uri: '/api/login', method: 'POST', logDate: '2023-12-10', count: 300 },
  { uri: '/api/users', method: 'GET', logDate: '2023-12-11', count: 320 },
]);

const series = ref([
  {
    name: 'Log Count',
    data: logData.value.map(item => ({
      x: item.logDate,
      y: item.count,
    })),
  },
]);

const chartConfig = computed(() => ({
  chart: { parentHeightOffset: 0, toolbar: { show: false } },
  grid: {
    padding: { top: -10 },
    xaxis: { lines: { show: true } },
  },
  xaxis: {
    type: 'datetime',
    axisBorder: { show: false },
    labels: { style: { fontSize: '0.8125rem' } },
  },
  yaxis: {
    tooltip: { enabled: true },
    labels: { style: { fontSize: '0.8125rem' } },
  },
}));
</script>
