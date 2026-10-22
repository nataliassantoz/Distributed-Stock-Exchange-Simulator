<script setup>
import { ref, onMounted } from 'vue'

const observedElements = ref(new Set())

onMounted(() => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.1, rootMargin: '0px 0px -50px 0px' }
  )

  document.querySelectorAll('.observe').forEach((el) => {
    observer.observe(el)
  })
})

const features = [
  {
    icon: 'wallet',
    title: 'Carteira Digital',
    description: 'Gerencie seus ativos e acompanhe o saldo da sua carteira em tempo real. Tudo organizado em um só lugar.',
  },
  {
    icon: 'arrows',
    title: 'Compra e Venda',
    description: 'Envie ordens de compra e venda de ações de forma rápida e intuitiva, definindo preço e quantidade.',
  },
  {
    icon: 'chart',
    title: 'Mercado em Tempo Real',
    description: 'Acompanhe os preços dos ativos sendo atualizados ao vivo, conforme as negociações acontecem.',
  },
  {
    icon: 'book',
    title: 'Book de Ofertas',
    description: 'Visualize o livro de ofertas com todas as ordens de compra e venda pendentes para cada ativo.',
  },
  {
    icon: 'engine',
    title: 'Negociações Automáticas',
    description: 'As ordens compatíveis são casadas automaticamente, garantindo a melhor execução para você.',
  },
  {
    icon: 'shield',
    title: 'Segurança',
    description: 'Suas operações são protegidas. Cada investidor acessa apenas seus próprios recursos e dados.',
  },
]

const steps = [
  {
    number: '01',
    title: 'Crie sua conta',
    description: 'Cadastre-se rapidamente e receba seu capital inicial para começar a investir.',
  },
  {
    number: '02',
    title: 'Explore o mercado',
    description: 'Consulte os ativos disponíveis, preços e variações em tempo real.',
  },
  {
    number: '03',
    title: 'Envie ordens',
    description: 'Compre ou venda ações definindo o preço e a quantidade desejada.',
  },
  {
    number: '04',
    title: 'Acompanhe seus resultados',
    description: 'Visualize sua carteira, histórico de negociações e evolução do portfólio.',
  },
]

// Mock Up de Ativos
const tickerItems = [
  { symbol: 'AAA3', price: '42.50', change: '+2.35%', up: true },
  { symbol: 'BBB4', price: '18.90', change: '-1.12%', up: false },
  { symbol: 'CCC3', price: '95.20', change: '+0.85%', up: true },
  { symbol: 'DDD4', price: '27.40', change: '+3.10%', up: true },
  { symbol: 'EEE3', price: '63.75', change: '-0.45%', up: false },
]
</script>

