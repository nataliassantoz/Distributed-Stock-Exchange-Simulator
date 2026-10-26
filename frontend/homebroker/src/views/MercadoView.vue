<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useOrdemStore } from '@/stores/ordemStore'

const router = useRouter()
const authStore = useAuthStore()
const ordemStore = useOrdemStore()

const primeiroNome = computed(() => {
  const nome = authStore.nomeUsuario || 'Investidor'
  return nome.split(' ')[0]
})

const ativos = computed(() => ordemStore.ativos)
const loadingAtivos = computed(() => ordemStore.loadingAtivos)

const ativoAtivo = ref(null)

function selecionarAtivo(ativo) {
  ativoAtivo.value = ativo
  ativoSelecionado.value = ativo.ticker
  preco.value = ativo.precoAtual
  erros.value = { ativo: '', quantidade: '', preco: '' }
  gerarCandlesticks(ativo)
}

const candles = ref([])
const chartWidth = 620
const chartHeight = 220
const chartPadding = { top: 16, bottom: 28, left: 12, right: 12 }

function gerarCandlesticks(ativo) {
  const base = ativo ? Number(ativo.precoAtual) : 50
  const result = []
  let prev = base * 0.92
  for (let i = 0; i < 40; i++) {
    const open = prev
    const change = (Math.random() - 0.48) * base * 0.03
    const close = Math.max(0.1, open + change)
    const high = Math.max(open, close) + Math.random() * base * 0.01
    const low = Math.min(open, close) - Math.random() * base * 0.01
    result.push({ open, close, high, low })
    prev = close
  }
  candles.value = result
}

const chartMinPrice = computed(() => {
  if (!candles.value.length) return 0
  return Math.min(...candles.value.map((c) => c.low)) * 0.998
})

const chartMaxPrice = computed(() => {
  if (!candles.value.length) return 100
  return Math.max(...candles.value.map((c) => c.high)) * 1.002
})

function priceToY(price) {
  const range = chartMaxPrice.value - chartMinPrice.value
  if (range === 0) return chartPadding.top
  const ratio = (chartMaxPrice.value - price) / range
  return chartPadding.top + ratio * (chartHeight - chartPadding.top - chartPadding.bottom)
}

const candleWidth = computed(() => {
  const totalW = chartWidth - chartPadding.left - chartPadding.right
  return Math.max(4, totalW / candles.value.length - 2)
})

function candleX(i) {
  const totalW = chartWidth - chartPadding.left - chartPadding.right
  const step = totalW / candles.value.length
  return chartPadding.left + i * step + step / 2
}

const priceLabels = computed(() => {
  const min = chartMinPrice.value
  const max = chartMaxPrice.value
  const count = 4
  return Array.from({ length: count + 1 }, (_, i) => {
    const val = min + ((max - min) * i) / count
    return { y: priceToY(val), label: val.toFixed(2) }
  })
})

const variacaoPct = computed(() => {
  if (!candles.value.length) return 0
  const first = candles.value[0].open
  const last = candles.value[candles.value.length - 1].close
  return ((last - first) / first) * 100
})

const closePath = computed(() => {
  if (!candles.value.length) return ''
  return candles.value
    .map((c, i) => {
      const x = candleX(i)
      const y = priceToY(c.close)
      return `${i === 0 ? 'M' : 'L'}${x},${y}`
    })
    .join(' ')
})

const tipo = ref('COMPRA')
const ativoSelecionado = ref('')
const quantidade = ref('')
const preco = ref('')

const isSubmitting = ref(false)
const successData = ref(null)
const formError = ref('')

const erros = ref({ ativo: '', quantidade: '', preco: '' })

function validarCampo(campo) {
  erros.value[campo] = ''
  if (campo === 'ativo' && !ativoSelecionado.value) {
    erros.value.ativo = 'Selecione um ativo.'
  }
  if (campo === 'quantidade') {
    const q = Number(quantidade.value)
    if (!quantidade.value || isNaN(q) || q <= 0 || !Number.isInteger(q)) {
      erros.value.quantidade = 'Quantidade inteira e positiva.'
    }
  }
  if (campo === 'preco') {
    const p = Number(preco.value)
    if (!preco.value || isNaN(p) || p <= 0) {
      erros.value.preco = 'Preço válido e positivo.'
    }
  }
}

