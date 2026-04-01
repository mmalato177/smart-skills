<template>
  <nav class="topbar">
    <div class="topbar-container">
      <router-link to="/" class="brand">
        <img src="/logo.png" alt="Smart Skills" class="brand-logo" />
      </router-link>

      <UserMenu />
    </div>
  </nav>
</template>

<script setup>
import UserMenu from './UserMenu.vue'
</script>

<style scoped>
.topbar {
  background: #ffffff;
  border-bottom: 1px solid #e5e5e5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.topbar-container {
  max-width: 1280px;
  height: 88px;
  margin: 0 auto;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
}

.brand-logo {
  height: 70px;
  width: auto;
  object-fit: contain;
}
</style>