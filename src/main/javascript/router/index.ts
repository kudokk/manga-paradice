import { createRouter, createWebHistory } from 'vue-router'
import { NAMES } from '@/router/name'
import HomeView from '@/pages/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/error',
      name: NAMES.Error,
      component: () => import('../pages/VueError.vue')
    },
    {
      path: '/',
      name: NAMES.Home,
      component: HomeView
    },
    {
      path: '/about',
      name: NAMES.About,
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../pages/AboutView.vue')
    },
    {
      path: '/manga/create',
      name: NAMES.Manga.Create,
      component: () => import('../pages/manga/Create.vue')
    },
    {
      path: '/manga/:mangaId/detail',
      name: NAMES.Manga.Detail,
      component: () => import('../pages/manga/Detail.vue')
    },
    {
      path: '/manga/:mangaId/:chapterId/views',
      name: NAMES.Manga.Views,
      component: () => import('../pages/manga/Views.vue')
    }
  ]
})

export default router
