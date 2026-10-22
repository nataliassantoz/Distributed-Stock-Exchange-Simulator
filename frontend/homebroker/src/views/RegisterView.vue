<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

// ── Form state ────────────────────────────────────────
const form = reactive({
  nome: '',
  email: '',
  senha: '',
  confirmarSenha: '',
  tipo: 'HUMANO',
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)

// ── Validation ────────────────────────────────────────
const nomeError = computed(() => {
  if (!form.nome) return ''
  return form.nome.trim().length < 3 ? 'Nome deve ter ao menos 3 caracteres' : ''
})

const emailError = computed(() => {
  if (!form.email) return ''
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(form.email) ? '' : 'Email inválido'
})

const senhaError = computed(() => {
  if (!form.senha) return ''
  if (form.senha.length < 8) return 'Mínimo de 8 caracteres'
  if (!/[A-Z]/.test(form.senha)) return 'Use ao menos uma letra maiúscula'
  if (!/[0-9]/.test(form.senha)) return 'Use ao menos um número'
  return ''
})

const confirmarSenhaError = computed(() => {
  if (!form.confirmarSenha) return ''
  return form.confirmarSenha !== form.senha ? 'As senhas não coincidem' : ''
})

const senhaStrength = computed(() => {
  const s = form.senha
  if (!s) return 0
  let score = 0
  if (s.length >= 6) score++
  if (s.length >= 10) score++
  if (/[A-Z]/.test(s)) score++
  if (/[0-9]/.test(s)) score++
  if (/[^A-Za-z0-9]/.test(s)) score++
  return score
})

const senhaStrengthLabel = computed(() => {
  const labels = ['', 'Muito fraca', 'Fraca', 'Média', 'Forte', 'Muito forte']
  return labels[senhaStrength.value] ?? ''
})

const senhaStrengthColor = computed(() => {
  const colors = ['', '#ef4444', '#f97316', '#eab308', '#10b981', '#06b6d4']
  return colors[senhaStrength.value] ?? ''
})

const isFormValid = computed(
  () =>
    form.nome.trim().length >= 3 &&
    form.email &&
    form.senha &&
    form.confirmarSenha &&
    !nomeError.value &&
    !emailError.value &&
    !senhaError.value &&
    !confirmarSenhaError.value
)

// ── Submit ────────────────────────────────────────────
async function handleSubmit() {
  if (!isFormValid.value) return
  try {
    await authStore.register({
      nome: form.nome.trim(),
      email: form.email,
      senha: form.senha,
      tipo: form.tipo,
    })
    router.push({ name: 'home' })
  } catch {
    // erro tratado pelo store (authStore.error)
  }
}
</script>

<template>
  <div class="auth-page">
    <!-- Background decorativo -->
    <div class="auth-bg">
      <div class="auth-bg__orb auth-bg__orb--1" />
      <div class="auth-bg__orb auth-bg__orb--2" />
      <div class="grid-lines" />
    </div>

    <main class="auth-container animate-fade-in-up">
      <!-- Cabeçalho do card -->
      <div class="auth-header">
        <div class="auth-logo">
          <svg width="32" height="32" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="2" y="16" width="4" height="10" rx="1" fill="currentColor" opacity="0.5"/>
            <rect x="8" y="10" width="4" height="16" rx="1" fill="currentColor" opacity="0.7"/>
            <rect x="14" y="6" width="4" height="20" rx="1" fill="currentColor" opacity="0.85"/>
            <rect x="20" y="2" width="4" height="24" rx="1" fill="currentColor"/>
          </svg>
        </div>
        <h1 class="auth-title">Criar sua conta</h1>
        <p class="auth-subtitle">Comece a simular investimentos agora mesmo</p>
      </div>

      <!-- Formulário -->
      <form id="form-registro" class="auth-form" novalidate @submit.prevent="handleSubmit">
        <!-- Nome -->
        <div class="form-group" :class="{ 'form-group--error': nomeError }">
          <label for="reg-nome" class="form-label">Nome completo</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
            </span>
            <input
              id="reg-nome"
              v-model="form.nome"
              type="text"
              class="form-input"
              placeholder="João da Silva"
              autocomplete="name"
              required
            />
          </div>
          <span v-if="nomeError" class="form-error-msg">{{ nomeError }}</span>
        </div>

        <!-- Email -->
        <div class="form-group" :class="{ 'form-group--error': emailError }">
          <label for="reg-email" class="form-label">Email</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
              </svg>
            </span>
            <input
              id="reg-email"
              v-model="form.email"
              type="email"
              class="form-input"
              placeholder="seu@email.com"
              autocomplete="email"
              required
            />
          </div>
          <span v-if="emailError" class="form-error-msg">{{ emailError }}</span>
        </div>

        <!-- Tipo de conta -->
        <div class="form-group">
          <label class="form-label">Tipo de conta</label>
          <div class="tipo-selector">
            <button
              id="tipo-humano"
              type="button"
              class="tipo-option"
              :class="{ 'tipo-option--active': form.tipo === 'HUMANO' }"
              @click="form.tipo = 'HUMANO'"
            >
              <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
              <span>Humano</span>
            </button>
            <button
              id="tipo-agente"
              type="button"
              class="tipo-option"
              :class="{ 'tipo-option--active': form.tipo === 'AGENTE' }"
              @click="form.tipo = 'AGENTE'"
            >
              <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2V9M9 21H5a2 2 0 01-2-2V9m0 0h18"/>
              </svg>
              <span>Agente</span>
            </button>
          </div>
          <p class="form-hint">
            <span v-if="form.tipo === 'HUMANO'">Investidor humano — opera manualmente no simulador.</span>
            <span v-else>Investidor automatizado — bot que opera via algoritmo.</span>
          </p>
        </div>

        <!-- Senha -->
        <div class="form-group" :class="{ 'form-group--error': senhaError }">
          <label for="reg-senha" class="form-label">Senha</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
              </svg>
            </span>
            <input
              id="reg-senha"
              v-model="form.senha"
              :type="showPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="••••••••"
              autocomplete="new-password"
              required
            />
            <button
              type="button"
              class="form-toggle-pw"
              :aria-label="showPassword ? 'Ocultar senha' : 'Mostrar senha'"
              @click="showPassword = !showPassword"
            >
              <svg v-if="!showPassword" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              <svg v-else width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
              </svg>
            </button>
          </div>
          <span v-if="senhaError" class="form-error-msg">{{ senhaError }}</span>

          <!-- Força da senha -->
          <div v-if="form.senha" class="password-strength">
            <div class="password-strength__bars">
              <span
                v-for="i in 5"
                :key="i"
                class="password-strength__bar"
                :style="{ background: i <= senhaStrength ? senhaStrengthColor : undefined }"
              />
            </div>
            <span class="password-strength__label" :style="{ color: senhaStrengthColor }">
              {{ senhaStrengthLabel }}
            </span>
          </div>
        </div>

        <!-- Confirmar senha -->
        <div class="form-group" :class="{ 'form-group--error': confirmarSenhaError }">
          <label for="reg-confirmar-senha" class="form-label">Confirmar senha</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
              </svg>
            </span>
            <input
              id="reg-confirmar-senha"
              v-model="form.confirmarSenha"
              :type="showConfirmPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="••••••••"
              autocomplete="new-password"
              required
            />
            <button
              type="button"
              class="form-toggle-pw"
              :aria-label="showConfirmPassword ? 'Ocultar confirmação' : 'Mostrar confirmação'"
              @click="showConfirmPassword = !showConfirmPassword"
            >
              <svg v-if="!showConfirmPassword" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
              </svg>
              <svg v-else width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
              </svg>
            </button>
          </div>
          <span v-if="confirmarSenhaError" class="form-error-msg">{{ confirmarSenhaError }}</span>
        </div>


        <!-- Feedback de erro da API -->
        <Transition name="alert">
          <div v-if="authStore.error" id="reg-error-alert" class="form-alert form-alert--error">
            <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            {{ authStore.error }}
          </div>
        </Transition>

        <!-- Submit -->
        <button
          id="btn-registro-submit"
          type="submit"
          class="btn-primary"
          :disabled="!isFormValid || authStore.loading"
        >
          <span v-if="authStore.loading" class="btn-spinner" />
          <span>{{ authStore.loading ? 'Criando conta…' : 'Criar conta' }}</span>
        </button>

        <!-- Divisor -->
        <div class="form-divider">
          <span>ou</span>
        </div>

        <!-- Link para login -->
        <p class="form-redirect">
          Já tem uma conta?
          <router-link id="link-para-login" to="/login" class="form-link">
            Entrar
          </router-link>
        </p>
      </form>
    </main>
  </div>
</template>

<style scoped>
/* ── Layout ─────────────────────────────────────────── */
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 24px 40px;
  position: relative;
  overflow: hidden;
}

