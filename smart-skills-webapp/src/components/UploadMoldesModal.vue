<template>
  <div class="modal-overlay" @click="handleClose">
    <div class="upload-modal" @click.stop>
      <div class="upload-modal-header">
        <div>
          <h2>Atualizar registos</h2>
          <p>Carrega um ou mais ficheiros Excel .xlsm e escolhe a company de cada um.</p>
        </div>

        <button class="upload-close-button" @click="handleClose" aria-label="Fechar">
          <X class="upload-close-icon" />
        </button>
      </div>

      <div
          class="upload-dropzone"
          :class="{ dragging: isDragging }"
          @dragenter.prevent="handleDragEnter"
          @dragover.prevent="handleDragOver"
          @dragleave.prevent="handleDragLeave"
          @drop.prevent="handleDrop"
      >
        <input
            ref="fileInput"
            type="file"
            multiple
            accept=".xlsm,application/vnd.ms-excel.sheet.macroEnabled.12"
            class="hidden-file-input"
            @change="handleFileChange"
        />

        <div class="upload-dropzone-content">
          <UploadCloud class="upload-dropzone-icon" />
          <h3>Arrasta os ficheiros aqui</h3>
          <p>ou seleciona manualmente um ou mais ficheiros .xlsm</p>

          <button type="button" class="select-file-button" @click="triggerFileInput">
            Escolher ficheiros
          </button>
        </div>
      </div>

      <div v-if="selectedFiles.length > 0" class="selected-files-list">
        <div
            v-for="item in selectedFiles"
            :key="item.id"
            class="selected-file-card"
        >
          <div class="selected-file-main">
            <div class="selected-file-info">
              <FileSpreadsheet class="selected-file-icon" />
              <div>
                <strong>{{ item.file.name }}</strong>
                <p>{{ formatFileSize(item.file.size) }}</p>
              </div>
            </div>

            <button class="remove-file-button" @click="removeSelectedFile(item.id)">
              Remover
            </button>
          </div>

          <div class="upload-form-group">
            <select
                :id="`company-${item.id}`"
                v-model="item.company"
                class="upload-select"
            >
              <option value="">Selecionar Empresa</option>
              <option
                  v-for="company in companies"
                  :key="company"
                  :value="company"
              >
                {{ company }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <p v-if="uploadError" class="upload-error">
        {{ uploadError }}
      </p>

      <div class="upload-modal-actions">
        <button class="secondary-button" @click="handleClose">
          Cancelar
        </button>

        <button
            class="primary-button"
            :disabled="!canUpload || uploading"
            @click="handleUpload"
        >
          {{ uploading ? 'A enviar...' : 'Enviar ficheiros' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { X, UploadCloud, FileSpreadsheet } from 'lucide-vue-next'
import { uploadMoldes } from '../services/moldeService.js'
import { useToast } from '../composables/useToast'

defineProps({
  companies: {
    type: Array,
    default: () => []
  }
})

const { showToast } = useToast()

const emit = defineEmits(['close', 'uploaded'])

const fileInput = ref(null)
const selectedFiles = ref([])
const isDragging = ref(false)
const uploadError = ref('')
const uploading = ref(false)

const canUpload = computed(() => {
  return (
      selectedFiles.value.length > 0 &&
      selectedFiles.value.every(item => item.file && item.company)
  )
})

const resetState = () => {
  selectedFiles.value = []
  isDragging.value = false
  uploadError.value = ''
  uploading.value = false

  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const handleClose = () => {
  if (uploading.value) return
  resetState()
  emit('close')
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const isValidXlsmFile = (file) => {
  if (!file) return false
  return file.name.toLowerCase().endsWith('.xlsm') || file.name.toLowerCase().endsWith('.xls')
}

const createFileItem = (file) => ({
  id: `${file.name}-${file.size}-${file.lastModified}-${Math.random().toString(36).slice(2, 8)}`,
  file,
  company: ''
})

const addFiles = (fileList) => {
  uploadError.value = ''

  const files = Array.from(fileList || [])
  if (!files.length) return

  const validFiles = []
  const invalidFiles = []

  files.forEach((file) => {
    if (isValidXlsmFile(file)) {
      validFiles.push(createFileItem(file))
    } else {
      invalidFiles.push(file.name)
    }
  })

  if (validFiles.length > 0) {
    selectedFiles.value.push(...validFiles)
  }

  if (invalidFiles.length > 0) {
    uploadError.value = `Só é permitido carregar ficheiros .xlsm. Inválidos: ${invalidFiles.join(', ')}`
  }
}

const handleFileChange = (event) => {
  addFiles(event.target.files)
}

const handleDragEnter = () => {
  isDragging.value = true
}

const handleDragOver = () => {
  isDragging.value = true
}

const handleDragLeave = (event) => {
  const currentTarget = event.currentTarget
  const relatedTarget = event.relatedTarget

  if (!currentTarget || !relatedTarget || !currentTarget.contains(relatedTarget)) {
    isDragging.value = false
  }
}

const handleDrop = (event) => {
  isDragging.value = false
  addFiles(event.dataTransfer?.files)
}

const removeSelectedFile = (id) => {
  selectedFiles.value = selectedFiles.value.filter(item => item.id !== id)
  uploadError.value = ''

  if (selectedFiles.value.length === 0 && fileInput.value) {
    fileInput.value.value = ''
  }
}

const formatFileSize = (bytes) => {
  if (!bytes && bytes !== 0) return '-'
  if (bytes < 1024) return `${bytes} B`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
}

const handleUpload = async () => {
  if (selectedFiles.value.length === 0) {
    uploadError.value = 'Seleciona pelo menos um ficheiro .xlsm.'
    alert(uploadError.value)
    return
  }

  const missingCompany = selectedFiles.value.some(item => !item.company)
  if (missingCompany) {
    uploadError.value = 'Seleciona uma empresa para cada ficheiro.'
    alert(uploadError.value)
    return
  }

  try {
    uploading.value = true
    uploadError.value = ''

    const payload = selectedFiles.value.map(item => ({
      file: item.file,
      company: item.company
    }))

    const result = await uploadMoldes(payload)
    console.log('UPLOAD RESULT:', result)

    showToast('Registos atualizados com sucesso.', 'success')

    emit('uploaded', result)
    resetState()
    emit('close')
  } catch (error) {
    console.error('Erro ao fazer upload:', error)
    uploadError.value = error.message || 'Não foi possível enviar os ficheiros. Tenta novamente.'

    showToast(uploadError.value, 'error')
    alert(uploadError.value)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  z-index: 999;
}

.upload-modal {
  width: min(680px, calc(100vw - 32px));
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.18);
  padding: 28px;
  box-sizing: border-box;
}

.upload-modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.upload-modal-header h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #2f312d;
}

.upload-modal-header p {
  margin: 8px 0 0;
  font-size: 15px;
  color: #6b6e67;
  line-height: 1.5;
}

.upload-close-button {
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 12px;
  background: #f3f4f2;
  color: #4a4d45;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s ease;
  flex-shrink: 0;
}

.upload-close-button:hover {
  background: #e9ebe7;
}

.upload-close-icon {
  width: 20px;
  height: 20px;
}

.hidden-file-input {
  display: none;
}

.upload-dropzone {
  border: 2px dashed #cfd5c7;
  border-radius: 12px;
  background: #fafcf8;
  padding: 36px 24px;
  transition: all 0.2s ease;
}

.upload-dropzone.dragging {
  border-color: #7cc12b;
  background: #f3fae9;
  box-shadow: 0 0 0 4px rgba(124, 193, 43, 0.1);
}

.upload-dropzone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.upload-dropzone-icon {
  width: 44px;
  height: 44px;
  color: #7cc12b;
  margin-bottom: 14px;
}

.upload-dropzone-content h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #30332e;
}

.upload-dropzone-content p {
  margin: 8px 0 20px;
  font-size: 14px;
  color: #6d7068;
}

.select-file-button {
  height: 46px;
  padding: 0 18px;
  border: none;
  border-radius: 12px;
  background: #7cc12b;
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s ease;
}

.select-file-button:hover {
  background: #6db11f;
}

.selected-files-list {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.selected-file-card {
  padding: 16px 18px;
  border: 1px solid #e2e5df;
  border-radius: 12px;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.selected-file-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.selected-file-info {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.selected-file-icon {
  width: 28px;
  height: 28px;
  color: #4f8f16;
  flex-shrink: 0;
}

.selected-file-info strong {
  display: block;
  font-size: 15px;
  color: #30332e;
  word-break: break-word;
}

.selected-file-info p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #767a72;
}

.upload-form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 0;
}

.upload-select {
  width: 100%;
  height: 48px;
  padding: 0 14px;
  border: 1px solid #dcdcdc;
  border-radius: 12px;
  background: #ffffff;
  font-size: 14px;
  color: #333333;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.upload-select:focus {
  border-color: #7cc12b;
  box-shadow: 0 0 0 3px rgba(124, 193, 43, 0.12);
}

.remove-file-button {
  border: none;
  background: transparent;
  color: #cf284a;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  flex-shrink: 0;
}

.upload-error {
  margin: 14px 0 0;
  color: #cf284a;
  font-size: 14px;
  font-weight: 600;
}

.upload-modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 26px;
}

.secondary-button,
.primary-button {
  height: 48px;
  padding: 0 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.secondary-button {
  border: 1px solid #d8ddd2;
  background: #ffffff;
  color: #55584f;
}

.secondary-button:hover {
  background: #f6f7f5;
}

.primary-button {
  border: none;
  background: #7cc12b;
  color: #ffffff;
}

.primary-button:hover {
  background: #6db11f;
}

.primary-button:disabled {
  background: #b9c7a7;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .upload-modal {
    padding: 20px;
    border-radius: 12px;
  }

  .upload-modal-header {
    margin-bottom: 20px;
  }

  .upload-modal-header h2 {
    font-size: 22px;
  }

  .upload-dropzone {
    padding: 28px 18px;
  }

  .selected-file-main {
    flex-direction: column;
    align-items: flex-start;
  }

  .selected-file-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .upload-modal-actions {
    flex-direction: column;
  }

  .secondary-button,
  .primary-button {
    width: 100%;
  }
}
</style>