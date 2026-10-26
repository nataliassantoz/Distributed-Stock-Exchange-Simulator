<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useOrdemStore } from '@/stores/ordemStore'

const router = useRouter()
const authStore = useAuthStore()
const ordemStore = useOrdemStore()

const nomeUsuario = computed(() => authStore.nomeUsuario || 'Investidor')
const primeiroNome = computed(() => nomeUsuario.value.split(' ')[0])

// ── Formulário ──────────────────────────────────────────────────────────────
const tipo = ref('COMPRA')
const ativoSelecionado = ref('')
const quantidade = ref('')
const preco = ref('')

// ── Estados de UI ────────────────────────────────────────────────────────────
const isSubmitting = ref(false)
const successData = ref(null)
const formError = ref('')

// ── Ativos disponíveis (carregados via store / mock) ────────────────────────
const ativos = computed(() => ordemStore.ativos)
const loadingAtivos = computed(() => ordemStore.loadingAtivos)

// ── Validação ────────────────────────────────────────────────────────────────
const erros = ref({ ativo: '', quantidade: '', preco: '' })

function validarCampo(campo) {
  erros.value[campo] = ''
  if (campo === 'ativo' && !ativoSelecionado.value) {
    erros.value.ativo = 'Selecione um ativo.'
  }
  if (campo === 'quantidade') {
    const q = Number(quantidade.value)
    if (!quantidade.value || isNaN(q) || q <= 0 || !Number.isInteger(q)) {
      erros.value.quantidade = 'Informe uma quantidade inteira e positiva.'
    }
  }
  if (campo === 'preco') {
    const p = Number(preco.value)
    if (!preco.value || isNaN(p) || p <= 0) {
      erros.value.preco = 'Informe um preço válido e positivo.'
    }
  }
}

function validarTudo() {
  validarCampo('ativo')
  validarCampo('quantidade')
  validarCampo('preco')
  return !erros.value.ativo && !erros.value.quantidade && !erros.value.preco
}

// ── Preço sugerido ao selecionar ativo ──────────────────────────────────────
function onAtivoChange() {
  erros.value.ativo = ''
  const ativo = ativos.value.find((a) => a.ticker === ativoSelecionado.value)
  if (ativo) {
    preco.value = ativo.precoAtual
  }
}

// ── Envio da ordem ───────────────────────────────────────────────────────────
async function enviarOrdem() {
  formError.value = ''
  if (!validarTudo()) return

  isSubmitting.value = true
  try {
    const resultado = await ordemStore.enviarOrdem({
      ativo: ativoSelecionado.value,
      tipo: tipo.value,
      quantidade: Number(quantidade.value),
      preco: Number(preco.value),
    })
    successData.value = resultado
    resetarFormulario()
  } catch {
    formError.value = 'Não foi possível enviar a ordem. Verifique sua conexão e tente novamente.'
  } finally {
    isSubmitting.value = false
  }
}

function resetarFormulario() {
  ativoSelecionado.value = ''
  quantidade.value = ''
  preco.value = ''
  erros.value = { ativo: '', quantidade: '', preco: '' }
}

function novaOrdem() {
  successData.value = null
  ordemStore.limparUltimaOrdem()
}

function voltarAoMenu() {
  router.push('/menu-principal')
}

// ── Formatação ───────────────────────────────────────────────────────────────
function formatarMoeda(valor) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function formatarData(isoString) {
  if (!isoString) return '—'
  return new Date(isoString).toLocaleString('pt-BR')
}

