<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)

const navLinks = [
  { label: 'Início', href: '#hero' },
  { label: 'Funcionalidades', href: '#funcionalidades' },
  { label: 'Como Funciona', href: '#como-funciona' },
]

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const scrollTo = async (href) => {
  isMobileMenuOpen.value = false
  if (router.currentRoute.value.path !== '/') {
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

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const goToLogin = () => {
  isMobileMenuOpen.value = false
  router.push('/login')
}

const goToHome = () => {
  isMobileMenuOpen.value = false
  if (router.currentRoute.value.path !== '/') {
    router.push('/')
  } else {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
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
      <!-- Logo -->
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

      <!-- Navigation Desktop -->
      <nav class="topbar__nav" aria-label="Navegação principal">
        <ul class="topbar__nav-list">
          <li v-for="link in navLinks" :key="link.href" class="topbar__nav-item">
            <a
              :href="link.href"
              class="topbar__nav-link"
              @click.prevent="scrollTo(link.href)"
            >
              {{ link.label }}
            </a>
          </li>
        </ul>
      </nav>

      <!-- CTA Button -->
      <div class="topbar__actions">
        <button id="btn-entrar" class="topbar__cta" @click="goToLogin">
          Entrar
        </button>
      </div>

      <!-- Mobile Menu Toggle -->
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

    <!-- Mobile Menu -->
    <Transition name="mobile-menu">
      <div v-if="isMobileMenuOpen" class="topbar__mobile-menu">
        <nav>
          <ul class="topbar__mobile-list">
            <li v-for="link in navLinks" :key="link.href">
              <a
                :href="link.href"
                class="topbar__mobile-link"
                @click.prevent="scrollTo(link.href)"
              >
                {{ link.label }}
              </a>
            </li>
            <li>
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

/* Logo */
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

/* Navigation */
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

/* CTA Button */
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

/* Mobile Toggle */
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

/* Mobile Menu */
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

/* Transitions */
.mobile-menu-enter-active,
.mobile-menu-leave-active {
  transition: all var(--transition-fast);
}

.mobile-menu-enter-from,
.mobile-menu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Responsive */
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
