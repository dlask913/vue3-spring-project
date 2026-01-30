const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '/',
        name: 'IndexPage',
        component: () => import('src/pages/common/IndexPage.vue'),
      },
      {
        path: '/notice/write',
        name: 'NoticeWritePage',
        component: () => import('src/pages/notice/NoticeWritePage.vue'),
        meta: {
          requiresAuth: true,
          roles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
        },
      },
      {
        path: '/notice/list',
        name: 'NoticeListPage',
        component: () => import('src/pages/notice/NoticeListPage.vue'),
        meta: {
          requiresAuth: true,
          roles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
        },
      },
      {
        path: '/notice/:id',
        name: 'NoticeViewPage',
        component: () => import('src/pages/notice/NoticeViewPage.vue'),
        meta: {
          requiresAuth: true,
          roles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
        },
      },
      {
        path: '/signup',
        name: 'SignUpPage',
        component: () => import('src/pages/member/SignUpPage.vue'),
      },
      {
        path: '/login',
        name: 'LoginPage',
        component: () => import('src/pages/member/LoginPage.vue'),
      },
      {
        path: '/auth/email-verify',
        name: 'EmailCodeVerifyPage',
        component: () => import('src/pages/auth/EmailCodeVerifyPage.vue'),
      },
      {
        path: '/auth/qr-verify',
        name: 'QrCodeVerifyPage',
        component: () => import('src/pages/auth/QrCodeVerifyPage.vue'),
      },
      {
        path: '/security/2fa',
        name: 'TwoFactorSetupPage',
        component: () => import('src/pages/auth/TwoFactorSetupPage.vue'),
        meta: {
          requiresAuth: true,
        },
      },
      {
        path: '/2fa/select',
        name: 'TwoFactorMethodSelectPage',
        component: () => import('src/pages/auth/TwoFactorMethodSelectPage.vue'),
      },
      {
        path: '/statistics/log',
        name: 'LogStatisticsViewPage',
        component: () =>
          import('src/pages/statistics/LogStatisticsViewPage.vue'),
        meta: {
          requiresAuth: true,
        },
      },
      {
        path: '/inventory',
        name: 'InventoryViewPage',
        component: () => import('src/pages/product/InventoryViewPage.vue'),
        meta: { requiresAuth: true, roles: ['ROLE_MANAGER', 'ROLE_ADMIN'] }, // 권한 정보 추가
      },
      {
        path: '/error/denied',
        name: 'AccessDeniedPage',
        component: () => import('src/pages/error/AccessDeniedPage.vue'),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    name: 'ErrorNotFound',
    component: () => import('src/pages/common/ErrorNotFound.vue'),
  },
];

export default routes;
