<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useCarteiraStore } from '@/stores/carteiraStore'

const router = useRouter()
const authStore = useAuthStore()
const carteiraStore = useCarteiraStore()

const nomeUsuario = computed(() => authStore.nomeUsuario || 'Investidor')
const primeiroNome = computed(() => nomeUsuario.value.split(' ')[0])

const isLoading = ref(true)
const hasError = ref(false)
const errorMessage = ref('')

const saldoData = computed(() => ({
  saldoCaixa: carteiraStore.saldoCaixa,
  saldoReservado: carteiraStore.saldoReservado,
  saldoDisponivel: carteiraStore.saldoDisponivel,
}))

const posicoes = computed(() => carteiraStore.posicoes)
const totalAtivos = computed(() => carteiraStore.totalAtivos)
const patrimonioEmAtivos = computed(() => carteiraStore.patrimonioEmAtivos)
const patrimonioTotal = computed(() => carteiraStore.patrimonioTotal)

async function carregarDados() {
  isLoading.value = true
  hasError.value = false
  errorMessage.value = ''
  try {
    await carteiraStore.carregarTudo()
    if (carteiraStore.error) {
      hasError.value = true
      errorMessage.value = carteiraStore.error
    }
  } catch {
    hasError.value = true
    errorMessage.value = 'Não foi possível carregar os dados da carteira.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  carregarDados()
})

function formatarMoeda(valor) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function voltarAoMenu() {
  router.push('/menu-principal')
}

const tickerColors = {
  PETR4: { r: 16, g: 185, b: 129 },
  VALE3: { r: 59, g: 130, b: 246 },
  ITUB4: { r: 245, g: 158, b: 11 },
  WEGE3: { r: 139, g: 92, b: 246 },
  BBAS3: { r: 236, g: 72, b: 153 },
  TEC3: { r: 59, g: 130, b: 246 },
  ENE3: { r: 245, g: 158, b: 11 }, 
  FIN3: { r: 139, g: 92, b: 246 },
}

const defaultColor = { r: 16, g: 185, b: 129 }

function badgeStyle(ticker) {
  const c = tickerColors[ticker] || defaultColor
  return {
    background: `rgba(${c.r},${c.g},${c.b},0.1)`,
    border: `1px solid rgba(${c.r},${c.g},${c.b},0.25)`,
    color: `rgb(${c.r},${c.g},${c.b})`,
  }
}
</script>

