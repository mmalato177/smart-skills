<template>
  <div class="toast-container">
    <transition-group name="toast">
      <div
          v-for="toast in toasts"
          :key="toast.id"
          :class="['toast', toast.type]"
      >
        {{ toast.message }}
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useToast } from '../composables/useToast'

const { toasts } = useToast()
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.toast {
  min-width: 260px;
  max-width: 380px;
  padding: 14px 18px;
  border-radius: 12px;
  color: white;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 10px 30px rgba(0,0,0,0.16);
}

.toast.success {
  background: #7cc12b;
}

.toast.error {
  background: #d9534f;
}

.toast.info {
  background: #3498db;
}

.toast-enter-active,
.toast-leave-active {
  transition: all .25s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>