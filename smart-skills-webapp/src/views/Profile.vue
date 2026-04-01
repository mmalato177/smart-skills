<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Upload, Lock, User } from 'lucide-vue-next'
import { useAuthStore } from '../stores/auth'
import { useToast } from '../composables/useToast'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()
const auth = useAuthStore()
const { showToast } = useToast()

const selectedImage = ref(null)
const previewUrl = ref('')

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const loadingPassword = ref(false)
const loadingImage = ref(false)

const getFullImageUrl = (path) => {
  if (!path) return ''
  if (path.startsWith('http')) return path

  return path.startsWith('/') ? path : `/${path}`
}

onMounted(() => {
  previewUrl.value = getFullImageUrl(auth.user?.profileImageUrl)
})

const goBack = () => {
  router.back()
}

function handleImageChange(event) {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    showToast('Ficheiro inválido. Seleciona uma imagem.', 'error')
    return
  }

  if (file.size > 10 * 1024 * 1024) {
    showToast('A imagem é demasiado grande. Máximo: 10MB.', 'error')
    return
  }

  selectedImage.value = file
  previewUrl.value = URL.createObjectURL(file)
}

const uploadImage = async () => {
  if (!selectedImage.value) {
    showToast('Seleciona uma imagem primeiro.', 'error')
    return
  }

  try {
    loadingImage.value = true

    await auth.uploadProfileImage(selectedImage.value)
    await auth.fetchMe()
    previewUrl.value = getFullImageUrl(auth.user?.profileImageUrl)
    showToast('Imagem atualizada com sucesso.', 'success')
  } catch (error) {
    console.error(error)
    showToast('Não foi possível atualizar a imagem.', 'error')
  } finally {
    loadingImage.value = false
  }
}

const changePassword = async () => {
  if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
    showToast('Preenche todos os campos da password.', 'error')
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    showToast('A confirmação da password não coincide.', 'error')
    return
  }

  if (newPassword.value.length < 6) {
    showToast('A nova password deve ter pelo menos 6 caracteres.', 'error')
    return
  }

  try {
    loadingPassword.value = true

    await auth.changePassword(currentPassword.value, newPassword.value)

    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''

    showToast('Password alterada com sucesso.', 'success')
  } catch (error) {
    console.error(error)
    showToast('Não foi possível alterar a password.', 'error')
  } finally {
    loadingPassword.value = false
  }
}
</script>

<template>
  <div class="profile-page">
    <Navbar />

    <main class="profile-wrapper">
      <div class="profile-header">
        <button class="back-button" @click="goBack">
          <ArrowLeft class="back-icon" />
          Voltar
        </button>

        <div class="profile-heading">
          <h1>Minha conta</h1>
          <p>Gere a tua informação pessoal, imagem de perfil e password.</p>
        </div>
      </div>

      <div class="profile-container">
        <aside class="profile-card profile-sidebar">
          <div class="sidebar-top">
            <div class="avatar-wrapper">
              <img
                  v-if="previewUrl"
                  :src="previewUrl"
                  alt="Profile"
                  class="profile-avatar"
              />
              <div v-else class="profile-avatar profile-placeholder">
                {{ auth.user?.username?.charAt(0)?.toUpperCase() }}
              </div>
            </div>

            <h2>{{ auth.user?.username || 'Utilizador' }}</h2>
            <p class="profile-role">{{ auth.user?.role || '-' }}</p>
          </div>

          <div class="sidebar-actions">
            <label class="upload-button">
              <Upload class="button-icon" />
              Escolher imagem
              <input type="file" accept="image/*" @change="handleImageChange" hidden />
            </label>

            <button class="save-button" @click="uploadImage" :disabled="loadingImage">
              {{ loadingImage ? 'A guardar...' : 'Atualizar imagem' }}
            </button>
          </div>
        </aside>

        <section class="profile-main">
          <div class="profile-card section-card">
            <div class="section-title">
              <User class="section-icon" />
              <h3>Informação da conta</h3>
            </div>

            <div class="info-grid">
              <div class="info-item">
                <span>Username</span>
                <strong>{{ auth.user?.username || '-' }}</strong>
              </div>

              <div class="info-item">
                <span>Role</span>
                <strong>{{ auth.user?.role || '-' }}</strong>
              </div>
            </div>
          </div>

          <div class="profile-card section-card">
            <div class="section-title">
              <Lock class="section-icon" />
              <h3>Alterar password</h3>
            </div>

            <div class="form-grid">
              <div class="input-group">
                <label for="currentPassword">Password atual</label>
                <input
                    id="currentPassword"
                    v-model="currentPassword"
                    type="password"
                    placeholder="Introduz a password atual"
                />
              </div>

              <div class="input-group">
                <label for="newPassword">Nova password</label>
                <input
                    id="newPassword"
                    v-model="newPassword"
                    type="password"
                    placeholder="Introduz a nova password"
                />
              </div>

              <div class="input-group">
                <label for="confirmPassword">Confirmar nova password</label>
                <input
                    id="confirmPassword"
                    v-model="confirmPassword"
                    type="password"
                    placeholder="Confirma a nova password"
                />
              </div>
            </div>

            <div class="section-actions">
              <button class="save-button" @click="changePassword" :disabled="loadingPassword">
                {{ loadingPassword ? 'A atualizar...' : 'Alterar password' }}
              </button>
            </div>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.profile-wrapper {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 24px 48px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 28px;
}