<template>
  <div class="carteira-page">
    <div class="carteira-bg" aria-hidden="true">
      <div class="carteira-bg__orb carteira-bg__orb--1"></div>
      <div class="carteira-bg__orb carteira-bg__orb--2"></div>
      <div class="carteira-bg__orb carteira-bg__orb--3"></div>
      <div class="grid-lines"></div>
    </div>

    <main class="carteira-main">
      <header class="carteira-header animate-fade-in-up">
        <div class="header-row">
          <div class="header-left">
            <button class="btn-voltar" @click="voltarAoMenu">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
              </svg>
              Voltar
            </button>
            <h1 class="page-title">
              Minha <span class="gradient-text">Carteira</span>
            </h1>
            <p class="page-subtitle">
              Acompanhe seu saldo disponível e posição dos ativos em carteira.
            </p>
          </div>
          <div class="header-right">
            <div class="user-info">
              <div class="user-avatar">
                {{ primeiroNome.charAt(0).toUpperCase() }}
              </div>
              <span class="user-name">{{ primeiroNome }}</span>
            </div>
          </div>
        </div>
      </header>

      <div v-if="isLoading" class="state-container animate-fade-in-up delay-100">
        <div class="state-card">
          <div class="spinner"></div>
          <p class="state-text">Carregando dados da carteira...</p>
        </div>
      </div>

      <div v-else-if="hasError" class="state-container animate-fade-in-up delay-100">
        <div class="state-card state-card--error">
          <div class="state-icon state-icon--error">
            <svg width="32" height="32" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
            </svg>
          </div>
          <p class="state-text">{{ errorMessage }}</p>
          <button class="btn-retry" @click="carregarDados">
            <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182" />
            </svg>
            Tentar novamente
          </button>
        </div>
      </div>

      <template v-else>
        <section class="section-saldo animate-fade-in-up delay-100">
          <div class="section-header">
            <h2 class="section-title">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              Saldo
            </h2>
          </div>
          <div class="saldo-grid">
            <div class="saldo-card saldo-card--principal">
              <div class="saldo-card__icon saldo-card__icon--emerald">
                <svg width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.25 18.75a60.07 60.07 0 0115.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 013 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.375m1.5-1.5H21a.75.75 0 00-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 01-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 003 15h-.75M15 10.5a3 3 0 11-6 0 3 3 0 016 0zm3 0h.008v.008H18V10.5zm-12 0h.008v.008H6V10.5z" />
                </svg>
              </div>
              <div class="saldo-card__content">
                <span class="saldo-card__label">Saldo Disponível</span>
                <p class="saldo-card__value saldo-card__value--large">{{ formatarMoeda(saldoData.saldoDisponivel) }}</p>
                <span class="saldo-card__hint">Livre para ordens de compra</span>
              </div>
            </div>

            <div class="saldo-card">
              <div class="saldo-card__icon saldo-card__icon--teal">
                <svg width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
                </svg>
              </div>
              <div class="saldo-card__content">
                <span class="saldo-card__label">Saldo Reservado</span>
                <p class="saldo-card__value">{{ formatarMoeda(saldoData.saldoReservado) }}</p>
                <span class="saldo-card__hint">Bloqueado em ordens</span>
              </div>
            </div>

            <div class="saldo-card">
              <div class="saldo-card__icon saldo-card__icon--cyan">
                <svg width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 6v12m-3-2.818l.879.659c1.171.879 3.07.879 4.242 0 1.172-.879 1.172-2.303 0-3.182C13.536 12.219 12.768 12 12 12c-.725 0-1.45-.22-2.003-.659-1.106-.879-1.106-2.303 0-3.182s2.9-.879 4.006 0l.415.33M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="saldo-card__content">
                <span class="saldo-card__label">Saldo Total (Caixa)</span>
                <p class="saldo-card__value">{{ formatarMoeda(saldoData.saldoCaixa) }}</p>
                <span class="saldo-card__hint">Disponível + reservado</span>
              </div>
            </div>
          </div>
        </section>

        <section class="section-patrimonio animate-fade-in-up delay-200">
          <div class="patrimonio-bar">
            <div class="patrimonio-item">
              <span class="patrimonio-label">Patrimônio em ativos</span>
              <span class="patrimonio-value">{{ formatarMoeda(patrimonioEmAtivos) }}</span>
            </div>
            <div class="patrimonio-divider"></div>
            <div class="patrimonio-item">
              <span class="patrimonio-label">Total de ativos</span>
              <span class="patrimonio-value patrimonio-value--count">{{ totalAtivos }}</span>
            </div>
            <div class="patrimonio-divider"></div>
            <div class="patrimonio-item patrimonio-item--highlight">
              <span class="patrimonio-label">Patrimônio total</span>
              <span class="patrimonio-value patrimonio-value--total">{{ formatarMoeda(patrimonioTotal) }}</span>
            </div>
          </div>
        </section>

        <section class="section-carteira animate-fade-in-up delay-300">
          <div class="section-header">
            <h2 class="section-title">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 12V7H5a2 2 0 0 1 0-4h14v4" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 5v14a2 2 0 0 0 2 2h16v-5" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M18 12a2 2 0 0 0 0 4h4v-4Z" />
              </svg>
              Ativos em Carteira
            </h2>
            <span class="section-hint">Posições do investidor</span>
          </div>

          <div v-if="posicoes.length === 0" class="empty-state">
            <div class="empty-icon">
              <svg width="48" height="48" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
              </svg>
            </div>
            <p class="empty-title">Nenhum ativo na carteira</p>
            <p class="empty-desc">Envie ordens de compra para começar a investir.</p>
          </div>

          <div v-else class="ativos-table-wrapper">
            <table class="ativos-table">
              <thead>
                <tr>
                  <th class="th-ativo">Ativo</th>
                  <th class="th-quantidade">Quantidade</th>
                  <th class="th-reservado">Reservadas</th>
                  <th class="th-preco">Preço Médio</th>
                  <th class="th-preco-atual">Preço Atual</th>
                  <th class="th-total">Valor Total</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="posicao in posicoes" :key="posicao.ativoId || posicao.ticker" class="ativo-row">
                  <td class="td-ativo">
                    <div class="ativo-info">
                      <div class="ativo-badge" :style="badgeStyle(posicao.ticker)">
                        {{ (posicao.ticker || '??').substring(0, 2) }}
                      </div>
                      <div>
                        <span class="ativo-ticker">{{ posicao.ticker }}</span>
                        <span class="ativo-nome">{{ posicao.nome }}</span>
                      </div>
                    </div>
                  </td>
                  <td class="td-quantidade">
                    <span class="valor-principal">{{ posicao.quantidade }}</span>
                    <span class="valor-unidade">ações</span>
                  </td>
                  <td class="td-reservado">
                    <span :class="posicao.quantidadeReservada > 0 ? 'reservado--ativo' : 'reservado--zero'">
                      {{ posicao.quantidadeReservada || 0 }}
                    </span>
                  </td>
                  <td class="td-preco">
                    {{ formatarMoeda(posicao.precoMedio || 0) }}
                  </td>
                  <td class="td-preco-atual">
                    {{ formatarMoeda(posicao.precoAtual || posicao.precoMedio || 0) }}
                  </td>
                  <td class="td-total">
                    <span class="valor-total">
                      {{ formatarMoeda(posicao.quantidade * (posicao.precoAtual || posicao.precoMedio || 0)) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>
      </template>
    </main>
  </div>
</template>

<style scoped>
.carteira-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.carteira-main {
  position: relative;
  z-index: 10;
  max-width: 1100px;
  margin: 0 auto;
  padding: 88px 40px 64px;
}

.carteira-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.carteira-bg__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.carteira-bg__orb--1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
  background: radial-gradient(circle, rgba(20,184,166,0.08) 0%, transparent 70%);
  animation: pulseGlow 6s ease-in-out infinite;
}

.carteira-bg__orb--2 {
  width: 500px;
  height: 500px;
  bottom: -150px;
  left: -150px;
  background: radial-gradient(circle, rgba(6,182,212,0.06) 0%, transparent 70%);
  animation: pulseGlow 8s ease-in-out infinite reverse;
}

.carteira-bg__orb--3 {
  width: 350px;
  height: 350px;
  top: 50%;
  right: 10%;
  background: radial-gradient(circle, rgba(16,185,129,0.04) 0%, transparent 70%);
  animation: pulseGlow 10s ease-in-out infinite;
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.carteira-header {
  margin-bottom: 40px;
}

.header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.btn-voltar {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  font-family: inherit;
  color: var(--text-secondary);
  background: rgba(148,163,184,0.08);
  border: 1px solid var(--border-subtle);
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.btn-voltar:hover {
  color: var(--text-primary);
  background: rgba(148,163,184,0.14);
  border-color: var(--border-accent);
  transform: translateX(-2px);
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.2;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 1rem;
  color: var(--text-secondary);
}

.header-right {
  flex-shrink: 0;
  padding-top: 40px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px 6px 6px;
  border-radius: 9999px;
  background: rgba(16,185,129,0.08);
  border: 1px solid rgba(16,185,129,0.2);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981, #14b8a6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 0.8rem;
  flex-shrink: 0;
}

.user-name {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-primary);
}

.state-container {
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.state-card {
  text-align: center;
  padding: 48px;
  border-radius: 20px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  max-width: 420px;
  width: 100%;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-subtle);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.state-text {
  font-size: 0.95rem;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.state-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.state-icon--error {
  background: rgba(245,158,11,0.1);
  color: #f59e0b;
  border: 1px solid rgba(245,158,11,0.2);
}

.btn-retry {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 9999px;
  font-size: 0.85rem;
  font-weight: 600;
  font-family: inherit;
  color: var(--bg-primary);
  background: var(--accent-primary);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 20px var(--accent-glow);
}

.btn-retry:hover {
  background: #0ea472;
  transform: translateY(-1px);
  box-shadow: 0 0 30px var(--accent-glow-strong);
}

.section-saldo {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.section-hint {
  font-size: 0.7rem;
  color: var(--text-muted);
}

.saldo-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr 1fr;
  gap: 20px;
}

.saldo-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  border-radius: 16px;
  padding: 24px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;
}

.saldo-card:hover {
  transform: translateY(-2px);
}

.saldo-card--principal {
  background: linear-gradient(135deg, rgba(16,185,129,0.06) 0%, rgba(15,22,41,0.6) 100%);
}

.saldo-card--principal:hover {
  border-color: rgba(16,185,129,0.3);
  box-shadow: 0 8px 30px rgba(16,185,129,0.08);
}

.saldo-card:not(.saldo-card--principal):hover {
  border-color: var(--border-accent);
}

.saldo-card__icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.saldo-card__icon--emerald {
  background: rgba(16,185,129,0.1);
  color: #34d399;
  border: 1px solid rgba(16,185,129,0.2);
}

.saldo-card__icon--teal {
  background: rgba(20,184,166,0.1);
  color: #2dd4bf;
  border: 1px solid rgba(20,184,166,0.2);
}

.saldo-card__icon--cyan {
  background: rgba(6,182,212,0.1);
  color: #22d3ee;
  border: 1px solid rgba(6,182,212,0.2);
}

.saldo-card__content {
  min-width: 0;
}

.saldo-card__label {
  display: block;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}

.saldo-card__value {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.saldo-card__value--large {
  font-size: 1.5rem;
  background: linear-gradient(135deg, #34d399, #2dd4bf);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.saldo-card__hint {
  font-size: 0.7rem;
  color: var(--text-muted);
}

.section-patrimonio {
  margin-bottom: 32px;
}

.patrimonio-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 20px 28px;
  border-radius: 14px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
}

.patrimonio-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.patrimonio-item--highlight {
  text-align: right;
}

.patrimonio-label {
  font-size: 0.7rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.patrimonio-value {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.patrimonio-value--count {
  color: #22d3ee;
}

.patrimonio-value--total {
  font-size: 1.15rem;
  background: linear-gradient(135deg, #34d399, #06b6d4);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.patrimonio-divider {
  width: 1px;
  height: 36px;
  background: var(--border-subtle);
  flex-shrink: 0;
}

.section-carteira {
  margin-bottom: 48px;
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
  border-radius: 16px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(16,185,129,0.06);
  border: 1px solid rgba(16,185,129,0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: var(--text-muted);
}

.empty-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 0.875rem;
  color: var(--text-muted);
}

.ativos-table-wrapper {
  border-radius: 16px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  overflow: hidden;
}

.ativos-table {
  width: 100%;
  border-collapse: collapse;
}

.ativos-table thead {
  background: rgba(148,163,184,0.04);
  border-bottom: 1px solid var(--border-subtle);
}

.ativos-table th {
  padding: 14px 20px;
  font-size: 0.7rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  text-align: left;
  white-space: nowrap;
}

.th-quantidade,
.th-reservado,
.th-preco,
.th-preco-atual,
.th-total {
  text-align: right;
}

.ativo-row {
  border-bottom: 1px solid rgba(148,163,184,0.06);
  transition: background 0.2s ease;
}

.ativo-row:last-child {
  border-bottom: none;
}

.ativo-row:hover {
  background: rgba(16,185,129,0.03);
}

.ativos-table td {
  padding: 16px 20px;
  font-size: 0.875rem;
  color: var(--text-primary);
  vertical-align: middle;
}

.td-quantidade,
.td-reservado,
.td-preco,
.td-preco-atual,
.td-total {
  text-align: right;
}

.td-ativo {
  min-width: 200px;
}

.ativo-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ativo-badge {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.75rem;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.ativo-row:hover .ativo-badge {
  transform: scale(1.08);
}

.ativo-ticker {
  display: block;
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--text-primary);
}

.ativo-nome {
  display: block;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.valor-principal {
  font-weight: 700;
  font-size: 0.95rem;
}

.valor-unidade {
  font-size: 0.7rem;
  color: var(--text-muted);
  margin-left: 4px;
}

.reservado--ativo {
  color: #f59e0b;
  font-weight: 600;
}

.reservado--zero {
  color: var(--text-muted);
}

.td-preco,
.td-preco-atual {
  font-variant-numeric: tabular-nums;
}

.valor-total {
  font-weight: 700;
  color: #34d399;
}

@media (max-width: 1024px) {
  .saldo-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .carteira-main {
    padding: 80px 24px 48px;
  }

  .header-row {
    flex-direction: column;
  }

  .header-right {
    padding-top: 0;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .patrimonio-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .patrimonio-divider {
    width: 100%;
    height: 1px;
  }

  .patrimonio-item--highlight {
    text-align: left;
  }

  .ativos-table-wrapper {
    overflow-x: auto;
  }

  .ativos-table {
    min-width: 640px;
  }
}
</style>
