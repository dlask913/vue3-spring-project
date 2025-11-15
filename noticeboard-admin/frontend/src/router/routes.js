const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      {
        path: 'notice/write',
        component: () => import('src/pages/NoticeWritePage.vue'),
      },
      {
        path: 'notice/list',
        component: () => import('src/pages/NoticeListPage.vue'),
      },
      { path: 'signup', component: () => import('src/pages/SignUpPage.vue') },
      { path: 'login', component: () => import('src/pages/LoginPage.vue') },
      {
        path: 'verify-code',
        component: () => import('src/pages/EmailCodeVerifyPage.vue'),
      },
      {
        path: '/security/2fa',
        name: 'TwoFactorSetupPage',
        component: () => import('pages/TwoFactorSetupPage.vue'),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
