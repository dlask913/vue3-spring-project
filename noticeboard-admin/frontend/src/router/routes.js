const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/IndexPage.vue') },
      { path: 'notice', component: () => import('pages/NoticeBoard.vue') },
      { path: 'signup', component: () => import('pages/AdminMemberForm.vue') },
      { path: 'login', component: () => import('pages/LoginForm.vue') },
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
