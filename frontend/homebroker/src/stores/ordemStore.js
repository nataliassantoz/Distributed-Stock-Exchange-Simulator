import { defineStore } from 'pinia'
import { ref } from 'vue'
import ordemService from '@/services/ordemService'

export const useOrdemStore = defineStore('ordem', () => {
  const ativos = ref([])
  const ultimaOrdem = ref(null)
  const loading = ref(false)
  const loadingAtivos = ref(false)
  const error = ref(null)

  /**
   * Carrega a lista de ativos disponíveis para negociação.
   */
  async function carregarAtivos() {
    loadingAtivos.value = true
    error.value = null
    try {
      const data = await ordemService.listarAtivos()
      const resultado = data?.resultado || data?.data || data
      ativos.value = Array.isArray(resultado) ? resultado : []
    } catch (err) {
      error.value = 'Não foi possível carregar os ativos disponíveis.'
      console.error('[OrdemStore] Erro ao carregar ativos:', err?.message)
    } finally {
      loadingAtivos.value = false
    }
  }

  /**
   * Envia uma ordem de compra ou venda.
   * @param {{ ativo: string, tipo: 'COMPRA'|'VENDA', quantidade: number, preco: number }} payload
   */
  async function enviarOrdem(payload) {
    loading.value = true
    error.value = null
    ultimaOrdem.value = null
    try {
      const data = await ordemService.enviarOrdem(payload)
      const resultado = data?.resultado || data?.data || data
      ultimaOrdem.value = resultado
      return resultado
    } catch (err) {
      error.value = 'Não foi possível enviar a ordem. Tente novamente.'
      console.error('[OrdemStore] Erro ao enviar ordem:', err?.message)
      throw err
    } finally {
      loading.value = false
    }
  }

  function limparUltimaOrdem() {
    ultimaOrdem.value = null
    error.value = null
  }

  return {
    ativos,
    ultimaOrdem,
    loading,
    loadingAtivos,
    error,
    carregarAtivos,
    enviarOrdem,
    limparUltimaOrdem,
  }
})
