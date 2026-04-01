<template>
  <div class="modal-overlay">
    <div class="modal-card">
      <div class="modal-header">
        <h2>{{ mold ? 'Editar Molde' : 'Adicionar Novo Molde' }}</h2>

        <button @click="$emit('close')" class="close-button">
          <X class="close-icon" />
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="wis">WIS *</label>
          <input
              id="wis"
              v-model="formData.wis"
              type="text"
              required
          />
        </div>

        <div class="form-group">
          <label for="order">Order *</label>
          <input
              id="order"
              v-model="formData.order"
              type="text"
          />
        </div>

        <div class="form-group">
          <label for="wkzBauerNr">WKZ Bauer Nr *</label>
          <input
              id="wkzBauerNr"
              v-model="formData.wkzBauerNr"
              type="text"
          />
        </div>

        <div class="form-group">
          <label for="responsibleDeveloper">Responsible Developer *</label>
          <input
              id="responsibleDeveloper"
              v-model="formData.responsibleDeveloper"
              type="text"
          />
        </div>

        <div class="form-group full-width">
          <label for="company">Company *</label>
          <select id="company" v-model="formData.company">
            <option value="">Selecionar...</option>
            <option
                v-for="company in companyList"
                :key="company"
                :value="company"
            >
              {{ company }}
            </option>
          </select>
        </div>

        <div class="form-group full-width">
          <label for="status">Status *</label>
          <select id="status" v-model="formData.status">
            <option value="">Selecionar...</option>
            <option
                v-for="status in statusList"
                :key="status"
                :value="status"
            >
              {{ status }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="poDate">PO Date *</label>
          <input
              id="poDate"
              v-model="formData.poDate"
              type="date"
          />
        </div>

        <div class="form-group">
          <label for="poDeliveryDate">PO Delivery Date *</label>
          <input
              id="poDeliveryDate"
              v-model="formData.poDeliveryDate"
              type="date"
          />
        </div>

        <div class="form-group">
          <label for="anticipatedDeliveryDate">Anticipated Delivery Date *</label>
          <input
              id="anticipatedDeliveryDate"
              v-model="formData.anticipatedDeliveryDate"
              type="date"
          />
        </div>

        <div class="form-group">
          <label for="anticipatedEndDate">Anticipated End Date *</label>
          <input
              id="anticipatedEndDate"
              v-model="formData.anticipatedEndDate"
              type="date"
          />
        </div>

        <div class="form-group full-width">
          <label for="image">Image URL (Opcional)</label>
          <input
              id="image"
              v-model="formData.image"
              type="text"
          />
        </div>

        <div class="form-group full-width">
          <label for="statusRemarks">Status Remarks *</label>
          <textarea
              id="statusRemarks"
              v-model="formData.statusRemarks"
              rows="3"
          />
        </div>

        <div class="form-actions">
          <button type="button" @click="$emit('close')" class="secondary-button">
            Cancelar
          </button>

          <button type="submit" class="primary-button">
            {{ mold ? 'Salvar Alterações' : 'Adicionar Molde' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { X } from 'lucide-vue-next'
import { getCompany, getStatus } from '@/services/MoldeService.js'

const statusList = ref([])
const companyList = ref([])
const loading = ref(true)

const props = defineProps({
  mold: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['save', 'close'])

onMounted(async () => {
  try {
    statusList.value = await getStatus()
    companyList.value = await getCompany()
  } catch (error) {
    console.error('Erro ao carregar status e ou company:', error)
  } finally {
    loading.value = false
  }
})

const formData = reactive({
  wis: props.mold?.wis || '',
  order: props.mold?.order || '',
  poDate: props.mold?.poDate || '',
  poDeliveryDate: props.mold?.poDeliveryDate || '',
  anticipatedDeliveryDate: props.mold?.anticipatedDeliveryDate || '',
  poQualificationEndDate: props.mold?.poQualificationEndDate || '',
  anticipatedQualificationEndDate: props.mold?.anticipatedQualificationEndDate || '',
  status: props.mold?.status || '',
  date: props.mold?.date || '',
  anticipatedEndDate: props.mold?.anticipatedEndDate || '',
  statusRemarks: props.mold?.statusRemarks || '',
  responsibleDeveloper: props.mold?.responsibleDeveloper || '',
  wkzBauerNr: props.mold?.wkzBauerNr || '',
  company: props.mold?.company || '',
  image: props.mold?.image || ''
})

const handleSubmit = () => {
  emit('save', { ...formData })
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

.modal-card {
  width: 100%;
  max-width: 1200px;
  max-height: 90vh;
  overflow-y: auto;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modal-header {
  position: sticky;
  top: 0;
  background: #ffffff;
  padding: 24px 28px;
  border-bottom: 1px solid #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #4a4d45;
}

.close-button {
  width: 42px;
  height: 42px;
  border: none;
  background: transparent;
  border-radius: 12px;
  cursor: pointer;
  color: #666666;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease, color 0.2s ease;
}

.close-button:hover {
  background: #f3f4f6;
  color: #333333;
}

.close-icon {
  width: 22px;
  height: 22px;
}

.modal-form {
  padding: 28px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  margin-bottom: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #4a4d45;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 13px 15px;
  border: 1px solid #dcdcdc;
  border-radius: 12px;
  background: #ffffff;
  font-size: 15px;
  color: #333333;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: #7cc12b;
  box-shadow: 0 0 0 3px rgba(124, 193, 43, 0.18);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
  grid-column: 1 / -1;
}

.secondary-button,
.primary-button {
  height: 48px;
  flex: 1;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.secondary-button {
  background: #ffffff;
  border: 1px solid #dddddd;
  color: #4a4d45;
}

.secondary-button:hover {
  background: #f7f7f7;
}

.primary-button {
  background: #7cc12b;
  color: #ffffff;
}

.primary-button:hover {
  background: #6eb023;
}

@media (max-width: 768px) {
  .modal-form {
    grid-template-columns: 1fr;
  }

  .form-group.full-width {
    grid-column: auto;
  }

  .modal-header,
  .modal-form {
    padding-left: 20px;
    padding-right: 20px;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>