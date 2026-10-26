import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import carteiraService from '@/services/carteiraService'

export const useCarteiraStore = defineStore('carteira', () => {
  const saldo = ref({
    saldoCaixa: 0,
    saldoReservado: 0,
    saldoDisponivel: 0,
  })

  const posicoes = ref([])

  const loading = ref(false)
  const error = ref(null)

  const saldoDisponivel = computed(() => saldo.value.saldoDisponivel)
  const saldoCaixa = computed(() => saldo.value.saldoCaixa)
  const saldoReservado = computed(() => saldo.value.saldoReservado)
  const totalAtivos = computed(() => posicoes.value.length)

  const patrimonioEmAtivos = computed(() =>
    posicoes.value.reduce((total, p) => total + (p.quantidade * (p.precoAtual || p.precoMedio || 0)), 0)
  )

  const patrimonioTotal = computed(() => saldo.value.saldoCaixa + patrimonioEmAtivos.value)

  async function carregarSaldo() {
    try {
      const data = await carteiraService.obterSaldo()
      const resultado = data?.resultado || data?.data || data
      saldo.value = {
        saldoCaixa: resultado.saldoCaixa ?? 0,
        saldoReservado: resultado.saldoReservado ?? 0,
        saldoDisponivel: resultado.saldoDisponivel ?? (resultado.saldoCaixa - resultado.saldoReservado) ?? 0,
      }
    } catch (err) {
      console.warn('[CarteiraStore] Erro ao carregar saldo:', err?.message)
      throw err
    }
  }

  async function carregarCarteira() {
    try {
      const data = await carteiraService.obterCarteira()
      const resultado = data?.resultado || data?.data || data
      posicoes.value = Array.isArray(resultado) ? resultado : resultado?.posicoes || []
    } catch (err) {
      console.warn('[CarteiraStore] Erro ao carregar carteira:', err?.message)
      throw err
    }
  }

  async function carregarTudo() {
    loading.value = true
    error.value = null
    try {
      await Promise.all([carregarSaldo(), carregarCarteira()])
    } catch (err) {
      error.value = 'Não foi possível carregar os dados da carteira. Verifique sua conexão.'
    } finally {
      loading.value = false
    }
  }

  return {
    saldo,
    posicoes,
    loading,
    error,
    saldoDisponivel,
    saldoCaixa,
    saldoReservado,
    totalAtivos,
    patrimonioEmAtivos,
    patrimonioTotal,
    carregarSaldo,
    carregarCarteira,
    carregarTudo,
  }
})
