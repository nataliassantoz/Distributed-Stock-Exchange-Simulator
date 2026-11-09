<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const nomeUsuario = computed(() => authStore.nomeUsuario || 'Investidor')
const primeiroNome = computed(() => nomeUsuario.value.split(' ')[0])

const saudacao = computed(() => {
  const hora = new Date().getHours()
  if (hora < 12) return 'Bom dia'
  if (hora < 18) return 'Boa tarde'
  return 'Boa noite'
})

const horaAtual = ref('')
const dataAtual = ref('')
let clockInterval = null

function atualizarRelogio() {
  const agora = new Date()
  horaAtual.value = agora.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  dataAtual.value = agora.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
}

onMounted(() => {
  atualizarRelogio()
  clockInterval = setInterval(atualizarRelogio, 1000)
})

onUnmounted(() => {
  if (clockInterval) clearInterval(clockInterval)
})

const mercadoAberto = ref(true)

const resumo = ref({
  saldoCaixa: 10000.00,
  patrimonioTotal: 10000.00,
  totalAtivos: 0,
  ordensAbertas: 0,
})

const cotacoes = ref([
  { ticker: 'TEC3', nome: 'TechNova', preco: 50.00, variacao: 0.00 },
  { ticker: 'ENE3', nome: 'EnergiaSul', preco: 80.00, variacao: 0.00 },
  { ticker: 'FIN3', nome: 'FinanCorp', preco: 30.00, variacao: 0.00 },
])

const menuCards = [
  {
    id: 'mercado',
    titulo: 'Mercado',
    descricao: 'Consulte os ativos disponíveis, preços e variações em tempo real.',
    icone: 'chart',
    cor: 'emerald',
    rota: '/mercado',
  },
  {
    id: 'carteira',
    titulo: 'Minha Carteira',
    descricao: 'Gerencie seus ativos, saldo e acompanhe a evolução do seu portfólio.',
    icone: 'wallet',
    cor: 'teal',
    rota: '/carteira',
  },
  {
    id: 'ordens',
    titulo: 'Enviar Ordem',
    descricao: 'Envie ordens de compra e venda definindo o ativo, preço e quantidade.',
    icone: 'arrows',
    cor: 'cyan',
    rota: '/ordens',
  },
  {
    id: 'book',
    titulo: 'Book de Ofertas',
    descricao: 'Visualize o livro de ofertas com todas as ordens pendentes para cada ativo.',
    icone: 'book',
    cor: 'sky',
    rota: null,
  },
  {
    id: 'minhas-ordens',
    titulo: 'Minhas Ordens',
    descricao: 'Acompanhe suas ordens abertas, executadas e canceladas.',
    icone: 'list',
    cor: 'violet',
    rota: null,
  },
  {
    id: 'historico',
    titulo: 'Histórico',
    descricao: 'Consulte o histórico completo de negociações e transações realizadas.',
    icone: 'history',
    cor: 'amber',
    rota: null,
  },
]

function navegarPara(card) {
  if (card.rota) {
    router.push(card.rota)
  }
}

function handleLogout() {
  authStore.logout()
  router.push({ name: 'home' })
}