function validarTudo() {
  validarCampo('ativo')
  validarCampo('quantidade')
  validarCampo('preco')
  return !erros.value.ativo && !erros.value.quantidade && !erros.value.preco
}

function onAtivoChange() {
  erros.value.ativo = ''
  const ativo = ativos.value.find((a) => a.ticker === ativoSelecionado.value)
  if (ativo) {
    preco.value = ativo.precoAtual
    selecionarAtivo(ativo)
  }
}

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
    formError.value = 'Não foi possível enviar a ordem. Tente novamente.'
  } finally {
    isSubmitting.value = false
  }
}

function resetarFormulario() {
  quantidade.value = ''
  erros.value = { ativo: '', quantidade: '', preco: '' }
}

function novaOrdem() {
  successData.value = null
  ordemStore.limparUltimaOrdem()
}

function formatarMoeda(valor) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

function formatarData(isoString) {
  if (!isoString) return '—'
  return new Date(isoString).toLocaleString('pt-BR')
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

function tickerColor(ticker) {
  return tickerColors[ticker] || defaultColor
}

function badgeStyle(ticker) {
  const c = tickerColor(ticker)
  return {
    background: `rgba(${c.r},${c.g},${c.b},0.12)`,
    border: `1px solid rgba(${c.r},${c.g},${c.b},0.3)`,
    color: `rgb(${c.r},${c.g},${c.b})`,
  }
}

function rowStyle(ticker) {
  const c = tickerColor(ticker)
  return {
    '--row-accent': `rgba(${c.r},${c.g},${c.b},0.12)`,
    '--row-accent-border': `rgba(${c.r},${c.g},${c.b},0.35)`,
  }
}

const variacoes = ref({})
function gerarVariacoes() {
  const result = {}
  ;['PETR4', 'VALE3', 'ITUB4', 'WEGE3', 'BBAS3', 'TEC3', 'ENE3', 'FIN3'].forEach((t) => {
    result[t] = (Math.random() - 0.45) * 5
  })
  variacoes.value = result
}

let tickInterval = null
onMounted(async () => {
  await ordemStore.carregarAtivos()
  gerarVariacoes()
  if (ativos.value.length > 0) {
    selecionarAtivo(ativos.value[0])
  } else {
    gerarCandlesticks(null)
  }
  tickInterval = setInterval(() => {
    gerarVariacoes()
  }, 3000)
})

onUnmounted(() => {
  clearInterval(tickInterval)
})
</script>

<template>
  <div class="mercado-page">
    <div class="mercado-bg" aria-hidden="true">
      <div class="mercado-bg__orb mercado-bg__orb--1"></div>
      <div class="mercado-bg__orb mercado-bg__orb--2"></div>
      <div class="mercado-bg__orb mercado-bg__orb--3"></div>
      <div class="grid-lines"></div>
    </div>

    <div class="topbar-spacer"></div>

    <div class="market-statusbar">
      <div class="statusbar-inner">
        <div class="statusbar-live">
          <span class="live-dot"></span>
          <span>Mercado Simulado</span>
        </div>
        <div class="statusbar-tickers">
          <span
            v-for="ativo in ativos"
            :key="ativo.ticker"
            class="ticker-chip"
            :class="(variacoes[ativo.ticker] ?? 0) >= 0 ? 'ticker-chip--up' : 'ticker-chip--down'"
          >
            {{ ativo.ticker }}
            <span class="ticker-chip__price">{{ formatarMoeda(ativo.precoAtual) }}</span>
            <span class="ticker-chip__var">
              {{ (variacoes[ativo.ticker] ?? 0) >= 0 ? '▲' : '▼' }}
              {{ Math.abs(variacoes[ativo.ticker] ?? 0).toFixed(2) }}%
            </span>
          </span>
        </div>
        <div class="statusbar-user">
          <div class="user-avatar-sm">{{ primeiroNome.charAt(0).toUpperCase() }}</div>
          <span>{{ primeiroNome }}</span>
        </div>
      </div>
    </div>

    
    <main class="mercado-layout">
      
      <aside class="col-ativos">
        <div class="col-header">
          <h2 class="col-title">
            <svg width="15" height="15" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            Ativos
          </h2>
        </div>

        <div v-if="loadingAtivos" class="ativos-loading">
          <div class="spinner"></div>
          <span>Carregando...</span>
        </div>

        <ul v-else class="ativos-list">
          <li
            v-for="ativo in ativos"
            :key="ativo.ticker"
            class="ativo-row"
            :class="{ 'ativo-row--active': ativoAtivo?.ticker === ativo.ticker }"
            :style="rowStyle(ativo.ticker)"
            @click="selecionarAtivo(ativo)"
          >
            <div class="ativo-row__badge" :style="badgeStyle(ativo.ticker)">
              {{ ativo.ticker.substring(0, 2) }}
            </div>
            <div class="ativo-row__info">
              <span class="ativo-row__ticker">{{ ativo.ticker }}</span>
              <span class="ativo-row__nome">{{ ativo.nome.split(' ')[0] }}</span>
            </div>
            <div class="ativo-row__price-block">
              <span class="ativo-row__price">{{ formatarMoeda(ativo.precoAtual) }}</span>
              <span
                class="ativo-row__var"
                :class="(variacoes[ativo.ticker] ?? 0) >= 0 ? 'var--up' : 'var--down'"
              >
                {{ (variacoes[ativo.ticker] ?? 0) >= 0 ? '▲' : '▼' }}
                {{ Math.abs(variacoes[ativo.ticker] ?? 0).toFixed(2) }}%
              </span>
            </div>
          </li>
        </ul>
      </aside>

      
      <section class="col-chart">
        
        <div class="chart-header" v-if="ativoAtivo">
          <div class="chart-asset-info">
            <span class="chart-asset-badge" :style="badgeStyle(ativoAtivo.ticker)">
              {{ ativoAtivo.ticker }}
            </span>
            <div>
              <h1 class="chart-asset-name">{{ ativoAtivo.nome }}</h1>
              <p class="chart-asset-ticker">{{ ativoAtivo.ticker }} · B3</p>
            </div>
          </div>
          <div class="chart-asset-price-block">
            <span class="chart-price">{{ formatarMoeda(ativoAtivo.precoAtual) }}</span>
            <span
              class="chart-var"
              :class="variacaoPct >= 0 ? 'chart-var--up' : 'chart-var--down'"
            >
              {{ variacaoPct >= 0 ? '▲' : '▼' }} {{ Math.abs(variacaoPct).toFixed(2) }}%
            </span>
          </div>
        </div>
        <div class="chart-header" v-else>
          <p class="chart-empty-hint">Selecione um ativo para visualizar o gráfico</p>
        </div>

        
        <div class="chart-container">
          <svg
            :viewBox="`0 0 ${chartWidth} ${chartHeight}`"
            class="chart-svg"
            xmlns="http://www.w3.org/2000/svg"
            aria-label="Gráfico de candlesticks"
          >
            
            <line
              v-for="label in priceLabels"
              :key="label.label"
              :x1="chartPadding.left"
              :x2="chartWidth - chartPadding.right"
              :y1="label.y"
              :y2="label.y"
              stroke="rgba(148,163,184,0.08)"
              stroke-width="1"
            />

            
            <text
              v-for="label in priceLabels"
              :key="'lbl-' + label.label"
              :x="chartWidth - chartPadding.right + 4"
              :y="label.y + 4"
              class="chart-label"
              fill="rgba(148,163,184,0.5)"
              font-size="8"
              text-anchor="start"
            >
              {{ label.label }}
            </text>

            
            <g v-for="(candle, i) in candles" :key="i">
              
              <line
                :x1="candleX(i)"
                :x2="candleX(i)"
                :y1="priceToY(candle.high)"
                :y2="priceToY(candle.low)"
                :stroke="candle.close >= candle.open ? '#10b981' : '#ef4444'"
                stroke-width="1"
                opacity="0.7"
              />
              
              <rect
                :x="candleX(i) - candleWidth / 2"
                :y="Math.min(priceToY(candle.open), priceToY(candle.close))"
                :width="candleWidth"
                :height="Math.max(1, Math.abs(priceToY(candle.open) - priceToY(candle.close)))"
                :fill="candle.close >= candle.open ? 'rgba(16,185,129,0.75)' : 'rgba(239,68,68,0.75)'"
                rx="1"
              />
            </g>

            
            <path
              v-if="closePath"
              :d="closePath"
              fill="none"
              stroke="rgba(6,182,212,0.35)"
              stroke-width="1"
              stroke-dasharray="3,3"
            />
          </svg>
        </div>

        
        <div class="chart-legend">
          <span class="legend-item legend-item--up">
            <span class="legend-dot legend-dot--up"></span> Alta
          </span>
          <span class="legend-item legend-item--down">
            <span class="legend-dot legend-dot--down"></span> Baixa
          </span>
          <span class="legend-item">
            <span class="legend-dot legend-dot--line"></span> Fechamento
          </span>
          <span class="legend-period">Intraday · simulado</span>
        </div>

        
        <div class="stats-row" v-if="ativoAtivo && candles.length">
          <div class="stat-card">
            <span class="stat-label">Mín.</span>
            <span class="stat-value stat-value--down">{{ formatarMoeda(chartMinPrice) }}</span>
          </div>
          <div class="stat-card">
            <span class="stat-label">Máx.</span>
            <span class="stat-value stat-value--up">{{ formatarMoeda(chartMaxPrice) }}</span>
          </div>
          <div class="stat-card">
            <span class="stat-label">Abertura</span>
            <span class="stat-value">{{ formatarMoeda(candles[0]?.open ?? 0) }}</span>
          </div>
          <div class="stat-card">
            <span class="stat-label">Últ. Fech.</span>
            <span class="stat-value">{{ formatarMoeda(candles[candles.length - 1]?.close ?? 0) }}</span>
          </div>
        </div>
      </section>

      
      <aside class="col-order">
        <div class="col-header">
          <h2 class="col-title">
            <svg width="15" height="15" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            Enviar Ordem
          </h2>
        </div>

        
        <transition name="fade-slide" mode="out-in">
          <div v-if="successData" class="success-panel" key="success">
            <div class="success-icon-wrap">
              <svg width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="success-title">Ordem Enviada!</h3>
            <p class="success-desc">{{ successData.mensagem || 'Registrada com sucesso.' }}</p>

            <div class="success-details">
              <div class="detail-row">
                <span class="detail-label">ID</span>
                <span class="detail-value detail-value--mono">{{ successData.id || '—' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Ativo</span>
                <span class="detail-badge" :style="badgeStyle(successData.ativo)">{{ successData.ativo }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Tipo</span>
                <span
                  class="detail-tipo"
                  :class="successData.tipo === 'COMPRA' ? 'detail-tipo--compra' : 'detail-tipo--venda'"
                >{{ successData.tipo }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Qtd.</span>
                <span class="detail-value">{{ successData.quantidade }} ações</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Preço</span>
                <span class="detail-value">{{ formatarMoeda(successData.preco) }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Status</span>
                <span class="detail-status">{{ successData.status || 'PENDENTE' }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">Em</span>
                <span class="detail-value detail-value--sm">{{ formatarData(successData.criadoEm) }}</span>
              </div>
            </div>

            <button class="btn-nova-ordem" @click="novaOrdem">
              <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4" />
              </svg>
              Nova Ordem
            </button>
          </div>

          
          <div v-else class="order-form-panel" key="form">
            
            <div class="tipo-selector">
              <button
                id="btn-tipo-compra"
                class="tipo-btn"
                :class="{ 'tipo-btn--active-compra': tipo === 'COMPRA' }"
                @click="tipo = 'COMPRA'"
              >
                <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 10l7-7m0 0l7 7m-7-7v18" />
                </svg>
                Compra
              </button>
              <button
                id="btn-tipo-venda"
                class="tipo-btn"
                :class="{ 'tipo-btn--active-venda': tipo === 'VENDA' }"
                @click="tipo = 'VENDA'"
              >
                <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
                </svg>
                Venda
              </button>
            </div>

            <form id="form-enviar-ordem" class="order-form" @submit.prevent="enviarOrdem" novalidate>
              
              <div class="form-field" :class="{ 'form-field--error': erros.ativo }">
                <label class="field-label" for="campo-ativo">Ativo</label>
                <div class="select-wrapper">
                  <div v-if="loadingAtivos" class="select-loading">
                    <div class="spinner-sm"></div>
                  </div>
                  <select
                    v-else
                    id="campo-ativo"
                    v-model="ativoSelecionado"
                    class="field-select"
                    @change="onAtivoChange"
                    @blur="validarCampo('ativo')"
                  >
                    <option value="" disabled>Selecione</option>
                    <option v-for="ativo in ativos" :key="ativo.id" :value="ativo.ticker">
                      {{ ativo.ticker }} — {{ ativo.nome.split(' ')[0] }}
                    </option>
                  </select>
                  <svg class="select-icon" width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
                <transition name="fade-error">
                  <span v-if="erros.ativo" class="field-error">{{ erros.ativo }}</span>
                </transition>
              </div>

              
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

              
              <transition name="fade-slide">
                <div
                  v-if="quantidade && preco && Number(quantidade) > 0 && Number(preco) > 0"
                  class="resumo"
                  :class="tipo === 'COMPRA' ? 'resumo--compra' : 'resumo--venda'"
                >
                  <span class="resumo-label">Total estimado</span>
                  <span class="resumo-valor">{{ formatarMoeda(Number(quantidade) * Number(preco)) }}</span>
                </div>
              </transition>

              
              <transition name="fade-error">
                <div v-if="formError" class="form-error-banner">
                  <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9.303 3.376c.866 1.5-.217 3.374-1.948 3.374H4.645c-1.73 0-2.813-1.874-1.948-3.374L10.051 3.378c.866-1.5 3.032-1.5 3.898 0L22.303 16.126zM12 15.75h.007v.008H12v-.008z" />
                  </svg>
                  {{ formError }}
                </div>
              </transition>

              
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
                  <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
                  </svg>
                  {{ tipo === 'COMPRA' ? 'Comprar' : 'Vender' }}
                </template>
              </button>
            </form>

            
            <div class="order-info">
              <svg width="13" height="13" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>Ordens enviadas ao <strong>Matching Engine</strong> distribuído. Ficam <em>PENDENTE</em> até execução.</span>
            </div>
          </div>
        </transition>
      </aside>
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
  flex-direction: column;
}

.topbar-spacer {
  height: 64px;
  flex-shrink: 0;
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
  width: 600px;
  height: 600px;
  top: -150px;
  right: -100px;
  background: radial-gradient(circle, rgba(16,185,129,0.07) 0%, transparent 70%);
}

.mercado-bg__orb--2 {
  width: 500px;
  height: 500px;
  bottom: -100px;
  left: -100px;
  background: radial-gradient(circle, rgba(6,182,212,0.06) 0%, transparent 70%);
}

.mercado-bg__orb--3 {
  width: 400px;
  height: 400px;
  top: 40%;
  left: 30%;
  background: radial-gradient(circle, rgba(139,92,246,0.04) 0%, transparent 70%);
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.025) 1px, transparent 1px);
  background-size: 60px 60px;
}

.market-statusbar {
  position: relative;
  z-index: 10;
  background: rgba(15,23,42,0.6);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(148,163,184,0.08);
  flex-shrink: 0;
}

.statusbar-inner {
  width: 100%;
  padding: 6px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.statusbar-live {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.7rem;
  font-weight: 600;
  color: #34d399;
  white-space: nowrap;
  flex-shrink: 0;
}

.live-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #34d399;
  animation: pulseDot 2s ease-in-out infinite;
}

@keyframes pulseDot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}

.statusbar-tickers {
  display: flex;
  align-items: center;
  gap: 6px;
  overflow-x: auto;
  flex: 1;
  scrollbar-width: none;
}

.statusbar-tickers::-webkit-scrollbar { display: none; }

.ticker-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 600;
  white-space: nowrap;
  background: rgba(148,163,184,0.06);
  border: 1px solid rgba(148,163,184,0.1);
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.ticker-chip--up .ticker-chip__var { color: #34d399; }
.ticker-chip--down .ticker-chip__var { color: #f87171; }
.ticker-chip__price { color: var(--text-primary); }

.statusbar-user {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.user-avatar-sm {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981, #06b6d4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 700;
  color: #fff;
}

.mercado-layout {
  position: relative;
  z-index: 10;
  flex: 1;
  display: grid;
  grid-template-columns: 260px 1fr 320px;
  gap: 0;
  width: 100%;
  min-height: 0;
}

.col-header {
  padding: 16px 16px 12px;
  border-bottom: 1px solid rgba(148,163,184,0.07);
}

.col-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.7rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.col-ativos {
  border-right: 1px solid rgba(148,163,184,0.07);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ativos-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 40px 16px;
  color: var(--text-muted);
  font-size: 0.8rem;
}

.ativos-list {
  list-style: none;
  overflow-y: auto;
  flex: 1;
  padding: 8px 0;
}

.ativos-list::-webkit-scrollbar { width: 4px; }
.ativos-list::-webkit-scrollbar-track { background: transparent; }
.ativos-list::-webkit-scrollbar-thumb { background: rgba(148,163,184,0.15); border-radius: 4px; }

.ativo-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.ativo-row:hover {
  background: var(--row-accent, rgba(16,185,129,0.06));
}

.ativo-row--active {
  background: var(--row-accent, rgba(16,185,129,0.08)) !important;
  border-left-color: var(--row-accent-border, rgba(16,185,129,0.5)) !important;
}

.ativo-row__badge {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.65rem;
  font-weight: 800;
  flex-shrink: 0;
}

.ativo-row__info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ativo-row__ticker {
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--text-primary);
}

.ativo-row__nome {
  font-size: 0.67rem;
  color: var(--text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ativo-row__price-block {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.ativo-row__price {
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--text-primary);
  white-space: nowrap;
}

.ativo-row__var {
  font-size: 0.65rem;
  font-weight: 600;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.var--up { color: #34d399; }
.var--down { color: #f87171; }

.col-chart {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid rgba(148,163,184,0.07);
}

.chart-header {
  padding: 16px 20px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(148,163,184,0.07);
  gap: 12px;
  flex-shrink: 0;
}

.chart-empty-hint {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.chart-asset-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-asset-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 800;
  letter-spacing: 0.03em;
}

.chart-asset-name {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.chart-asset-ticker {
  font-size: 0.7rem;
  color: var(--text-muted);
}

.chart-asset-price-block {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.chart-price {
  font-size: 1.3rem;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.chart-var {
  font-size: 0.8rem;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 6px;
}

.chart-var--up {
  color: #34d399;
  background: rgba(16,185,129,0.1);
}

.chart-var--down {
  color: #f87171;
  background: rgba(239,68,68,0.1);
}

.chart-container {
  flex: 1;
  padding: 12px 16px 4px;
  min-height: 0;
  display: flex;
  align-items: stretch;
}

.chart-svg {
  width: 100%;
  height: 100%;
  min-height: 160px;
}

.chart-legend {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 6px 20px;
  border-top: 1px solid rgba(148,163,184,0.06);
  flex-shrink: 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.68rem;
  color: var(--text-muted);
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
}

.legend-dot--up { background: rgba(16,185,129,0.75); }
.legend-dot--down { background: rgba(239,68,68,0.75); }
.legend-dot--line {
  background: transparent;
  border-bottom: 2px dashed rgba(6,182,212,0.5);
  width: 16px;
  height: 0;
  border-radius: 0;
}

.legend-period {
  margin-left: auto;
  font-size: 0.65rem;
  color: var(--text-muted);
  opacity: 0.6;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background: rgba(148,163,184,0.07);
  border-top: 1px solid rgba(148,163,184,0.07);
  flex-shrink: 0;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 10px 16px;
  background: var(--bg-primary);
}

.stat-label {
  font-size: 0.65rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-value--up { color: #34d399; }
.stat-value--down { color: #f87171; }

.col-order {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.col-order::-webkit-scrollbar { width: 4px; }
.col-order::-webkit-scrollbar-track { background: transparent; }
.col-order::-webkit-scrollbar-thumb { background: rgba(148,163,184,0.15); border-radius: 4px; }

.success-panel {
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-align: center;
}

.success-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(16,185,129,0.1);
  border: 1px solid rgba(16,185,129,0.25);
  color: #34d399;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-title {
  font-size: 1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.success-desc {
  font-size: 0.78rem;
  color: var(--text-secondary);
  line-height: 1.5;
}

.success-details {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1px;
  background: rgba(148,163,184,0.07);
  border-radius: 10px;
  overflow: hidden;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: var(--bg-card);
  gap: 8px;
}

.detail-label {
  font-size: 0.7rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  flex-shrink: 0;
}

.detail-value {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-primary);
  text-align: right;
}

.detail-value--mono { font-family: 'Courier New', monospace; font-size: 0.7rem; }
.detail-value--sm { font-size: 0.68rem; }

.detail-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.7rem;
  font-weight: 700;
}

.detail-tipo {
  font-size: 0.72rem;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
}

.detail-tipo--compra { color: #34d399; background: rgba(16,185,129,0.1); }
.detail-tipo--venda  { color: #f87171; background: rgba(239,68,68,0.1); }

.detail-status {
  font-size: 0.72rem;
  font-weight: 700;
  color: #fbbf24;
  background: rgba(245,158,11,0.1);
  padding: 2px 8px;
  border-radius: 4px;
}

.btn-nova-ordem {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 8px;
  background: rgba(6,182,212,0.1);
  border: 1px solid rgba(6,182,212,0.25);
  color: #22d3ee;
  font-size: 0.82rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-nova-ordem:hover {
  background: rgba(6,182,212,0.18);
  border-color: rgba(6,182,212,0.45);
}

.order-form-panel {
  padding: 16px 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.tipo-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.tipo-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border-radius: 8px;
  background: var(--bg-card);
  border: 1.5px solid var(--border-subtle);
  color: var(--text-secondary);
  font-size: 0.82rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tipo-btn:hover {
  border-color: var(--border-accent);
  color: var(--text-primary);
}

.tipo-btn--active-compra {
  background: rgba(16,185,129,0.08);
  border-color: rgba(16,185,129,0.45);
  color: #34d399;
  box-shadow: 0 0 16px rgba(16,185,129,0.1);
}

.tipo-btn--active-venda {
  background: rgba(239,68,68,0.08);
  border-color: rgba(239,68,68,0.45);
  color: #f87171;
  box-shadow: 0 0 16px rgba(239,68,68,0.1);
}

.order-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.field-label {
  font-size: 0.68rem;
  font-weight: 700;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.select-wrapper {
  position: relative;
}

.select-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.select-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  pointer-events: none;
}

.field-select {
  width: 100%;
  padding: 10px 32px 10px 12px;
  border-radius: 8px;
  background: rgba(148,163,184,0.05);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  font-size: 0.82rem;
  font-family: inherit;
  appearance: none;
  cursor: pointer;
  transition: all 0.2s ease;
  outline: none;
}

.field-select:focus {
  border-color: rgba(6,182,212,0.4);
  background: rgba(6,182,212,0.04);
  box-shadow: 0 0 0 2px rgba(6,182,212,0.08);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-prefix {
  position: absolute;
  left: 12px;
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-muted);
  pointer-events: none;
  z-index: 1;
}

.input-suffix {
  position: absolute;
  right: 12px;
  font-size: 0.72rem;
  color: var(--text-muted);
  pointer-events: none;
}

.field-input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(148,163,184,0.05);
  border: 1px solid var(--border-subtle);
  color: var(--text-primary);
  font-size: 0.82rem;
  font-family: inherit;
  transition: all 0.2s ease;
  outline: none;
  -moz-appearance: textfield;
}

.field-input::-webkit-outer-spin-button,
.field-input::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }

.field-input--with-prefix { padding-left: 36px; }

.field-input:focus {
  border-color: rgba(6,182,212,0.4);
  background: rgba(6,182,212,0.04);
  box-shadow: 0 0 0 2px rgba(6,182,212,0.08);
}

.form-field--error .field-input,
.form-field--error .field-select {
  border-color: rgba(239,68,68,0.4);
  background: rgba(239,68,68,0.04);
}

.field-error {
  font-size: 0.68rem;
  color: #f87171;
}

.resumo {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
}

.resumo--compra {
  background: rgba(16,185,129,0.07);
  border: 1px solid rgba(16,185,129,0.2);
}

.resumo--venda {
  background: rgba(239,68,68,0.07);
  border: 1px solid rgba(239,68,68,0.2);
}

.resumo-label { font-size: 0.72rem; color: var(--text-muted); }

.resumo--compra .resumo-valor { font-size: 0.88rem; font-weight: 700; color: #34d399; }
.resumo--venda .resumo-valor  { font-size: 0.88rem; font-weight: 700; color: #f87171; }

.form-error-banner {
  display: flex;
  align-items: flex-start;
  gap: 7px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(239,68,68,0.07);
  border: 1px solid rgba(239,68,68,0.25);
  color: #f87171;
  font-size: 0.75rem;
  line-height: 1.4;
}

.btn-enviar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 13px 16px;
  border-radius: 10px;
  font-size: 0.88rem;
  font-weight: 700;
  font-family: inherit;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-enviar:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-enviar--compra {
  background: linear-gradient(135deg, #10b981, #059669);
  color: #fff;
  box-shadow: 0 4px 20px rgba(16,185,129,0.25);
}

.btn-enviar--compra:hover:not(:disabled) {
  background: linear-gradient(135deg, #0ea472, #047857);
  box-shadow: 0 6px 28px rgba(16,185,129,0.35);
  transform: translateY(-1px);
}

.btn-enviar--venda {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: #fff;
  box-shadow: 0 4px 20px rgba(239,68,68,0.25);
}

.btn-enviar--venda:hover:not(:disabled) {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  box-shadow: 0 6px 28px rgba(239,68,68,0.35);
  transform: translateY(-1px);
}

.order-info {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(148,163,184,0.05);
  border: 1px solid rgba(148,163,184,0.1);
  font-size: 0.72rem;
  color: var(--text-muted);
  line-height: 1.5;
  flex-shrink: 0;
}

.order-info span {
  flex: 1;
}

.order-info svg {
  flex-shrink: 0;
  margin-top: 2px;
}

.order-info strong {
  color: var(--text-secondary);
  font-weight: 600;
}

.order-info em {
  color: #fbbf24;
  font-style: normal;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 2px solid rgba(148,163,184,0.1);
  border-top-color: var(--accent-primary);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

.spinner-sm {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255,255,255,0.2);
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-error-enter-active,
.fade-error-leave-active { transition: all 0.2s ease; }
.fade-error-enter-from,
.fade-error-leave-to { opacity: 0; transform: translateY(-4px); }

@media (max-width: 1100px) {
  .mercado-layout {
    grid-template-columns: 200px 1fr 290px;
  }
}

@media (max-width: 860px) {
  .mercado-layout {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }

  .col-ativos {
    border-right: none;
    border-bottom: 1px solid rgba(148,163,184,0.07);
    max-height: 220px;
  }

  .col-chart {
    border-right: none;
    border-bottom: 1px solid rgba(148,163,184,0.07);
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