/* ── Background ─────────────────────────────────────── */
.auth-bg {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.auth-bg__orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.auth-bg__orb--1 {
  width: 500px;
  height: 500px;
  top: -150px;
  left: -100px;
  background: radial-gradient(circle, rgba(20,184,166,0.12) 0%, transparent 70%);
  animation: pulseGlow 6s ease-in-out infinite;
}

.auth-bg__orb--2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -100px;
  background: radial-gradient(circle, rgba(16,185,129,0.1) 0%, transparent 70%);
  animation: pulseGlow 8s ease-in-out infinite reverse;
}

.grid-lines {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* ── Card ───────────────────────────────────────────── */
.auth-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  background: rgba(15, 22, 41, 0.7);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(148,163,184,0.1);
  border-radius: 20px;
  padding: 40px 36px;
  box-shadow:
    0 25px 50px rgba(0,0,0,0.4),
    0 0 0 1px rgba(16,185,129,0.05) inset;
}

/* ── Header ─────────────────────────────────────────── */
.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(16,185,129,0.2), rgba(20,184,166,0.1));
  border: 1px solid rgba(16,185,129,0.3);
  border-radius: 14px;
  color: #10b981;
  margin: 0 auto 16px;
}

.auth-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin-bottom: 6px;
}

.auth-subtitle {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

/* ── Form ───────────────────────────────────────────── */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--text-secondary);
  letter-spacing: 0.01em;
}

