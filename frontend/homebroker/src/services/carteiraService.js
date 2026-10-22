import api from './api'
import carteiraMock from '@/mocks/carteiraMock.json'

const carteiraService = {
  async obterCarteira() {
    try {
      const response = await api.get('/carteira')
      return response.data
    } catch (err) {
      console.warn('[carteiraService] Backend indisponível ou endpoint /carteira não implementado. Utilizando dados simulados.', err?.message)
      return carteiraMock.carteira
    }
  },

  async obterSaldo() {
    try {
      const response = await api.get('/saldo')
      return response.data
    } catch (err) {
      console.warn('[carteiraService] Backend indisponível ou endpoint /saldo não implementado. Utilizando dados simulados.', err?.message)
      return carteiraMock.saldo
    }
  },
}

export default carteiraService
