<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

const isHomePage = computed(() => route.path === '/')

const navLinks = computed(() => {
  if (isHomePage.value) {
    const links = [
      { id: 'inicio', label: 'Início', href: '#hero', type: 'scroll' },
    ]

    if (authStore.isAuthenticated) {
      links.push({ id: 'menu', label: 'Menu', to: '/menu-principal', type: 'route', active: false })
    }

    links.push(
      { id: 'funcionalidades', label: 'Funcionalidades', href: '#funcionalidades', type: 'scroll' },
      { id: 'como-funciona', label: 'Como Funciona', href: '#como-funciona', type: 'scroll' },
    )

    return links
  }
  return [
    { id: 'inicio', label: 'Início', to: '/', type: 'route', active: route.path === '/' },
    { id: 'menu', label: 'Menu', to: '/menu-principal', type: 'route', active: route.path === '/menu-principal' },
    { id: 'mercado', label: 'Mercado', to: '/mercado', type: 'route', active: route.path === '/mercado' },
  ]
})

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const scrollTo = async (href) => {
  isMobileMenuOpen.value = false
  if (route.path !== '/') {
    await router.push('/')
    setTimeout(() => {
      const el = document.querySelector(href)
      if (el) el.scrollIntoView({ behavior: 'smooth' })
    }, 100)
    return
  }
  const el = document.querySelector(href)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleNavClick = (link) => {
  isMobileMenuOpen.value = false
  if (link.type === 'scroll') {
    scrollTo(link.href)
  } else if (link.type === 'route') {
    if (route.path !== link.to) {
      router.push(link.to)
    }
  }
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const goToLogin = () => {
  isMobileMenuOpen.value = false
  router.push('/login')
}

const goToHome = () => {
  isMobileMenuOpen.value = false
  if (route.path !== '/') {
    router.push('/')
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const handleLogout = () => {
  isMobileMenuOpen.value = false
  authStore.logout()
  router.push('/')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <header
    id="topbar"
    class="topbar"
    :class="{ 'topbar--scrolled': isScrolled }"
  >
    <div class="topbar__container">
      <a href="/" class="topbar__logo" @click.prevent="goToHome">
        <div class="topbar__logo-icon">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="2" y="16" width="4" height="10" rx="1" fill="currentColor" opacity="0.5"/>
            <rect x="8" y="10" width="4" height="16" rx="1" fill="currentColor" opacity="0.7"/>
            <rect x="14" y="6" width="4" height="20" rx="1" fill="currentColor" opacity="0.85"/>
            <rect x="20" y="2" width="4" height="24" rx="1" fill="currentColor"/>
          </svg>
        </div>
        <span class="topbar__logo-text">Home Broker</span>
      </a>

      <nav class="topbar__nav" aria-label="Navegação principal">
        <ul class="topbar__nav-list">
          <li v-for="link in navLinks" :key="link.id" class="topbar__nav-item">
            <a
              :href="link.type === 'scroll' ? link.href : link.to"
              class="topbar__nav-link"
              :class="{ 'topbar__nav-link--active': link.active }"
              @click.prevent="handleNavClick(link)"
            >
              {{ link.label }}
            </a>
          </li>
        </ul>
      </nav>

      <div class="topbar__actions">
        <div v-if="authStore.isAuthenticated" class="topbar__user-area">
          <div class="topbar__user-badge" :title="authStore.nomeUsuario || 'Usuário logado'">
            <div class="topbar__user-avatar">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <span class="topbar__user-name">{{ authStore.nomeUsuario || 'Investidor' }}</span>
          </div>

          <button
            id="topbar-btn-logout"
            class="topbar__logout-btn"
            @click="handleLogout"
            title="Sair da conta"
            aria-label="Sair da conta"
          >
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>

        <button v-else id="btn-entrar" class="topbar__cta" @click="goToLogin">
          Entrar
        </button>
      </div>

      <button
        class="topbar__mobile-toggle"
        :class="{ 'topbar__mobile-toggle--active': isMobileMenuOpen }"
        @click="toggleMobileMenu"
        aria-label="Menu"
      >
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>

    <Transition name="mobile-menu">
      <div v-if="isMobileMenuOpen" class="topbar__mobile-menu">
        <nav>
          <ul class="topbar__mobile-list">
            <li v-for="link in navLinks" :key="link.id">
              <a
                :href="link.type === 'scroll' ? link.href : link.to"
                class="topbar__mobile-link"
                :class="{ 'topbar__mobile-link--active': link.active }"
                @click.prevent="handleNavClick(link)"
              >
                {{ link.label }}
              </a>
            </li>

            <li v-if="authStore.isAuthenticated" class="topbar__mobile-auth">
              <div class="topbar__user-badge topbar__user-badge--mobile">
                <div class="topbar__user-avatar">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                </div>
                <span class="topbar__user-name">{{ authStore.nomeUsuario || 'Investidor' }}</span>
              </div>
              <button class="topbar__logout-btn topbar__logout-btn--mobile" @click="handleLogout">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                <span>Sair</span>
              </button>
            </li>
            <li v-else>
              <button class="topbar__cta topbar__cta--mobile" @click="goToLogin">
                Entrar
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </Transition>
  </header>
</template>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 0;
  transition: all var(--transition-smooth);
}

.topbar--scrolled {
  background: var(--bg-topbar);
  backdrop-filter: blur(var(--glass-blur));
  -webkit-backdrop-filter: blur(var(--glass-blur));
  border-bottom: 1px solid var(--border-subtle);
  padding: 10px 0;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
}

.topbar__container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.topbar__logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform var(--transition-fast);
}

.topbar__logo:hover {
  transform: scale(1.02);
}

.topbar__logo-icon {
  color: var(--accent-primary);
  display: flex;
  align-items: center;
}

.topbar__logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.topbar__nav-list {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topbar__nav-link {
  padding: 8px 16px;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
  position: relative;
}

.topbar__nav-link::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%) scaleX(0);
  width: 20px;
  height: 2px;
  background: var(--accent-primary);
  border-radius: var(--radius-full);
  transition: transform var(--transition-fast);
}

.topbar__nav-link:hover {
  color: var(--text-primary);
}

.topbar__nav-link:hover::after {
  transform: translateX(-50%) scaleX(1);
}

.topbar__nav-link--active {
  color: var(--text-primary);
}

.topbar__nav-link--active::after {
  transform: translateX(-50%) scaleX(1);
}

.topbar__cta {
  padding: 10px 24px;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--bg-primary);
  background: var(--accent-primary);
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 0 20px var(--accent-glow);
}

.topbar__cta:hover {
  background: #0ea472;
  transform: translateY(-1px);
  box-shadow: 0 0 30px var(--accent-glow-strong);
}

.topbar__cta:active {
  transform: translateY(0);
}

.topbar__cta--mobile {
  width: 100%;
  margin-top: 8px;
}

.topbar__user-area {
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__user-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 5px 14px 5px 6px;
  background: rgba(16, 185, 129, 0.08);
  border: 1px solid rgba(16, 185, 129, 0.25);
  border-radius: var(--radius-full);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  transition: all var(--transition-fast);
}

.topbar__user-badge:hover {
  background: rgba(16, 185, 129, 0.14);
  border-color: rgba(16, 185, 129, 0.4);
  box-shadow: 0 0 15px rgba(16, 185, 129, 0.15);
}

.topbar__user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(16, 185, 129, 0.18);
  color: var(--accent-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.topbar__user-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-primary);
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topbar__logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(239, 68, 68, 0.08);
  border: 1px solid rgba(239, 68, 68, 0.22);
  color: #f87171;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.topbar__logout-btn:hover {
  background: rgba(239, 68, 68, 0.18);
  border-color: rgba(239, 68, 68, 0.45);
  color: #ef4444;
  transform: scale(1.05);
  box-shadow: 0 0 16px rgba(239, 68, 68, 0.25);
}

