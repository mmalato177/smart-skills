<template>
  <div class="user-menu" v-click-outside="closeMenu">
    <button @click="toggleMenu" class="user-menu-button">

      <div class="user-avatar">
        <img
            v-if="profileImageSrc"
            :src="profileImageSrc"
            class="user-avatar-img"
            alt="User"
        />
        <User v-else class="user-avatar-icon" />
      </div>

      <span class="user-menu-text">
        {{ auth.user?.username || 'Minha Conta' }}
      </span>
    </button>

    <div v-if="isOpen" class="user-dropdown">
      <button @click="handleProfile" class="user-dropdown-item">
        <UserCircle class="user-dropdown-icon" />
        Meu Perfil
      </button>

      <button @click="handleLogout" class="user-dropdown-item logout">
        <LogOut class="user-dropdown-icon" />
        Logout
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { User, LogOut, UserCircle } from 'lucide-vue-next'
import router from "@/router/index.js";
import { useToast } from '../composables/useToast'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()

const { showToast } = useToast()

const isOpen = ref(false)
const loading = ref(false)

const profileImageSrc = computed(() => {
  const path = auth.user?.profileImageUrl

  if (!path) return ''
  if (path.startsWith('http')) return path

  return path.startsWith('/') ? path : `/${path}`
})

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

const closeMenu = () => {
  isOpen.value = false
}

const handleLogout = async () => {
  try {
    await auth.logout()
    closeMenu()
    router.push('/login')
  } catch (err) {
    const logoutError = err.message || 'Logout failed. Please try again.'
    showToast(logoutError, 'error')
  } finally {
    loading.value = false
  }
}

const handleProfile = async () => {
  try {
    await auth.fetchMe()
    closeMenu()
    router.push('/profile')
  } catch (err) {
    const profileError = err.message || 'Fetching profile failed. Please try again.'
    showToast(profileError, 'error')
  } finally {
    loading.value = false
  }
}

const vClickOutside = {
  mounted(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!el.contains(event.target)) {
        binding.value()
      }
    }
    document.addEventListener('click', el.clickOutsideEvent)
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent)
  }
}
</script>

<style scoped>
.user-menu {
  position: relative;
}

.user-menu-button {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px;
  border: none;
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.user-menu-button:hover {
  background: #f3f4f6;
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  background: #7cc12b;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar-icon {
  width: 20px;
  height: 20px;
  color: #ffffff;
}

.user-menu-text {
  font-size: 16px;
  font-weight: 600;
  color: #4a4d45;
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  width: 210px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12);
  padding: 8px 0;
  z-index: 100;
}

.user-dropdown-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  color: #4a4d45;
  font-size: 15px;
  text-align: left;
  cursor: pointer;
  transition: background 0.2s ease;
}

.user-dropdown-item:hover {
  background: #f8f8f8;
}

.user-dropdown-item.logout {
  color: #d61f45;
}

.user-dropdown-item.logout:hover {
  background: #fff1f3;
}

.user-dropdown-icon {
  width: 16px;
  height: 16px;
}

@media (max-width: 768px) {
  .user-menu-text {
    display: none;
  }
}
</style>