.back-button {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 44px;
  padding: 0 16px;
  border: 1px solid #d8dee8;
  border-radius: 12px;
  background: #ffffff;
  color: #4a4d45;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-button:hover {
  background: #f8faf7;
  border-color: #cfd8c2;
}

.back-icon {
  width: 18px;
  height: 18px;
}

.profile-heading h1 {
  margin: 0;
  font-size: 32px;
  line-height: 1.15;
  font-weight: 700;
  color: #363a32;
}

.profile-heading p {
  margin: 8px 0 0;
  font-size: 15px;
  color: #6b7280;
}

.profile-container {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

.profile-card {
  background: #ffffff;
  border: 1px solid #e9edf2;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.05);
}

.profile-sidebar {
  padding: 28px;
  height: fit-content;
  text-align: center;
}

.sidebar-top {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 18px;
}

.profile-avatar {
  width: 136px;
  height: 136px;
  border-radius: 12px;
  object-fit: cover;
  border: 4px solid #edf5e3;
  box-shadow: 0 8px 18px rgba(117, 183, 44, 0.12);
}

.profile-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #75b72c;
  color: #ffffff;
  font-size: 44px;
  font-weight: 700;
}

.profile-sidebar h2 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #31352d;
}

.profile-role {
  margin: 10px 0 0;
  color: #6b7280;
  font-size: 15px;
  font-weight: 600;
}

.sidebar-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 28px;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-card {
  padding: 28px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.section-title h3 {
  margin: 0;
  color: #3f4637;
  font-size: 22px;
  font-weight: 700;
}

.section-icon {
  width: 20px;
  height: 20px;
  color: #75b72c;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.info-item {
  background: #f8faf7;
  border: 1px solid #e6eed8;
  border-radius: 12px;
  padding: 16px;
}

.info-item span {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
}

.info-item strong {
  color: #374151;
  font-size: 16px;
}

.form-grid {
  display: grid;
  gap: 16px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-size: 14px;
  font-weight: 600;
  color: #4a4d45;
}

.input-group input {
  width: 100%;
  padding: 14px 15px;
  border: 1px solid #d9dee7;
  border-radius: 12px;
  font-size: 14px;
  background: #ffffff;
  transition: 0.2s ease;
  box-sizing: border-box;
}

.input-group input:focus {
  outline: none;
  border-color: #75b72c;
  box-shadow: 0 0 0 4px rgba(117, 183, 44, 0.12);
}

.section-actions {
  margin-top: 20px;
}

.upload-button,
.save-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  border-radius: 12px;
  padding: 13px 16px;
  font-weight: 700;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s ease;
}

.upload-button {
  background: #eef6e3;
  color: #5f8f29;
}

.upload-button:hover {
  background: #e6f1d7;
}

.save-button {
  width: 100%;
  background: #75b72c;
  color: white;
}

.save-button:hover {
  background: #6db11f;
}

.save-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.button-icon {
  width: 16px;
  height: 16px;
}

@media (max-width: 900px) {
  .profile-container {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .profile-wrapper {
    padding: 24px 16px 40px;
  }

  .profile-heading h1 {
    font-size: 28px;
  }

  .profile-sidebar,
  .section-card {
    padding: 22px;
  }

  .profile-avatar {
    width: 112px;
    height: 112px;
  }

  .profile-sidebar h2 {
    font-size: 28px;
  }
}
</style>