function formatarMoeda(valor) {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function formatarVariacao(valor) {
  const sinal = valor >= 0 ? '+' : ''
  return `${sinal}${valor.toFixed(2)}%`
}

const corMap = {
  emerald: { r: 16, g: 185, b: 129 },
  teal:    { r: 20, g: 184, b: 166 },
  cyan:    { r: 6,  g: 182, b: 212 },
  sky:     { r: 14, g: 165, b: 233 },
  violet:  { r: 139, g: 92, b: 246 },
  amber:   { r: 245, g: 158, b: 11 },
}

function iconBg(cor) {
  const c = corMap[cor]
  return `background: rgba(${c.r},${c.g},${c.b},0.1); border: 1px solid rgba(${c.r},${c.g},${c.b},0.2); color: rgba(${c.r},${c.g},${c.b},1);`
}

function glowBg(cor) {
  const c = corMap[cor]
  return `background: radial-gradient(ellipse at top left, rgba(${c.r},${c.g},${c.b},0.06) 0%, transparent 70%);`
}

function hoverBorder(cor) {
  const c = corMap[cor]
  return `rgba(${c.r},${c.g},${c.b},0.3)`
}
</script>

<template>
  <div class="dashboard-page">
    <div class="dashboard-bg" aria-hidden="true">
      <div class="dashboard-bg__orb dashboard-bg__orb--1"></div>
      <div class="dashboard-bg__orb dashboard-bg__orb--2"></div>
      <div class="dashboard-bg__orb dashboard-bg__orb--3"></div>
      <div class="grid-lines"></div>
    </div>

    <main class="dashboard-main">
      <header class="dashboard-header animate-fade-in-up">
        <div class="header-content">
          <div class="header-left">
            <div class="market-status" :class="mercadoAberto ? 'market-status--open' : 'market-status--closed'">
              <span class="market-dot"></span>
              {{ mercadoAberto ? 'Mercado aberto' : 'Mercado fechado' }}
            </div>
            <h1 class="header-title">
              {{ saudacao }}, <span class="gradient-text">{{ primeiroNome }}</span>
            </h1>
            <p class="header-subtitle">
              Bem-vindo ao seu painel de investimentos. O que deseja fazer hoje?
            </p>
          </div>
          <div class="header-right">
            <span class="clock-time">{{ horaAtual }}</span>
            <span class="clock-date">{{ dataAtual }}</span>
          </div>
        </div>
      </header>

      <section class="section-resumo animate-fade-in-up delay-100">
        <div class="resumo-grid">
          <div class="resumo-card resumo-card--emerald">
            <div class="resumo-card__header">
              <div class="resumo-card__icon resumo-card__icon--emerald">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <span class="resumo-card__label">Saldo</span>
            </div>
            <p class="resumo-card__value">{{ formatarMoeda(resumo.saldoCaixa) }}</p>
            <span class="resumo-card__desc">Disponível em caixa</span>
          </div>

          <div class="resumo-card resumo-card--teal">
            <div class="resumo-card__header">
              <div class="resumo-card__icon resumo-card__icon--teal">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
              </div>
              <span class="resumo-card__label">Patrimônio</span>
            </div>
            <p class="resumo-card__value">{{ formatarMoeda(resumo.patrimonioTotal) }}</p>
            <span class="resumo-card__desc">Saldo + ativos</span>
          </div>

          <div class="resumo-card resumo-card--cyan">
            <div class="resumo-card__header">
              <div class="resumo-card__icon resumo-card__icon--cyan">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
              </div>
              <span class="resumo-card__label">Ativos</span>
            </div>
            <p class="resumo-card__value">{{ resumo.totalAtivos }}</p>
            <span class="resumo-card__desc">Na carteira</span>
          </div>

          <div class="resumo-card resumo-card--violet">
            <div class="resumo-card__header">
              <div class="resumo-card__icon resumo-card__icon--violet">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <span class="resumo-card__label">Ordens</span>
            </div>
            <p class="resumo-card__value">{{ resumo.ordensAbertas }}</p>
            <span class="resumo-card__desc">Pendentes</span>
          </div>
        </div>
      </section>

      <section class="section-cotacoes animate-fade-in-up delay-200">
        <div class="section-header">
          <h2 class="section-title">Cotações</h2>
          <span class="section-hint">Preços iniciais da simulação</span>
        </div>
        <div class="cotacoes-grid">
          <div v-for="cotacao in cotacoes" :key="cotacao.ticker" class="cotacao-card">
            <div class="cotacao-left">
              <div class="cotacao-badge">
                {{ cotacao.ticker.substring(0, 2) }}
              </div>
              <div>
                <p class="cotacao-ticker">{{ cotacao.ticker }}</p>
                <p class="cotacao-nome">{{ cotacao.nome }}</p>
              </div>
            </div>
            <div class="cotacao-right">
              <p class="cotacao-preco">{{ formatarMoeda(cotacao.preco) }}</p>
              <p class="cotacao-variacao" :class="cotacao.variacao >= 0 ? 'cotacao-variacao--up' : 'cotacao-variacao--down'">
                {{ formatarVariacao(cotacao.variacao) }}
              </p>
            </div>
          </div>
        </div>
      </section>

      <section class="section-menu animate-fade-in-up delay-300">
        <h2 class="section-title">Menu Principal</h2>
        <div class="menu-grid">
          <button
            v-for="(card, index) in menuCards"
            :key="card.id"
            :id="`menu-card-${card.id}`"
            class="menu-card"
            :style="{ animationDelay: `${0.3 + index * 0.08}s`, '--hover-border': hoverBorder(card.cor) }"
            @click="navegarPara(card)"
            @mouseenter="$event.currentTarget.style.borderColor = hoverBorder(card.cor)"
            @mouseleave="$event.currentTarget.style.borderColor = ''"
          >
            <div class="menu-card__glow" :style="glowBg(card.cor)"></div>

            <div class="menu-card__icon" :style="iconBg(card.cor)">
              <svg v-if="card.icone === 'chart'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 3v18h18" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 16l4-4 4 2 6-8" />
              </svg>
              <svg v-if="card.icone === 'wallet'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21 12V7H5a2 2 0 0 1 0-4h14v4" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 5v14a2 2 0 0 0 2 2h16v-5" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M18 12a2 2 0 0 0 0 4h4v-4Z" />
              </svg>
              <svg v-if="card.icone === 'arrows'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M7 3L7 21M7 3L3 7M7 3L11 7" />
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 21L17 3M17 21L13 17M17 21L21 17" />
              </svg>
              <svg v-if="card.icone === 'book'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
                <line x1="8" y1="7" x2="16" y2="7" />
                <line x1="8" y1="11" x2="13" y2="11" />
              </svg>
              <svg v-if="card.icone === 'list'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
              </svg>
              <svg v-if="card.icone === 'history'" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>

            <div class="menu-card__text">
              <h3 class="menu-card__title">{{ card.titulo }}</h3>
              <p class="menu-card__desc">{{ card.descricao }}</p>
            </div>

            <div class="menu-card__arrow">
              <svg width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M17 8l4 4m0 0l-4 4m4-4H3" />
              </svg>
            </div>
          </button>
        </div>
      </section>

      <section class="section-footer animate-fade-in-up delay-400">
        <div class="footer-bar">
          <div class="footer-user">
            <div class="footer-avatar">
              {{ primeiroNome.charAt(0).toUpperCase() }}
            </div>
            <div>
              <p class="footer-name">{{ nomeUsuario }}</p>
              <p class="footer-role">Investidor Humano · Capital inicial R$ 10.000,00</p>
            </div>
          </div>
          <button id="btn-logout" class="btn-logout" @click="handleLogout">
            <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            Sair
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.dashboard-main {
  position: relative;
  z-index: 10;
  max-width: 1100px;
  margin: 0 auto;
  padding: 88px 40px 64px;
}

.dashboard-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.dashboard-bg__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.dashboard-bg__orb--1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
  background: radial-gradient(circle, rgba(16,185,129,0.08) 0%, transparent 70%);
  animation: pulseGlow 6s ease-in-out infinite;
}

