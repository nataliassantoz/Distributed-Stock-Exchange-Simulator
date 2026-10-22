import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/views/HomePage.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePage,
    meta: { title: 'Home Broker — Simulador de Bolsa de Valores' },
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: 'Entrar — Home Broker' },
  },
  {
    path: '/registrar',
    name: 'registrar',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: 'Criar conta — Home Broker' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  document.title = to.meta?.title || 'Home Broker'
})

export default router