.form-hint {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-top: 2px;
}

.form-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-icon {
  position: absolute;
  left: 14px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  pointer-events: none;
  transition: color 0.2s;
}

.form-input {
  width: 100%;
  padding: 11px 40px 11px 42px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(148,163,184,0.15);
  border-radius: 10px;
  color: var(--text-primary);
  font-family: inherit;
  font-size: 0.9375rem;
  outline: none;
  transition: all 0.2s;
}

.form-input::placeholder {
  color: var(--text-muted);
}

.form-input:focus {
  border-color: rgba(16,185,129,0.5);
  background: rgba(16,185,129,0.04);
  box-shadow: 0 0 0 3px rgba(16,185,129,0.1);
}

.form-input-wrapper:focus-within .form-icon {
  color: #10b981;
}

.form-group--error .form-input {
  border-color: rgba(239,68,68,0.5);
  background: rgba(239,68,68,0.04);
}

.form-group--error .form-input:focus {
  box-shadow: 0 0 0 3px rgba(239,68,68,0.1);
}

.form-toggle-pw {
  position: absolute;
  right: 14px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  padding: 2px;
  transition: color 0.2s;
}

.form-toggle-pw:hover {
  color: var(--text-secondary);
}

.form-error-msg {
  font-size: 0.75rem;
  color: #f87171;
}

/* ── Tipo de conta ──────────────────────────────────── */
.tipo-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.tipo-option {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 11px 16px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(148,163,184,0.15);
  border-radius: 10px;
  color: var(--text-secondary);
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.tipo-option:hover {
  border-color: rgba(16,185,129,0.3);
  color: var(--text-primary);
}

.tipo-option--active {
  background: rgba(16,185,129,0.12);
  border-color: rgba(16,185,129,0.5);
  color: #10b981;
  box-shadow: 0 0 16px rgba(16,185,129,0.1);
}

/* ── Força da senha ─────────────────────────────────── */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 6px;
}

.password-strength__bars {
  display: flex;
  gap: 4px;
  flex: 1;
}

.password-strength__bar {
  flex: 1;
  height: 4px;
  background: rgba(148,163,184,0.15);
  border-radius: 999px;
  transition: background 0.3s ease;
}

.password-strength__label {
  font-size: 0.75rem;
  font-weight: 500;
  min-width: 80px;
  text-align: right;
  transition: color 0.3s;
}

/* ── Checkbox de termos ─────────────────────────────── */
.terms-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  cursor: pointer;
}

.terms-checkbox__input {
  display: none;
}

.terms-checkbox__box {
  flex-shrink: 0;
  width: 18px;
  height: 18px;
  border: 1.5px solid rgba(148,163,184,0.3);
  border-radius: 5px;
  background: rgba(255,255,255,0.04);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  margin-top: 1px;
  color: #0a0f1a;
}

.terms-checkbox:hover .terms-checkbox__box {
  border-color: rgba(16,185,129,0.4);
}

.terms-checkbox__input:checked ~ .terms-checkbox__box {
  background: #10b981;
  border-color: #10b981;
}

.terms-checkbox__label {
  font-size: 0.8125rem;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* ── Alert ──────────────────────────────────────────── */
.form-alert {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 0.8125rem;
}

.form-alert--error {
  background: rgba(239,68,68,0.1);
  border: 1px solid rgba(239,68,68,0.25);
  color: #f87171;
}

.alert-enter-active, .alert-leave-active {
  transition: all 0.25s ease;
}
.alert-enter-from, .alert-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

/* ── Botão primário ─────────────────────────────────── */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 13px 24px;
  background: linear-gradient(135deg, #10b981, #14b8a6);
  color: #0a0f1a;
  font-family: inherit;
  font-size: 0.9375rem;
  font-weight: 600;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 0 24px rgba(16,185,129,0.25);
  margin-top: 4px;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 0 36px rgba(16,185,129,0.4);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-primary:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.btn-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(10,15,26,0.3);
  border-top-color: #0a0f1a;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Divisor ────────────────────────────────────────── */
.form-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
  font-size: 0.8125rem;
}

.form-divider::before,
.form-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: rgba(148,163,184,0.12);
}

/* ── Redirect ───────────────────────────────────────── */
.form-redirect {
  text-align: center;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.form-link {
  color: #10b981;
  font-weight: 500;
  transition: color 0.2s;
}

.form-link:hover {
  color: #34d399;
  text-decoration: underline;
}

/* ── Responsive ─────────────────────────────────────── */
@media (max-width: 480px) {
  .auth-container {
    padding: 32px 24px;
  }

  .tipo-selector {
    grid-template-columns: 1fr;
  }
}
</style>
