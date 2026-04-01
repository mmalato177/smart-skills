<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import { Package } from 'lucide-vue-next'
import { useToast } from '../composables/useToast'

const { showToast } = useToast()

const auth = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)

async function handleLogin() {
  loading.value = true

  try {
    await auth.login(username.value, password.value)
    router.push('/')
  } catch (err) {
    const loginError = err.message || 'Login failed. Please try again.'
    showToast(loginError, 'error')
  } finally {
    loading.value = false
  }
}

const floatingIcons = [
  { id: 1, top: '10%', left: '8%', size: 42, duration: '8s', delay: '0s' },
  { id: 2, top: '18%', left: '78%', size: 42, duration: '10s', delay: '1s' },
  { id: 3, top: '72%', left: '12%', size: 42, duration: '9s', delay: '2s' },
  { id: 4, top: '80%', left: '82%', size: 42, duration: '11s', delay: '0.5s' },
  { id: 5, top: '42%', left: '6%', size: 42, duration: '7.5s', delay: '1.5s' },
  { id: 6, top: '28%', left: '90%', size: 42, duration: '8.5s', delay: '2.5s' },
  { id: 7, top: '60%', left: '88%', size: 42, duration: '12s', delay: '1.2s' },
  { id: 8, top: '12%', left: '48%', size: 42, duration: '7s', delay: '0.8s' }
]
</script>

<template>
  <div class="login-page">
    <div class="background-icons">
      <div
          v-for="icon in floatingIcons"
          :key="icon.id"
          class="floating-icon"
          :style="{
          top: icon.top,
          left: icon.left,
          animationDuration: icon.duration,
          animationDelay: icon.delay,
          width: icon.size + 'px',
          height: icon.size + 'px'
        }"
      >
        <Package class="bg-icon-svg" />
      </div>
    </div>

    <div class="login-card">
      <div class="login-logo">
        <img src="/logo.png" alt="Logo" />
      </div>

      <h2 class="login-title">Login</h2>

      <form @submit.prevent="handleLogin" class="login-form">
        <input
            v-model="username"
            type="text"
            placeholder="Username"
            required
        />

        <input
            v-model="password"
            type="password"
            placeholder="Password"
            required
        />

        <button type="submit" class="login-button" :disabled="loading">
          {{ loading ? 'A entrar...' : 'Login' }}
        </button>
      </form>
    </div>
    <footer class="login-footer">
      © {{ new Date().getFullYear() }} smart skills — All rights reserved
    </footer>
  </div>
</template>

<style scoped>
.login-footer {
  position: absolute;
  bottom: 16px;
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
  letter-spacing: 0.3px;
}

.login-footer span {
  font-weight: 600;
}

.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
      radial-gradient(circle at top left, rgba(117 ,183 ,44, 0.08), transparent 28%),
      radial-gradient(circle at bottom right, rgba(34, 197, 94, 0.06), transparent 24%),
      #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* TOAST */
.toast {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 30;
  min-width: 260px;
  max-width: 360px;
  padding: 14px 18px;
  border-radius: 12px;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

.toast.error {
  background: #dc3545;
}

.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.3s ease;
}

.toast-slide-enter-from,
.toast-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* BACKGROUND ICONS */

.background-icons {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.floating-icon {
  position: absolute;
  color: rgba(117, 183, 44, 0.5);
  animation: floatMold linear infinite;
  opacity: 0.8;
}

.bg-icon-svg {
  width: 100%;
  height: 100%;
}

/* animação contínua e suave */
@keyframes floatMold {
  0% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-40px) rotate(10deg);
  }

  100% {
    transform: translateY(0px) rotate(0deg);
  }
}

/* CARD */
.login-card {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(6px);
  padding: 38px 34px;
  border-radius: 12px;
  box-shadow:
      0 20px 50px rgba(0, 0, 0, 0.10),
      0 2px 10px rgba(0, 0, 0, 0.04);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.65);
  max-height: 60vh;
  height: 100%;
}

.login-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.login-logo img {
  max-height: 150px;
  width: auto;
  object-fit: contain;
}

.login-title {
  margin: 0 0 24px;
  font-size: 26px;
  font-weight: 700;
  color: #4a4d45;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.login-form input {
  width: 100%;
  padding: 13px 14px;
  border: 1px solid #d9dee7;
  border-radius: 12px;
  font-size: 14px;
  background: #fff;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.login-form input:focus {
  outline: none;
  border-color: #75b72c;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.12);
}

.login-button {
  margin-top: 6px;
  width: 100%;
  padding: 13px;
  border: none;
  border-radius: 12px;
  background: #75b72c;
  color: white;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-button:hover {
  background: #6db11f;
  transform: translateY(-1px);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 480px) {
  .login-card {
    margin: 20px;
    padding: 30px 22px;
  }

  .toast {
    right: 16px;
    left: 16px;
    top: 16px;
    max-width: none;
  }
}
</style>