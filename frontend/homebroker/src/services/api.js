import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
})

let csrfCache = null

export async function carregarCsrf() {
  if (csrfCache) return csrfCache
  try {
    const { data } = await api.get('/auth/csrf')
    csrfCache = {
      headerName: data.headerName || 'X-CSRF-TOKEN',
      token: data.token,
    }
    return csrfCache
  } catch (err) {
    console.warn('[API] Não foi possível obter o token CSRF:', err?.message)
    return null
  }
}

export function invalidarCsrf() {
  csrfCache = null
}

api.interceptors.request.use(
  async (config) => {
    const method = config.method ? config.method.toUpperCase() : 'GET'
    if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method)) {
      const csrf = await carregarCsrf()
      if (csrf && csrf.token) {
        config.headers[csrf.headerName] = csrf.token
      }
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response) {
      const { status } = error.response

      if (status === 403) {
        invalidarCsrf()
        console.warn('[API] Acesso negado ou token CSRF inválido.')
      }

      if (status === 401) {
        console.warn('[API] Sessão expirada ou não autenticada.')
      }

      if (status >= 500) {
        console.error('[API] Erro interno do servidor:', error.response.data)
      }
    } else if (error.request) {
      console.error('[API] Servidor não respondeu. Verifique se o backend está rodando em http://localhost:8080.')
    }

    return Promise.reject(error)
  }
)

export default api