// ── Cores por ticker ─────────────────────────────────────────────────────────
const tickerColors = {
  PETR4: { r: 16,  g: 185, b: 129 },
  VALE3: { r: 59,  g: 130, b: 246 },
  ITUB4: { r: 245, g: 158, b: 11  },
  WEGE3: { r: 139, g: 92,  b: 246 },
  BBAS3: { r: 236, g: 72,  b: 153 },
  TEC3:  { r: 59,  g: 130, b: 246 },
  ENE3:  { r: 245, g: 158, b: 11  },
  FIN3:  { r: 139, g: 92,  b: 246 },
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

// ── Inicialização ────────────────────────────────────────────────────────────
onMounted(() => {
  ordemStore.carregarAtivos()
})
</script>

<template>
  <div class="ordens-page">
    <div class="ordens-bg" aria-hidden="true">
      <div class="ordens-bg__orb ordens-bg__orb--1"></div>
      <div class="ordens-bg__orb ordens-bg__orb--2"></div>
      <div class="ordens-bg__orb ordens-bg__orb--3"></div>
      <div class="grid-lines"></div>
    </div>

    <main class="ordens-main">
      <!-- Cabeçalho -->
      <header class="ordens-header animate-fade-in-up">
        <div class="header-row">
          <div class="header-left">
            <button class="btn-voltar" @click="voltarAoMenu">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
              </svg>
              Voltar
            </button>
            <h1 class="page-title">
              Enviar <span class="gradient-text">Ordem</span>
            </h1>
            <p class="page-subtitle">
              Registre ordens de compra e venda de ativos no mercado.
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

      <!-- Tela de Sucesso -->
      <transition name="fade-slide" mode="out-in">
        <div v-if="successData" class="success-container animate-fade-in-up delay-100" key="success">
          <div class="success-card">
            <div class="success-icon">
              <svg width="32" height="32" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 class="success-title">Ordem Enviada!</h2>
            <p class="success-desc">{{ successData.mensagem || 'Sua ordem foi registrada com sucesso.' }}</p>

            <div class="success-details">
              <div class="detail-row">
                <span class="detail-label">ID da Ordem</span>
                <span class="detail-value detail-value--mono">{{ successData.id || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Ativo</span>
                <span class="detail-value">
                  <span class="detail-badge" :style="badgeStyle(successData.ativo)">
                    {{ successData.ativo || '—' }}
                  </span>
                </span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Tipo</span>
                <span
                  class="detail-value detail-tipo"
                  :class="successData.tipo === 'COMPRA' ? 'detail-tipo--compra' : 'detail-tipo--venda'"
                >
                  {{ successData.tipo || '—' }}
                </span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Quantidade</span>
                <span class="detail-value">{{ successData.quantidade }} ações</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Preço</span>
                <span class="detail-value">{{ formatarMoeda(successData.preco) }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Status</span>
                <span class="detail-value detail-status">{{ successData.status || 'PENDENTE' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Registrado em</span>
                <span class="detail-value">{{ formatarData(successData.criadoEm) }}</span>
              </div>
            </div>

            <button class="btn-nova-ordem" @click="novaOrdem">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              Nova Ordem
            </button>
          </div>
        </div>

        <!-- Formulário -->
        <div v-else class="form-container animate-fade-in-up delay-100" key="form">
          <!-- Seleção de tipo -->
          <section class="section-tipo animate-fade-in-up delay-100">
            <div class="section-header">
              <h2 class="section-title">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M7 16V4m0 0L3 8m4-4l4 4m6 0v12m0 0l4-4m-4 4l-4-4" />
                </svg>
                Tipo de Ordem
              </h2>
            </div>
            <div class="tipo-selector">
              <button
                id="btn-tipo-compra"
                class="tipo-btn"
                :class="{ 'tipo-btn--active-compra': tipo === 'COMPRA' }"
                @click="tipo = 'COMPRA'"
              >
                <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span>Compra</span>
              </button>
              <button
                id="btn-tipo-venda"
                class="tipo-btn"
                :class="{ 'tipo-btn--active-venda': tipo === 'VENDA' }"
                @click="tipo = 'VENDA'"
              >
                <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M20 8l-4.586 4.586a2 2 0 01-2.828 0L8 8m2 2L8.414 11.586a2 2 0 01-2.828 0L2 10m6 6h.01M18 4H6a2 2 0 00-2 2v12a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2z" />
                </svg>
                <span>Venda</span>
              </button>
            </div>
          </section>

          <!-- Formulário de campos -->
          <section class="section-form animate-fade-in-up delay-200">
            <div class="section-header">
              <h2 class="section-title">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
                Dados da Ordem
              </h2>
            </div>

            <div class="form-card">
              <form id="form-enviar-ordem" class="ordem-form" @submit.prevent="enviarOrdem" novalidate>

                <!-- Campo: Ativo -->
                <div class="form-field" :class="{ 'form-field--error': erros.ativo }">
                  <label class="field-label" for="campo-ativo">Ativo</label>
                  <div class="select-wrapper">
                    <div v-if="loadingAtivos" class="select-loading">
                      <div class="spinner-sm"></div>
                      <span>Carregando ativos...</span>
                    </div>
                    <select
                      v-else
                      id="campo-ativo"
                      v-model="ativoSelecionado"
                      class="field-select"
                      @change="onAtivoChange"
                      @blur="validarCampo('ativo')"
                    >
                      <option value="" disabled>Selecione o ativo</option>
                      <option
                        v-for="ativo in ativos"
                        :key="ativo.id"
                        :value="ativo.ticker"
                      >
                        {{ ativo.ticker }} — {{ ativo.nome }}
                      </option>
                    </select>
                    <svg class="select-icon" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                    </svg>
                  </div>
                  <transition name="fade-error">
                    <span v-if="erros.ativo" class="field-error">{{ erros.ativo }}</span>
                  </transition>

                  <!-- Preview do ativo selecionado -->
                  <transition name="fade-slide">
                    <div v-if="ativoSelecionado" class="ativo-preview">
                      <span class="ativo-badge" :style="badgeStyle(ativoSelecionado)">
                        {{ ativoSelecionado.substring(0, 2) }}
                      </span>
                      <div>
                        <span class="ativo-ticker-preview">{{ ativoSelecionado }}</span>
                        <span class="ativo-preco-hint">
                          Preço atual: {{ formatarMoeda(ativos.find(a => a.ticker === ativoSelecionado)?.precoAtual || 0) }}
                        </span>
                      </div>
                    </div>
                  </transition>
                </div>

                <!-- Campo: Quantidade -->
                <div class="form-field" :class="{ 'form-field--error': erros.quantidade }">
                  <label class="field-label" for="campo-quantidade">Quantidade</label>
                  <div class="input-wrapper">
                    <input
                      id="campo-quantidade"
                      v-model="quantidade"
                      type="number"
                      min="1"
                      step="1"
                      class="field-input"
                      placeholder="Ex.: 100"
                      @blur="validarCampo('quantidade')"
                      @input="validarCampo('quantidade')"
                    />
                    <span class="input-suffix">ações</span>
                  </div>
                  <transition name="fade-error">
                    <span v-if="erros.quantidade" class="field-error">{{ erros.quantidade }}</span>
                  </transition>
                </div>

                <!-- Campo: Preço -->
                <div class="form-field" :class="{ 'form-field--error': erros.preco }">
                  <label class="field-label" for="campo-preco">Preço por ação</label>
                  <div class="input-wrapper">
                    <span class="input-prefix">R$</span>
                    <input
                      id="campo-preco"
                      v-model="preco"
                      type="number"
                      min="0.01"
                      step="0.01"
                      class="field-input field-input--with-prefix"
                      placeholder="0,00"
                      @blur="validarCampo('preco')"
                      @input="validarCampo('preco')"
                    />
                  </div>
                  <transition name="fade-error">
                    <span v-if="erros.preco" class="field-error">{{ erros.preco }}</span>
                  </transition>
                </div>

                <!-- Resumo financeiro -->
                <transition name="fade-slide">
                  <div v-if="quantidade && preco && Number(quantidade) > 0 && Number(preco) > 0" class="resumo-financeiro">
                    <div class="resumo-row">
                      <span class="resumo-label">Total estimado</span>
                      <span class="resumo-valor" :class="tipo === 'COMPRA' ? 'resumo-valor--compra' : 'resumo-valor--venda'">
                        {{ formatarMoeda(Number(quantidade) * Number(preco)) }}
                      </span>
                    </div>
                    <p class="resumo-hint">
                      {{ tipo === 'COMPRA' ? '💡 Verifique seu saldo disponível antes de confirmar.' : '💡 Certifique-se de ter os ativos em carteira.' }}
                    </p>
                  </div>
                </transition>

                <!-- Erro geral -->
                <transition name="fade-error">
                  <div v-if="formError" class="form-error-banner">
                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
                    </svg>
                    {{ formError }}
                  </div>
                </transition>

                <!-- Botão de envio -->
                <button
                  id="btn-enviar-ordem"
                  type="submit"
                  class="btn-enviar"
                  :class="tipo === 'COMPRA' ? 'btn-enviar--compra' : 'btn-enviar--venda'"
                  :disabled="isSubmitting"
                >
                  <template v-if="isSubmitting">
                    <div class="spinner-sm"></div>
                    Enviando...
                  </template>
                  <template v-else>
                    <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
                    </svg>
                    Enviar Ordem de {{ tipo === 'COMPRA' ? 'Compra' : 'Venda' }}
                  </template>
                </button>
              </form>
            </div>
          </section>

          <!-- Informativo de riscos -->
          <section class="section-info animate-fade-in-up delay-300">
            <div class="info-card">
              <div class="info-icon">
                <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div>
                <p class="info-title">Como funciona?</p>
                <p class="info-text">
                  Sua ordem será enviada ao <strong>Matching Engine</strong> distribuído. Ordens de compra e venda com preços compatíveis são executadas automaticamente.
                  Enquanto não executada, a ordem permanece com status <em>PENDENTE</em>.
                </p>
              </div>
            </div>
          </section>
        </div>
      </transition>
    </main>
  </div>
</template>

<style scoped>
/* ── Estrutura da página ───────────────────────────────────────────────── */
.ordens-page {
  min-height: 100vh;
  background-color: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.ordens-main {
  position: relative;
  z-index: 10;
  max-width: 760px;
  margin: 0 auto;
  padding: 88px 40px 64px;
}

/* ── Background decorativo (mesmo padrão do CarteiraView) ─────────────── */
.ordens-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.ordens-bg__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.ordens-bg__orb--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -150px;
  background: radial-gradient(circle, rgba(6,182,212,0.08) 0%, transparent 70%);
  animation: pulseGlow 6s ease-in-out infinite;
}

.ordens-bg__orb--2 {
  width: 450px;
  height: 450px;
  bottom: -120px;
  left: -120px;
  background: radial-gradient(circle, rgba(16,185,129,0.06) 0%, transparent 70%);
  animation: pulseGlow 8s ease-in-out infinite reverse;
}

.ordens-bg__orb--3 {
  width: 300px;
  height: 300px;
  top: 45%;
  left: 5%;
  background: radial-gradient(circle, rgba(139,92,246,0.04) 0%, transparent 70%);
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

/* ── Cabeçalho ────────────────────────────────────────────────────────── */
.ordens-header {
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

.header-right {
  flex-shrink: 0;
  padding-top: 40px;
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

.gradient-text {
  background: linear-gradient(135deg, #06b6d4, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  font-size: 1rem;
  color: var(--text-secondary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px 6px 6px;
  border-radius: 9999px;
  background: rgba(6,182,212,0.08);
  border: 1px solid rgba(6,182,212,0.2);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #06b6d4, #8b5cf6);
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

/* ── Seções ────────────────────────────────────────────────────────────── */
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

/* ── Seleção de tipo ───────────────────────────────────────────────────── */
.section-tipo {
  margin-bottom: 24px;
}

.tipo-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.tipo-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 18px 24px;
  border-radius: 16px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1.5px solid var(--border-subtle);
  color: var(--text-secondary);
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.25s ease;
}

.tipo-btn:hover {
  border-color: var(--border-accent);
  color: var(--text-primary);
  transform: translateY(-2px);
}

.tipo-btn--active-compra {
  background: rgba(16,185,129,0.08);
  border-color: rgba(16,185,129,0.4);
  color: #34d399;
  box-shadow: 0 0 24px rgba(16,185,129,0.12);
}

.tipo-btn--active-venda {
  background: rgba(239,68,68,0.08);
  border-color: rgba(239,68,68,0.4);
  color: #f87171;
  box-shadow: 0 0 24px rgba(239,68,68,0.12);
}

/* ── Formulário ────────────────────────────────────────────────────────── */
.section-form {
  margin-bottom: 24px;
}

.form-card {
  border-radius: 20px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--border-subtle);
  padding: 32px;
}

.ordem-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ── Campos ────────────────────────────────────────────────────────────── */
.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-prefix {
  position: absolute;
  left: 16px;
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-muted);
  pointer-events: none;
  z-index: 1;
}

.input-suffix {
  position: absolute;
  right: 16px;
  font-size: 0.8rem;
  color: var(--text-muted);
  pointer-events: none;
}

.field-input {
  width: 100%;
  padding: 14px 16px;
  border-radius: 12px;
  background: rgba(148,163,184,0.05);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  font-size: 0.95rem;
  font-family: inherit;
  transition: all 0.25s ease;
  outline: none;
  -moz-appearance: textfield;
}

.field-input::-webkit-outer-spin-button,
.field-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.field-input--with-prefix {
  padding-left: 44px;
}

.field-input:focus {
  border-color: rgba(6,182,212,0.5);
  background: rgba(6,182,212,0.04);
  box-shadow: 0 0 0 3px rgba(6,182,212,0.1);
}

.form-field--error .field-input,
.form-field--error .field-select {
  border-color: rgba(239,68,68,0.4);
  background: rgba(239,68,68,0.04);
}

/* ── Select ────────────────────────────────────────────────────────────── */
.select-wrapper {
  position: relative;
}

.field-select {
  width: 100%;
  padding: 14px 44px 14px 16px;
  border-radius: 12px;
  background: rgba(148,163,184,0.05);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  font-size: 0.95rem;
  font-family: inherit;
  appearance: none;
  cursor: pointer;
  transition: all 0.25s ease;
  outline: none;
}

.field-select:focus {
  border-color: rgba(6,182,212,0.5);
  background: rgba(6,182,212,0.04);
  box-shadow: 0 0 0 3px rgba(6,182,212,0.1);
}

.select-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  pointer-events: none;
}

.select-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-radius: 12px;
  background: rgba(148,163,184,0.05);
  border: 1px solid var(--border-subtle);
  color: var(--text-muted);
  font-size: 0.9rem;
}

/* ── Preview do ativo ─────────────────────────────────────────────────── */
.ativo-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  background: rgba(6,182,212,0.04);
  border: 1px solid rgba(6,182,212,0.12);
}

.ativo-badge {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: 700;
  flex-shrink: 0;
}

.ativo-ticker-preview {
  display: block;
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-primary);
}

.ativo-preco-hint {
  display: block;
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-top: 2px;
}

/* ── Erros de campo ────────────────────────────────────────────────────── */
.field-error {
  font-size: 0.78rem;
  color: #f87171;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* ── Resumo financeiro ─────────────────────────────────────────────────── */
.resumo-financeiro {
  padding: 16px 20px;
  border-radius: 12px;
  background: rgba(6,182,212,0.04);
  border: 1px solid rgba(6,182,212,0.12);
}

.resumo-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.resumo-label {
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.resumo-valor {
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.01em;
}

.resumo-valor--compra {
  background: linear-gradient(135deg, #34d399, #06b6d4);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.resumo-valor--venda {
  background: linear-gradient(135deg, #f87171, #f43f5e);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.resumo-hint {
  font-size: 0.75rem;
  color: var(--text-muted);
  line-height: 1.5;
}

/* ── Erro geral ────────────────────────────────────────────────────────── */
.form-error-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-radius: 10px;
  background: rgba(239,68,68,0.06);
  border: 1px solid rgba(239,68,68,0.2);
  color: #f87171;
  font-size: 0.875rem;
}

/* ── Botão de envio ───────────────────────────────────────────────────── */
.btn-enviar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 16px 24px;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 700;
  font-family: inherit;
  border: none;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-top: 4px;
}

.btn-enviar:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.btn-enviar--compra {
  background: linear-gradient(135deg, #10b981, #06b6d4);
  color: #ffffff;
  box-shadow: 0 0 24px rgba(16,185,129,0.3);
}

.btn-enviar--compra:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 0 36px rgba(16,185,129,0.45);
}

.btn-enviar--venda {
  background: linear-gradient(135deg, #ef4444, #f43f5e);
  color: #ffffff;
  box-shadow: 0 0 24px rgba(239,68,68,0.25);
}

.btn-enviar--venda:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 0 36px rgba(239,68,68,0.4);
}

/* ── Spinner ────────────────────────────────────────────────────────────── */
.spinner-sm {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  flex-shrink: 0;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Informativo ────────────────────────────────────────────────────────── */
.section-info {
  margin-top: 8px;
}

.info-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px 20px;
  border-radius: 14px;
  background: rgba(139,92,246,0.04);
  border: 1px solid rgba(139,92,246,0.12);
}

.info-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(139,92,246,0.1);
  border: 1px solid rgba(139,92,246,0.2);
  color: #a78bfa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-title {
  font-size: 0.8rem;
  font-weight: 700;
  color: #a78bfa;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}

.info-text {
  font-size: 0.82rem;
  color: var(--text-muted);
  line-height: 1.6;
}

.info-text strong {
  color: var(--text-secondary);
}

.info-text em {
  color: #a78bfa;
  font-style: normal;
  font-weight: 600;
}

/* ── Tela de sucesso ───────────────────────────────────────────────────── */
.success-container {
  display: flex;
  justify-content: center;
}

.success-card {
  width: 100%;
  max-width: 540px;
  border-radius: 20px;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(16,185,129,0.25);
  padding: 40px 36px;
  text-align: center;
  box-shadow: 0 0 60px rgba(16,185,129,0.08);
}

.success-icon {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  background: rgba(16,185,129,0.1);
  border: 1px solid rgba(16,185,129,0.25);
  color: #34d399;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.success-title {
  font-size: 1.75rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin-bottom: 8px;
}

.success-desc {
  font-size: 0.9rem;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 28px;
}

.success-details {
  background: rgba(148,163,184,0.04);
  border: 1px solid var(--border-subtle);
  border-radius: 14px;
  padding: 20px;
  text-align: left;
  margin-bottom: 28px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.detail-label {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  flex-shrink: 0;
}

.detail-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
  text-align: right;
}

.detail-value--mono {
  font-family: 'Courier New', monospace;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.detail-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 700;
}

.detail-tipo {
  padding: 2px 12px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.detail-tipo--compra {
  background: rgba(16,185,129,0.1);
  color: #34d399;
  border: 1px solid rgba(16,185,129,0.25);
}

.detail-tipo--venda {
  background: rgba(239,68,68,0.1);
  color: #f87171;
  border: 1px solid rgba(239,68,68,0.25);
}

.detail-status {
  background: rgba(245,158,11,0.1);
  color: #fbbf24;
  border: 1px solid rgba(245,158,11,0.25);
  padding: 2px 10px;
  border-radius: 9999px;
  font-size: 0.75rem;
  letter-spacing: 0.04em;
}

.btn-nova-ordem {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  border-radius: 9999px;
  background: linear-gradient(135deg, #06b6d4, #8b5cf6);
  color: #ffffff;
  font-size: 0.9rem;
  font-weight: 700;
  font-family: inherit;
  border: none;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 0 24px rgba(6,182,212,0.25);
}

.btn-nova-ordem:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 36px rgba(6,182,212,0.4);
}

/* ── Transições ─────────────────────────────────────────────────────────── */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.35s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}

.fade-error-enter-active,
.fade-error-leave-active {
  transition: all 0.2s ease;
}

.fade-error-enter-from,
.fade-error-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* ── Animações globais (mesmas das outras views) ────────────────────────── */
@keyframes pulseGlow {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.05); }
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease both;
}

.delay-100 { animation-delay: 0.1s; }
.delay-200 { animation-delay: 0.2s; }
.delay-300 { animation-delay: 0.3s; }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ── Responsivo ─────────────────────────────────────────────────────────── */
@media (max-width: 640px) {
  .ordens-main {
    padding: 80px 20px 48px;
  }

  .tipo-selector {
    grid-template-columns: 1fr;
  }

  .form-card {
    padding: 24px 20px;
  }

  .header-row {
    flex-direction: column;
  }

  .header-right {
    padding-top: 0;
  }
}
</style>