.topbar__logout-btn:active {
  transform: scale(0.96);
}

.topbar__mobile-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  z-index: 1001;
}

.topbar__mobile-toggle span {
  display: block;
  width: 22px;
  height: 2px;
  background: var(--text-primary);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.topbar__mobile-toggle--active span:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.topbar__mobile-toggle--active span:nth-child(2) {
  opacity: 0;
}

.topbar__mobile-toggle--active span:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

.topbar__mobile-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-subtle);
  padding: 16px 24px;
}

.topbar__mobile-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.topbar__mobile-link {
  display: block;
  padding: 12px 16px;
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-secondary);
  border-radius: var(--radius-sm);
  transition: all var(--transition-fast);
}

.topbar__mobile-link:hover {
  color: var(--text-primary);
  background: var(--accent-glow);
}

.topbar__mobile-link--active {
  color: var(--accent-primary);
  background: rgba(16, 185, 129, 0.1);
  font-weight: 600;
}

.topbar__mobile-auth {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-top: 12px;
  margin-top: 6px;
  border-top: 1px solid var(--border-subtle);
}

.topbar__user-badge--mobile {
  width: 100%;
  justify-content: flex-start;
  padding: 8px 12px;
}

.topbar__logout-btn--mobile {
  width: 100%;
  border-radius: var(--radius-sm);
  height: 42px;
  gap: 8px;
  font-size: 0.875rem;
  font-weight: 500;
}

.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all var(--transition-fast);
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .topbar__nav,
  .topbar__actions {
    display: none;
  }

  .topbar__mobile-toggle {
    display: flex;
  }
}
</style>
