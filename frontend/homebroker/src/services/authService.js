import api, { invalidarCsrf } from './api'

/**
  * Serviço de autenticação conectado aos endpoints do Spring Boot (/api/auth/*):
  * - POST /api/auth/cadastro
  * - POST /api/auth/login
  * - GET  /api/auth/me
  */
const authService = {
  /**
   * Realiza login do usuário.
   * @param {{ email: string, senha: string }} credentials
   * @returns {Promise<{ idSessao: string, usuario: { id: number, nome: string, email: string } }>}
   */
  async login(credentials) {
    const response = await api.post('/auth/login', {
      email: credentials.email.trim(),
      senha: credentials.senha,
    })
    return response.data
  },

  /**
   * Registra um novo usuário no backend Spring Boot.
   * Rota real: POST /api/auth/cadastro (conforme AutenticacaoController)
   * Payload: { nome, email, senha }
   * @param {{ nome: string, email: string, senha: string }} payload
   * @returns {Promise<{ id: number, nome: string, email: string }>}
   */
  async register(payload) {
    const response = await api.post('/auth/cadastro', {
      nome: payload.nome.trim(),
      email: payload.email.trim(),
      senha: payload.senha,
    })
    return response.data
  },

  /**
   * Obtém os dados do usuário autenticado na sessão atual.
   * @returns {Promise<{ id: number, nome: string, email: string }>}
   */
  async me() {
    const response = await api.get('/auth/me')
    return response.data
  },

  /**
   * Realiza logout limpando tokens CSRF locais.
   */
  logout() {
    invalidarCsrf()
  },
}

export default authService
