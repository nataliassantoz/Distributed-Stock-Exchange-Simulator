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
  {
    path: '/menu-principal',
    name: 'menu-principal',
    component: () => import('@/views/MenuPrincipalView.vue'),
    meta: { title: 'Painel — Home Broker', requiresAuth: true },
  },
  {
    path: '/mercado',
    name: 'mercado',
    component: () => import('@/views/MercadoView.vue'),
    meta: { title: 'Mercado — Home Broker', requiresAuth: true },
  },
  {
    path: '/carteira',
    name: 'carteira',
    component: () => import('@/views/CarteiraView.vue'),
    meta: { title: 'Minha Carteira — Home Broker', requiresAuth: true },
  },
  {
    path: '/ordens',
    name: 'ordens',
    component: () => import('@/views/OrdensView.vue'),
    meta: { title: 'Enviar Ordem — Home Broker', requiresAuth: true },
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

  if (to.meta?.requiresAuth) {
    const token = localStorage.getItem('hb_token')
    if (!token) {
      return { name: 'login' }
    }
  }
})

export default router