<template>
  <main class="home">
    <div class="home__orbs" aria-hidden="true">
      <div class="home__orb home__orb--1"></div>
      <div class="home__orb home__orb--2"></div>
      <div class="home__orb home__orb--3"></div>
    </div>

    <div class="ticker" aria-label="Cotações simuladas">
      <div class="ticker__track">
        <div class="ticker__content" v-for="i in 2" :key="i">
          <div
            v-for="item in tickerItems"
            :key="`${i}-${item.symbol}`"
            class="ticker__item"
          >
            <span class="ticker__symbol">{{ item.symbol }}</span>
            <span class="ticker__price">R$ {{ item.price }}</span>
            <span
              class="ticker__change"
              :class="item.up ? 'ticker__change--up' : 'ticker__change--down'"
            >
              {{ item.change }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <section id="hero" class="hero">
      <div class="hero__container">
        <div class="hero__content animate-fade-in-up">
          <div class="hero__badge">
            <span class="hero__badge-dot"></span>
            Simulador de Bolsa de Valores
          </div>

          <h1 class="hero__title">
            Negocie ativos com<br />
            <span class="gradient-text">inteligência e agilidade</span>
          </h1>

          <p class="hero__subtitle">
            Simule investimentos em uma bolsa de valores distribuída.
            Compre e venda ações, acompanhe o mercado em tempo real e
            gerencie sua carteira com facilidade.
          </p>

          <div class="hero__buttons">
            <button id="btn-comecar" class="hero__btn hero__btn--primary">
              <span>Começar Agora</span>
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                <path d="M3 8H13M13 8L9 4M13 8L9 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <a href="#funcionalidades" class="hero__btn hero__btn--outline" @click.prevent="$el => document.querySelector('#funcionalidades').scrollIntoView({ behavior: 'smooth' })">
              Saiba Mais
            </a>
          </div>

          <div class="hero__stats">
            <div class="hero__stat">
              <span class="hero__stat-number">5+</span>
              <span class="hero__stat-label">Ativos disponíveis</span>
            </div>
            <div class="hero__stat-divider"></div>
            <div class="hero__stat">
              <span class="hero__stat-number">Tempo Real</span>
              <span class="hero__stat-label">Atualização do mercado</span>
            </div>
            <div class="hero__stat-divider"></div>
            <div class="hero__stat">
              <span class="hero__stat-number">R$ 10k</span>
              <span class="hero__stat-label">Capital inicial</span>
            </div>
          </div>
        </div>

        <div class="hero__visual animate-fade-in-up delay-300">
          <div class="hero__chart-card glass">
            <div class="hero__chart-header">
              <div>
                <span class="hero__chart-symbol">AAA3</span>
                <span class="hero__chart-name">Ação Alpha</span>
              </div>
              <div class="hero__chart-price-group">
                <span class="hero__chart-price">R$ 42,50</span>
                <span class="hero__chart-change hero__chart-change--up">+2,35%</span>
              </div>
            </div>
            <div class="hero__chart-graph">
              <svg viewBox="0 0 400 150" preserveAspectRatio="none" class="hero__chart-svg">
                <defs>
                  <linearGradient id="chartGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" style="stop-color: var(--accent-primary); stop-opacity: 0.3" />
                    <stop offset="100%" style="stop-color: var(--accent-primary); stop-opacity: 0" />
                  </linearGradient>
                </defs>
                <path
                  d="M0,120 Q30,100 60,95 T120,80 T180,60 T240,70 T300,40 T360,30 T400,20 V150 H0 Z"
                  fill="url(#chartGradient)"
                  class="hero__chart-area"
                />
                <path
                  d="M0,120 Q30,100 60,95 T120,80 T180,60 T240,70 T300,40 T360,30 T400,20"
                  fill="none"
                  stroke="var(--accent-primary)"
                  stroke-width="2.5"
                  stroke-linecap="round"
                  class="hero__chart-line"
                />
                <circle cx="400" cy="20" r="4" fill="var(--accent-primary)" class="hero__chart-dot" />
              </svg>
            </div>
            <div class="hero__chart-footer">
              <span>1D</span>
              <span class="hero__chart-footer--active">1S</span>
              <span>1M</span>
              <span>3M</span>
              <span>1A</span>
            </div>
          </div>

          <div class="hero__float-card hero__float-card--1 glass">
            <div class="hero__float-icon hero__float-icon--green">↑</div>
            <div>
              <span class="hero__float-label">BBB4</span>
              <span class="hero__float-value hero__float-value--green">+3.10%</span>
            </div>
          </div>

          <div class="hero__float-card hero__float-card--2 glass">
            <div class="hero__float-icon hero__float-icon--red">↓</div>
            <div>
              <span class="hero__float-label">CCC3</span>
              <span class="hero__float-value hero__float-value--red">-1.12%</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="funcionalidades" class="features">
      <div class="features__container">
        <div class="features__header observe">
          <span class="features__label">Funcionalidades</span>
          <h2 class="features__title">
            Tudo que você precisa para<br />
            <span class="gradient-text">investir com confiança</span>
          </h2>
          <p class="features__subtitle">
            Uma plataforma completa que simula o funcionamento de uma bolsa de valores real,
            com ferramentas intuitivas para gerenciar seus investimentos.
          </p>
        </div>

        <div class="features__grid">
          <div
            v-for="(feature, index) in features"
            :key="feature.title"
            class="features__card glass observe"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="features__card-icon" :class="`features__card-icon--${feature.icon}`">
              <svg v-if="feature.icon === 'wallet'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/>
                <path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/>
                <path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/>
              </svg>
              <svg v-if="feature.icon === 'arrows'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M7 3L7 21M7 3L3 7M7 3L11 7"/>
                <path d="M17 21L17 3M17 21L13 17M17 21L21 17"/>
              </svg>
              <svg v-if="feature.icon === 'chart'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 3v18h18"/>
                <path d="M7 16l4-4 4 2 6-8"/>
              </svg>
              <svg v-if="feature.icon === 'book'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20"/>
                <line x1="8" y1="7" x2="16" y2="7"/>
                <line x1="8" y1="11" x2="13" y2="11"/>
              </svg>
              <svg v-if="feature.icon === 'engine'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3"/>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1Z"/>
              </svg>
              <svg v-if="feature.icon === 'shield'" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                <path d="M9 12l2 2 4-4"/>
              </svg>
            </div>

            <h3 class="features__card-title">{{ feature.title }}</h3>
            <p class="features__card-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <section id="como-funciona" class="how-it-works">
      <div class="how-it-works__container">
        <div class="how-it-works__header observe">
          <span class="how-it-works__label">Como Funciona</span>
          <h2 class="how-it-works__title">
            Comece a investir em<br />
            <span class="gradient-text">poucos passos</span>
          </h2>
        </div>

        <div class="how-it-works__steps">
          <div
            v-for="(step, index) in steps"
            :key="step.number"
            class="how-it-works__step observe"
            :style="{ animationDelay: `${index * 0.15}s` }"
          >
            <div class="how-it-works__step-number">
              {{ step.number }}
            </div>
            <h3 class="how-it-works__step-title">{{ step.title }}</h3>
            <p class="how-it-works__step-description">{{ step.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <section class="cta-final">
      <div class="cta-final__container observe">
        <div class="cta-final__glow" aria-hidden="true"></div>
        <h2 class="cta-final__title">
          Pronto para começar a<br />
          <span class="gradient-text">negociar?</span>
        </h2>
        <p class="cta-final__subtitle">
          Crie sua conta agora e receba R$ 10.000,00 em capital inicial
          para simular suas operações na bolsa de valores.
        </p>
        <button id="btn-criar-conta" class="cta-final__btn">
          Criar Conta Gratuita
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M3 8H13M13 8L9 4M13 8L9 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </section>

    <footer class="footer">
      <div class="footer__container">
        <div class="footer__logo">
          <svg width="20" height="20" viewBox="0 0 28 28" fill="none">
            <rect x="2" y="16" width="4" height="10" rx="1" fill="currentColor" opacity="0.5"/>
            <rect x="8" y="10" width="4" height="16" rx="1" fill="currentColor" opacity="0.7"/>
            <rect x="14" y="6" width="4" height="20" rx="1" fill="currentColor" opacity="0.85"/>
            <rect x="20" y="2" width="4" height="24" rx="1" fill="currentColor"/>
          </svg>
          <span>Home Broker</span>
        </div>
        <p class="footer__text">
          Projeto acadêmico — Sistemas Distribuídos. Simulação de bolsa de valores.
        </p>
      </div>
    </footer>
  </main>
</template>

<style scoped>
.ticker {
  position: fixed;
  top: 68px;
  left: 0;
  right: 0;
  z-index: 900;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-subtle);
  padding: 8px 0;
  overflow: hidden;
}

.ticker__track {
  display: flex;
  width: max-content;
  animation: ticker 30s linear infinite;
}

.ticker__content {
  display: flex;
  gap: 40px;
  padding: 0 20px;
}

.ticker__item {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
}

.ticker__symbol {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 0.05em;
}

.ticker__price {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-secondary);
}

.ticker__change {
  font-size: 0.7rem;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
}

.ticker__change--up {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.ticker__change--down {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.home {
  position: relative;
  overflow: hidden;
}

.home__orbs {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.home__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  animation: pulseGlow 6s ease-in-out infinite;
}

.home__orb--1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.12), transparent);
  top: -100px;
  right: -100px;
}

