import api from './api'
import ordemMock from '@/mocks/ordemMock.json'

const ordemService = {
  /**
   * Busca os ativos disponíveis para negociação.
   * Normaliza os campos para garantir que id, ticker e precoAtual existam.
   * Fallback para mock em caso de indisponibilidade do backend.
   */
  async listarAtivos() {
    try {
      const response = await api.get('/ativos')
      const payload = response.data?.dados ?? response.data?.resultado ?? response.data
      const lista = Array.isArray(payload) ? payload : (Array.isArray(payload?.ativos) ? payload.ativos : [])

      if (lista.length > 0) {
        return lista.map((a, index) => ({
          id: a.id ?? (index + 1),
          ticker: a.ticker || a.codigo,
          codigo: a.codigo || a.ticker,
          nome: a.nome,
          precoAtual: Number(a.precoAtual),
        }))
      }
      return ordemMock.ativos
    } catch (err) {
      console.warn('[ordemService] Backend indisponível ou endpoint /ativos não implementado. Utilizando dados simulados.', err?.message)
      return ordemMock.ativos
    }
  },

  /**
   * Envia uma ordem de compra ou venda para o backend.
   * Payload esperado pelo backend (OrdemDTO.Request.Cadastro):
   * { ativo: Long, tipo: 'COMPRA'|'VENDA', quantidade: Integer, preco: BigDecimal }
   */
  async enviarOrdem(payload) {
    const requestBody = {
      ativo: Number(payload.ativo),
      tipo: payload.tipo,
      quantidade: Number(payload.quantidade),
      preco: Number(Number(payload.preco).toFixed(2)),
    }

    let response
    try {
      // Backend mapeia @RequestMapping("/ordens") na raiz
      response = await api.post('/ordens', requestBody, { baseURL: '' })
    } catch (err) {
      // Fallback para /api/ordens caso o backend seja refatorado para ter o prefixo /api
      if (err.response?.status === 404) {
        response = await api.post('/ordens', requestBody)
      } else {
        throw err
      }
    }

    const dadosOrdem = response.data?.dados ?? response.data?.resultado ?? response.data
    return {
      ...dadosOrdem,
      id: dadosOrdem.id ?? `ORD-${Date.now()}`,
      ativo: dadosOrdem.ativo ?? payload.ativo,
      ticker: payload.ticker || payload.codigoAtivo || String(dadosOrdem.ativo),
      tipo: dadosOrdem.tipo ?? payload.tipo,
      quantidade: dadosOrdem.quantidade ?? payload.quantidade,
      preco: dadosOrdem.preco ?? payload.preco,
      status: dadosOrdem.status ?? 'PENDENTE',
      criadoEm: dadosOrdem.dataHora ?? dadosOrdem.criadoEm ?? new Date().toISOString(),
      mensagem: response.data?.msgUsuario || 'Ordem registrada com sucesso.',
    }
  },
}

export default ordemService
