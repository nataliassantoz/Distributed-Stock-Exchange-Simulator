<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function voltarAoMenu() {
  router.push('/menu-principal')
}
</script>

<template>
  <div class="mercado-page">
    <div class="mercado-bg" aria-hidden="true">
      <div class="mercado-bg__orb mercado-bg__orb--1"></div>
      <div class="mercado-bg__orb mercado-bg__orb--2"></div>
      <div class="grid-lines"></div>
    </div>

    <main class="mercado-main animate-fade-in-up">
      <div class="mercado-card">
        <div class="mercado-badge">
          <span class="mercado-badge__dot"></span>
          Em Desenvolvimento
        </div>

        <div class="mercado-icon">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
            <path stroke-linecap="round" stroke-linejoin="round" d="M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a2 2 0 01-2 2H6a2 2 0 01-2-2V4z" />
          </svg>
        </div>

        <h1 class="mercado-title">Mercado de Ativos</h1>
        <p class="mercado-desc">
          Esta tela exibirá a listagem de ações, preços e cotações em tempo real integradas ao motor distribuído do Home Broker.
        </p>

        <div class="mercado-actions">
          <button class="mercado-btn" @click="voltarAoMenu">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            Voltar ao Menu Principal
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.mercado-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 24px 48px;
}

.mercado-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.mercado-bg__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.mercado-bg__orb--1 {
  width: 500px;
  height: 500px;
  top: -100px;
  right: -100px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.1) 0%, transparent 70%);
}

.mercado-bg__orb--2 {
  width: 450px;
  height: 450px;
  bottom: -100px;
  left: -100px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.08) 0%, transparent 70%);
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.mercado-main {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 540px;
}

.mercado-card {
  background: var(--bg-card);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--border-subtle);
  border-radius: 20px;
  padding: 48px 36px;
  text-align: center;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.4);
}

.mercado-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  color: #34d399;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.25);
  margin-bottom: 24px;
}

.mercado-badge__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #34d399;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.mercado-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(16, 185, 129, 0.1);
  border: 1px solid rgba(16, 185, 129, 0.2);
  color: var(--accent-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.mercado-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin-bottom: 12px;
}

.mercado-desc {
  font-size: 0.95rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 32px;
}

.mercado-actions {
  display: flex;
  justify-content: center;
}

.mercado-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: var(--radius-full);
  background: var(--accent-primary);
  color: var(--bg-primary);
  font-weight: 600;
  font-size: 0.9rem;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 0 20px var(--accent-glow);
}

.mercado-btn:hover {
  background: #0ea472;
  transform: translateY(-1px);
  box-shadow: 0 0 30px var(--accent-glow-strong);
}

.mercado-btn:active {
  transform: translateY(0);
}
</style>