.home__orb--2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(20, 184, 166, 0.08), transparent);
  bottom: 20%;
  left: -150px;
  animation-delay: 2s;
}

.home__orb--3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(6, 182, 212, 0.06), transparent);
  top: 50%;
  right: 10%;
  animation-delay: 4s;
}

.hero {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  align-items: center;
  padding: 140px 24px 80px;
}

.hero__container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

.hero__badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--accent-primary);
  background: var(--accent-glow);
  border: 1px solid var(--border-accent);
  border-radius: var(--radius-full);
  margin-bottom: 24px;
}

.hero__badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent-primary);
  animation: pulseGlow 2s ease-in-out infinite;
}

.hero__title {
  font-size: clamp(2.2rem, 5vw, 3.5rem);
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.03em;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.hero__subtitle {
  font-size: 1.05rem;
  line-height: 1.7;
  color: var(--text-secondary);
  max-width: 500px;
  margin-bottom: 32px;
}

.hero__buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 48px;
}

.hero__btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: none;
}

.hero__btn--primary {
  background: var(--accent-primary);
  color: var(--bg-primary);
  box-shadow: 0 0 30px var(--accent-glow);
}

.hero__btn--primary:hover {
  background: #0ea472;
  transform: translateY(-2px);
  box-shadow: 0 4px 40px var(--accent-glow-strong);
}