.dashboard-bg__orb--2 {
  width: 500px;
  height: 500px;
  bottom: -150px;
  left: -150px;
  background: radial-gradient(circle, rgba(20,184,166,0.06) 0%, transparent 70%);
  animation: pulseGlow 8s ease-in-out infinite reverse;
}

.dashboard-bg__orb--3 {
  width: 350px;
  height: 350px;
  top: 40%;
  right: 5%;
  background: radial-gradient(circle, rgba(6,182,212,0.04) 0%, transparent 70%);
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

.dashboard-header {
  margin-bottom: 48px;
}

.header-content {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.market-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  margin-bottom: 12px;
}

.market-status--open {
  background: rgba(16,185,129,0.1);
  color: #34d399;
  border: 1px solid rgba(16,185,129,0.2);
}

.market-status--closed {
  background: rgba(239,68,68,0.1);
  color: #f87171;
  border: 1px solid rgba(239,68,68,0.2);
}

.market-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.market-status--open .market-dot {
  background-color: #34d399;
}

.market-status--closed .market-dot {
  background-color: #f87171;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.header-title {
  font-size: 2rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.header-subtitle {
  margin-top: 8px;
  font-size: 1rem;
  color: var(--text-secondary);
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex-shrink: 0;
}

.clock-time {
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
  font-family: 'Inter', monospace;
}

.clock-date {
  font-size: 0.75rem;
  color: var(--text-muted);
  text-transform: capitalize;
}

.section-resumo {
  margin-bottom: 40px;
}

.resumo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.resumo-card {
  position: relative;
  border-radius: 16px;
  padding: 20px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;
}

.resumo-card:hover {
  transform: translateY(-2px);
}

.resumo-card--emerald:hover { border-color: rgba(16,185,129,0.3); box-shadow: 0 8px 30px rgba(16,185,129,0.08); }
.resumo-card--teal:hover    { border-color: rgba(20,184,166,0.3); box-shadow: 0 8px 30px rgba(20,184,166,0.08); }
.resumo-card--cyan:hover    { border-color: rgba(6,182,212,0.3);  box-shadow: 0 8px 30px rgba(6,182,212,0.08); }
.resumo-card--violet:hover  { border-color: rgba(139,92,246,0.3); box-shadow: 0 8px 30px rgba(139,92,246,0.08); }

.resumo-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.resumo-card__icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.resumo-card__icon--emerald { background: rgba(16,185,129,0.1); color: #34d399; }
.resumo-card__icon--teal    { background: rgba(20,184,166,0.1); color: #2dd4bf; }
.resumo-card__icon--cyan    { background: rgba(6,182,212,0.1);  color: #22d3ee; }
.resumo-card__icon--violet  { background: rgba(139,92,246,0.1); color: #a78bfa; }

.resumo-card__label {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.resumo-card__value {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
}

.resumo-card__desc {
  font-size: 0.7rem;
  color: var(--text-muted);
}

.section-cotacoes {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
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

.cotacoes-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.cotacao-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 12px;
  padding: 16px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;
  cursor: pointer;
}

.cotacao-card:hover {
  border-color: var(--border-accent);
}

.cotacao-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cotacao-badge {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(16,185,129,0.1);
  border: 1px solid rgba(16,185,129,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #34d399;
  font-weight: 700;
  font-size: 0.75rem;
  transition: transform 0.3s ease;
}

.cotacao-card:hover .cotacao-badge {
  transform: scale(1.1);
}

.cotacao-ticker {
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--text-primary);
}

.cotacao-nome {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.cotacao-right {
  text-align: right;
}

.cotacao-preco {
  font-size: 0.875rem;
  font-weight: 700;
  color: var(--text-primary);
}

.cotacao-variacao {
  font-size: 0.75rem;
  font-weight: 600;
}

.cotacao-variacao--up   { color: #34d399; }
.cotacao-variacao--down { color: #f87171; }

.section-menu {
  margin-bottom: 0;
}

.section-menu .section-title {
  margin-bottom: 24px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.menu-card {
  position: relative;
  text-align: left;
  border-radius: 16px;
  padding: 28px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  transition: all 0.3s ease;
  cursor: pointer;
  font-family: inherit;
  color: inherit;
  outline: none;
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(16,185,129,0.06);
}

.menu-card:focus-visible {
  box-shadow: 0 0 0 2px rgba(16,185,129,0.5);
}

.menu-card__glow {
  position: absolute;
  inset: 0;
  border-radius: 16px;
  opacity: 0;
  transition: opacity 0.5s ease;
  pointer-events: none;
}

.menu-card:hover .menu-card__glow {
  opacity: 1;
}

.menu-card__icon {
  position: relative;
  z-index: 1;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.menu-card:hover .menu-card__icon {
  transform: scale(1.1);
}

.menu-card__text {
  position: relative;
  z-index: 1;
}

.menu-card__title {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
  transition: color 0.3s ease;
}

.menu-card:hover .menu-card__title {
  color: #ffffff;
}

.menu-card__desc {
  font-size: 0.875rem;
  color: var(--text-muted);
  line-height: 1.5;
  transition: color 0.3s ease;
}

.menu-card:hover .menu-card__desc {
  color: var(--text-secondary);
}

.menu-card__arrow {
  position: absolute;
  top: 24px;
  right: 24px;
  opacity: 0;
  transform: translateX(8px);
  transition: all 0.3s ease;
  color: var(--text-muted);
}

.menu-card:hover .menu-card__arrow {
  opacity: 1;
  transform: translateX(0);
}

.section-footer {
  margin-top: 48px;
}

.footer-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-radius: 16px;
  padding: 24px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
}

.footer-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981, #14b8a6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-weight: 700;
  font-size: 0.875rem;
  flex-shrink: 0;
}

.footer-name {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

.footer-role {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.btn-logout {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;
  font-family: inherit;
  color: #f87171;
  background: rgba(239,68,68,0.1);
  border: 1px solid rgba(239,68,68,0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-logout:hover {
  background: rgba(239,68,68,0.2);
  border-color: rgba(239,68,68,0.3);
}

@media (max-width: 1024px) {
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-main {
    padding: 80px 24px 48px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    align-items: flex-start;
  }

  .resumo-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .cotacoes-grid {
    grid-template-columns: 1fr;
  }

  .menu-grid {
    grid-template-columns: 1fr;
  }

  .footer-bar {
    flex-direction: column;
    text-align: center;
  }

  .footer-user {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .header-title {
    font-size: 1.5rem;
  }

  .resumo-grid {
    grid-template-columns: 1fr;
  }
}
</style>
