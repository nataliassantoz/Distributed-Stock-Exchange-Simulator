import api from './api'
import ordemMock from '@/mocks/ordemMock.json'

const ordemService = {
  /**
   * Busca os ativos disponíveis para negociação.
   * Fallback para mock em caso de indisponibilidade do backend.
   */
  async listarAtivos() {
    try {
      const response = await api.get('/ativos')
      return response.data
    } catch (err) {
      console.warn('[ordemService] Backend indisponível ou endpoint /ativos não implementado. Utilizando dados simulados.', err?.message)
      return ordemMock.ativos
    }
  },

  /**
   * Envia uma ordem de compra ou venda.
   * Payload: { ativo, tipo, quantidade, preco }
   * Fallback: simula resposta de sucesso com mock quando o backend está indisponível.
   */
  async enviarOrdem(payload) {
    try {
      const response = await api.post('/ordens', payload)
      return response.data
    } catch (err) {
      console.warn('[ordemService] Backend indisponível ou endpoint POST /ordens não implementado. Simulando resposta de sucesso.', err?.message)
      // Simula uma resposta de sucesso com ID gerado localmente
      return {
        ...ordemMock.ordemEnviada,
        id: `ORD-${Date.now()}`,
        ativo: payload.ativo,
        tipo: payload.tipo,
        quantidade: payload.quantidade,
        preco: payload.preco,
        criadoEm: new Date().toISOString(),
      }
    }
  },
}

export default ordemService