.hero__btn--outline {
  background: transparent;
  color: var(--text-primary);
  border: 1px solid var(--border-subtle);
}

.hero__btn--outline:hover {
  border-color: var(--border-accent);
  background: var(--accent-glow);
}

/* Hero Stats */
.hero__stats {
  display: flex;
  align-items: center;
  gap: 24px;
}

.hero__stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hero__stat-number {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--accent-primary);
}

.hero__stat-label {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.hero__stat-divider {
  width: 1px;
  height: 36px;
  background: var(--border-subtle);
}

/* Hero Visual / Chart Card */
.hero__visual {
  position: relative;
}

.hero__chart-card {
  border-radius: var(--radius-lg);
  padding: 24px;
  transition: all var(--transition-smooth);
}

.hero__chart-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 40px rgba(16, 185, 129, 0.08);
}

.hero__chart-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.hero__chart-symbol {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  display: block;
}

.hero__chart-name {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.hero__chart-price-group {
  text-align: right;
}

.hero__chart-price {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--text-primary);
  display: block;
}

.hero__chart-change {
  font-size: 0.8rem;
  font-weight: 600;
}

.hero__chart-change--up {
  color: var(--accent-primary);
}

.hero__chart-graph {
  margin-bottom: 16px;
}

.hero__chart-svg {
  width: 100%;
  height: 120px;
}

.hero__chart-line {
  stroke-dasharray: 1000;
  animation: chartLine 2s ease forwards;
}

.hero__chart-area {
  opacity: 0;
  animation: fadeIn 1.5s ease 0.5s forwards;
}

.hero__chart-dot {
  opacity: 0;
  animation: fadeIn 0.3s ease 2s forwards;
}

.hero__chart-footer {
  display: flex;
  justify-content: center;
  gap: 20px;
  font-size: 0.75rem;
  color: var(--text-muted);
}

.hero__chart-footer--active {
  color: var(--accent-primary);
  font-weight: 600;
}

.hero__float-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  animation: float 4s ease-in-out infinite;
}

.hero__float-card--1 {
  top: -20px;
  right: -30px;
  animation-delay: 0s;
}

.hero__float-card--2 {
  bottom: 20px;
  left: -40px;
  animation-delay: 2s;
}

.hero__float-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-sm);
  font-weight: 700;
  font-size: 0.85rem;
}

.hero__float-icon--green {
  background: rgba(16, 185, 129, 0.15);
  color: var(--accent-primary);
}

.hero__float-icon--red {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.hero__float-label {
  display: block;
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-primary);
}

.hero__float-value {
  font-size: 0.75rem;
  font-weight: 600;
}

.hero__float-value--green { color: var(--accent-primary); }
.hero__float-value--red { color: #ef4444; }

.features {
  position: relative;
  z-index: 1;
  padding: 100px 24px;
}

.features__container {
  max-width: 1200px;
  margin: 0 auto;
}

.features__header {
  text-align: center;
  max-width: 600px;
  margin: 0 auto 60px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.7s var(--transition-smooth);
}

.features__header.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.features__label {
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--accent-primary);
  margin-bottom: 16px;
}

.features__title {
  font-size: clamp(1.8rem, 4vw, 2.5rem);
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.02em;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.features__subtitle {
  font-size: 1rem;
  color: var(--text-secondary);
  line-height: 1.7;
}

.features__grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.features__card {
  padding: 32px 28px;
  border-radius: var(--radius-lg);
  transition: all var(--transition-smooth);
  opacity: 0;
  transform: translateY(30px);
}

.features__card.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.features__card:hover {
  transform: translateY(-6px) !important;
  box-shadow: 0 8px 40px rgba(16, 185, 129, 0.08);
}

.features__card-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  background: var(--accent-glow);
  color: var(--accent-primary);
  margin-bottom: 20px;
  transition: all var(--transition-fast);
}

