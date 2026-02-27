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
        <template v-for="link in linksList" :key="link.title">
          <q-expansion-item
            v-if="link.children"
            :icon="link.icon"
            :label="link.title"
          >
            <q-list class="q-pl-lg">
              <EssentialLink
                v-for="subLink in link.children"
                :key="subLink.title"
                v-bind="subLink"
              />
            </q-list>
          </q-expansion-item>
          <EssentialLink v-else v-bind="link" />
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue';
import EssentialLink from 'components/EssentialLink.vue';
import { useUserStore } from 'stores/user';
import { useRouter } from 'vue-router';
import { messaging } from 'boot/firebase';
import { getToken, onMessage } from 'firebase/messaging';

const userStore = useUserStore();
const router = useRouter();

const linksList = [
  {
    title: '공지사항',
    icon: 'notifications',
    children: [
      {
        title: '공지사항 작성',
        icon: 'edit',
        to: '/notice/write',
      },
      {
        title: '공지사항 보기',
        icon: 'list',
        to: '/notice/list',
      },
    ],
  },
  {
    title: '상품',
    icon: 'inventory_2',
    children: [
      {
        title: '재고 등록',
        icon: 'add_box',
        to: '/inventory',
      },
    ],
  },
  {
    title: '통계',
    icon: 'bar_chart',
    children: [
      {
        title: '접속 로그',
        icon: 'analytics',
        to: '/statistics/log',
      },
    ],
  },
  {
    title: '계정 정보',
    icon: 'person',
    children: [
      {
        title: '2차 인증 설정',
        icon: 'vpn_key',
        to: '/security/2fa',
      },
    ],
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

// Firebase Cloud Messaging 토큰 요청 및 메시지 수신 설정
onMounted(async () => {
  if (!messaging) return

  try {
    if (Notification.permission === 'denied') { // 알림 권한 거부
      console.warn('알림 권한이 차단되어 있습니다.')
      return
    }

    if (Notification.permission !== 'granted') { // 알림 허용 요청
      await Notification.requestPermission()
    }

    if (Notification.permission === 'granted') { // 알림 허용된 경우 토큰 요청
      const token = await getToken(messaging, {
        vapidKey: import.meta.env.VITE_FIREBASE_VAPID_KEY,
      })

      console.log('FCM TOKEN:', token)

      //  TODO: Spring 서버로 토큰 저장 API 호출
    }

  } catch (e) {
    console.error('FCM 초기화 실패:', e)
  }

  if (messaging) {
    onMessage(messaging, payload => {
      new Notification(payload.notification.title, {
        body: payload.notification.body,
      });
    });
  }
});
</script>
