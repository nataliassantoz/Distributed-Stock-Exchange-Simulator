<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

// ── Form state ────────────────────────────────────────
const form = reactive({
  email: '',
  senha: '',
})

const showPassword = ref(false)

// ── Validation ────────────────────────────────────────
const emailError = computed(() => {
  if (!form.email) return ''
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(form.email) ? '' : 'Email inválido'
})

const senhaError = computed(() => {
  if (!form.senha) return ''
  return form.senha.length < 6 ? 'Mínimo de 6 caracteres' : ''
})

const isFormValid = computed(
  () =>
    form.email &&
    form.senha &&
    !emailError.value &&
    !senhaError.value
)

// ── Submit ────────────────────────────────────────────
async function handleSubmit() {
  if (!isFormValid.value) return
  try {
    await authStore.login({ email: form.email, senha: form.senha })
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
        <h1 class="auth-title">Login</h1>
        <p class="auth-subtitle">Acesse sua conta para operar no mercado</p>
      </div>

      <!-- Formulário -->
      <form id="form-login" class="auth-form" novalidate @submit.prevent="handleSubmit">
        <!-- Email -->
        <div class="form-group" :class="{ 'form-group--error': emailError }">
          <label for="login-email" class="form-label">Email</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
              </svg>
            </span>
            <input
              id="login-email"
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

        <!-- Senha -->
        <div class="form-group" :class="{ 'form-group--error': senhaError }">
          <label for="login-senha" class="form-label">Senha</label>
          <div class="form-input-wrapper">
            <span class="form-icon">
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
              </svg>
            </span>
            <input
              id="login-senha"
              v-model="form.senha"
              :type="showPassword ? 'text' : 'password'"
              class="form-input"
              placeholder="••••••••"
              autocomplete="current-password"
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
        </div>

        <!-- Feedback de erro da API -->
        <Transition name="alert">
          <div v-if="authStore.error" id="login-error-alert" class="form-alert form-alert--error">
            <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            {{ authStore.error }}
          </div>
        </Transition>

        <!-- Submit -->
        <button
          id="btn-login-submit"
          type="submit"
          class="btn-primary"
          :disabled="!isFormValid || authStore.loading"
        >
          <span v-if="authStore.loading" class="btn-spinner" />
          <span>{{ authStore.loading ? 'Entrando…' : 'Entrar' }}</span>
        </button>

        <!-- Divisor -->
        <div class="form-divider">
          <span>ou</span>
        </div>

        <!-- Link para registro -->
        <p class="form-redirect">
          Não tem uma conta?
          <router-link id="link-para-registro" to="/registrar" class="form-link">
            Criar conta
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

/* ── Background decorativo ──────────────────────────── */
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
  right: -150px;
  background: radial-gradient(circle, rgba(16,185,129,0.12) 0%, transparent 70%);
  animation: pulseGlow 6s ease-in-out infinite;
}

.auth-bg__orb--2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  background: radial-gradient(circle, rgba(20,184,166,0.1) 0%, transparent 70%);
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
  max-width: 440px;
  background: rgba(15, 22, 41, 0.7);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(148, 163, 184, 0.1);
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

.form-input:focus + .form-icon,
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
}
</style>