.features__card:hover .features__card-icon {
  background: var(--accent-glow-strong);
  transform: scale(1.05);
}

.features__card-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.features__card-description {
  font-size: 0.9rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

.how-it-works {
  position: relative;
  z-index: 1;
  padding: 100px 24px;
}

.how-it-works__container {
  max-width: 1200px;
  margin: 0 auto;
}

.how-it-works__header {
  text-align: center;
  max-width: 500px;
  margin: 0 auto 60px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.7s var(--transition-smooth);
}

.how-it-works__header.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.how-it-works__label {
  display: inline-block;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: var(--accent-primary);
  margin-bottom: 16px;
}

.how-it-works__title {
  font-size: clamp(1.8rem, 4vw, 2.5rem);
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.02em;
  color: var(--text-primary);
}

.how-it-works__steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.how-it-works__step {
  position: relative;
  text-align: center;
  padding: 32px 16px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.7s var(--transition-smooth);
}

.how-it-works__step.is-visible {
  opacity: 1;
  transform: translateY(0);
}


.how-it-works__step-number {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 1.1rem;
  font-weight: 800;
  color: var(--accent-primary);
  background: var(--accent-glow);
  border: 2px solid var(--border-accent);
  border-radius: var(--radius-full);
  transition: all var(--transition-fast);
}

.how-it-works__step:hover .how-it-works__step-number {
  background: var(--accent-primary);
  color: var(--bg-primary);
  transform: scale(1.1);
  box-shadow: 0 0 30px var(--accent-glow-strong);
}

.how-it-works__step-title {
  font-size: 1.05rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.how-it-works__step-description {
  font-size: 0.85rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

.cta-final {
  position: relative;
  z-index: 1;
  padding: 100px 24px;
}

.cta-final__container {
  max-width: 700px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  padding: 60px 40px;
  border-radius: var(--radius-xl);
  background: var(--bg-card);
  backdrop-filter: blur(var(--glass-blur));
  border: 1px solid var(--border-subtle);
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.7s var(--transition-smooth);
}

.cta-final__container.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.cta-final__glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-glow-strong), transparent);
  border-radius: 50%;
  filter: blur(60px);
  pointer-events: none;
}

.cta-final__title {
  font-size: clamp(1.8rem, 4vw, 2.4rem);
  font-weight: 800;
  line-height: 1.2;
  color: var(--text-primary);
  margin-bottom: 16px;
  position: relative;
}

.cta-final__subtitle {
  font-size: 1rem;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 32px;
  position: relative;
}

.cta-final__btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 16px 32px;
  font-size: 1rem;
  font-weight: 700;
  color: var(--bg-primary);
  background: var(--accent-primary);
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 0 30px var(--accent-glow);
  position: relative;
}

.cta-final__btn:hover {
  background: #0ea472;
  transform: translateY(-2px);
  box-shadow: 0 4px 40px var(--accent-glow-strong);
}

/* ===== FOOTER ===== */
.footer {
  position: relative;
  z-index: 1;
  padding: 32px 24px;
  border-top: 1px solid var(--border-subtle);
}

.footer__container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.footer__logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--accent-primary);
  font-weight: 600;
  font-size: 0.9rem;
}

.footer__text {
  font-size: 0.8rem;
  color: var(--text-muted);
}

@media (max-width: 1024px) {
  .hero__container {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .hero__visual {
    max-width: 500px;
    margin: 0 auto;
  }

  .features__grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .how-it-works__steps {
    grid-template-columns: repeat(2, 1fr);
  }

}

@media (max-width: 640px) {
  .hero {
    padding-top: 120px;
  }

  .hero__buttons {
    flex-direction: column;
  }

  .hero__btn {
    justify-content: center;
  }

  .hero__stats {
    flex-wrap: wrap;
    gap: 16px;
  }

  .hero__stat-divider {
    display: none;
  }

  .hero__float-card--1 {
    right: -10px;
    top: -10px;
  }

  .hero__float-card--2 {
    left: -10px;
  }

  .features__grid {
    grid-template-columns: 1fr;
  }

  .how-it-works__steps {
    grid-template-columns: 1fr;
  }

  .footer__container {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .ticker {
    top: 56px;
  }
}
</style>
