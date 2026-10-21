import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
})

api.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      const { status } = error.response

      if (status === 401) {
        console.warn('[API] Sessão expirada ou não autenticada.')
      }

      if (status === 403) {
        console.warn('[API] Acesso negado.')
      }

      if (status >= 500) {
        console.error('[API] Erro interno do servidor:', error.response.data)
      }
    } else if (error.request) {
      console.error('[API] Servidor não respondeu. Verifique se o backend está rodando.')
    }

    return Promise.reject(error)
  }
)

export default api
