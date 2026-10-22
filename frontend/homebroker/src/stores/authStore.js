import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authService from '@/services/authService'

/**
 * Store de autenticacao — gerencia estado do usuario logado e token JWT.
 */
export const useAuthStore = defineStore('auth', () => {
  // ── State ─────────────────────────────────────────────
  const token = ref(localStorage.getItem('hb_token') ?? null)
  const usuario = ref(
    localStorage.getItem('hb_usuario')
      ? JSON.parse(localStorage.getItem('hb_usuario'))
      : null
  )
  const loading = ref(false)
  const error = ref(null)

  // ── Getters ───────────────────────────────────────────
  const isAuthenticated = computed(() => !!token.value)
  const nomeUsuario = computed(() => usuario.value?.nome ?? '')

  // ── Actions ───────────────────────────────────────────
  async function login(credentials) {
    loading.value = true
    error.value = null
    try {
      const data = await authService.login(credentials)
      _persistSession(data)
      return data
    } catch (err) {
      error.value = _extractMessage(err, 'Email ou senha inválidos.')
      throw err
    } finally {
      loading.value = false
    }
  }

  async function register(payload) {
    loading.value = true
    error.value = null
    try {
      // 1. Cadastra no backend
      await authService.register({
        nome: payload.nome,
        email: payload.email,
        senha: payload.senha,
      })
      // 2. Faz login automático para criar a sessão do usuário
      return await login({
        email: payload.email,
        senha: payload.senha,
      })
    } catch (err) {
      error.value = _extractMessage(err, 'Erro ao criar conta. Verifique os dados e tente novamente.')
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    authService.logout()
    _clearSession()
  }

  // ── Helpers ───────────────────────────────────────────
  function _persistSession(data) {
    token.value = data.idSessao || 'authenticated'
    usuario.value = data.usuario
    localStorage.setItem('hb_token', token.value)
    localStorage.setItem('hb_usuario', JSON.stringify(data.usuario))
  }

  function _clearSession() {
    token.value = null
    usuario.value = null
    localStorage.removeItem('hb_token')
    localStorage.removeItem('hb_usuario')
  }

  function _extractMessage(err, fallback) {
    if (!err.response?.data) return fallback

    const data = err.response.data
    // Se o backend retornou detail (RFC 7807 ProblemDetails)
    if (data.detail) return data.detail

    // Se retornou mapa de erros de validação
    if (data.erros && typeof data.erros === 'object') {
      const firstKey = Object.keys(data.erros)[0]
      return data.erros[firstKey] || fallback
    }

    return data.message || fallback
  }

  return {
    token,
    usuario,
    loading,
    error,
    isAuthenticated,
    nomeUsuario,
    login,
    register,
    logout,
  }
})
