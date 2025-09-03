<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title> 관리자 페이지 </q-toolbar-title>

        <q-space />
        <!-- 오른쪽으로 밀어내기 -->
        <template v-if="!isLoggedIn">
          <q-btn
            flat
            dense
            class="q-mr-sm"
            label="회원가입"
            icon="how_to_reg"
            @click="$router.push('/signup')"
          />
          <q-btn
            flat
            dense
            class="q-mr-md"
            label="로그인"
            icon="login"
            @click="$router.push('/login')"
          />
        </template>

        <!-- 로그인 상태일 때만 로그아웃 버튼 -->
        <q-btn
          v-else
          flat
          dense
          round
          class="q-mr-md"
          label="로그아웃"
          icon="logout"
          @click="onLogout"
          aria-label="Logout"
        />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawerOpen" show-if-above bordered v-if="isLoggedIn">
      <q-list>
        <q-item-label header> Essential Links </q-item-label>

        <EssentialLink
          v-for="link in linksList"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { computed, ref } from 'vue';
import EssentialLink from 'components/EssentialLink.vue';
import { useUserStore } from 'stores/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

const linksList = [
  {
    title: '공지사항',
    icon: 'notifications',
    to: '/notice',
  },
];

// 로그아웃 수행
const onLogout = () => {
  userStore.clearAuthInfo();
  router.push('/login');
};

// 로그인 여부
const isLoggedIn = computed(() => userStore.isLoggedIn);

const leftDrawerOpen = ref(false);

function toggleLeftDrawer() {
  leftDrawerOpen.value = !leftDrawerOpen.value;
}
</script>
