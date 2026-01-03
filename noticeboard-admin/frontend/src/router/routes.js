const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('src/pages/common/IndexPage.vue') },
      {
        path: '/notice/write',
        component: () => import('src/pages/notice/NoticeWritePage.vue'),
      },
      {
        path: '/notice/list',
        component: () => import('src/pages/notice/NoticeListPage.vue'),
      },
      {
        path: '/notice/:id',
        component: () => import('src/pages/notice/NoticeViewPage.vue'),
      },
      {
        path: 'signup',
        component: () => import('src/pages/member/SignUpPage.vue'),
      },
      {
        path: 'login',
        component: () => import('src/pages/member/LoginPage.vue'),
      },
      {
        path: '/auth/email-verify',
        component: () => import('src/pages/auth/EmailCodeVerifyPage.vue'),
      },
      {
        path: '/auth/qr-verify',
        component: () => import('src/pages/auth/QrCodeVerifyPage.vue'),
      },
      {
        path: '/security/2fa',
        name: 'TwoFactorSetupPage',
        component: () => import('src/pages/auth/TwoFactorSetupPage.vue'),
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
      },
      {
        path: '/inventory',
        name: 'InventoryViewPage',
        component: () => import('src/pages/product/InventoryViewPage.vue'),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('src/pages/common/ErrorNotFound.vue'),
  },
];

export default routes;
