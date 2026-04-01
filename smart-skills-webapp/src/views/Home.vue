<template>
  <div class="molds-page">
    <Navbar/>

    <main class="page-content">
      <div class="page-header">
        <p class="molds-count">
          {{ filteredMoldes.length }}
          {{ filteredMoldes.length === 1 ? 'molde registado' : 'moldes registados' }}
        </p>

        <button class="add-button" @click="isUploadModalOpen = true">
          <Upload class="button-icon"/>
          Atualizar registos
        </button>
      </div>

      <div v-if="!loading" class="toolbar">
        <div class="toolbar-top">
          <div class="company-tabs">
            <button
                v-for="company in companyTabs"
                :key="company"
                :class="{ active: selectedCompany === company }"
                class="company-tab"
                @click="selectedCompany = company"
            >
              {{ company }}
              <span class="tab-count">{{ getCompanyCount(company) }}</span>
            </button>
          </div>
        </div>

        <div class="filters-card">
          <div class="filters-section">
            <div class="filters-section-header">
              <h3>Estado</h3>
            </div>

            <div class="status-filters">
              <label
                  v-for="status in statusOptions"
                  :key="status"
                  :class="[
      getStatusClass(status),
      { selected: selectedStatuses.includes(status) }
    ]"
                  class="status-filter-chip"
              >
                <input
                    v-model="selectedStatuses"
                    :value="status"
                    class="status-filter-input"
                    type="checkbox"
                />

                <span class="status-filter-check">
      <svg aria-hidden="true" fill="none" viewBox="0 0 16 16">
        <path
            d="M3.5 8.5L6.5 11.5L12.5 4.5"
            stroke="currentColor"
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
        />
      </svg>
    </span>
                <span class="status-filter-label">{{ status }}</span>
              </label>
            </div>
          </div>

          <div class="filters-divider"></div>

          <div class="filters-section">
            <div class="filters-row">
              <div class="search-box">
                <Search class="search-icon"/>
                <input
                    v-model="searchTerm"
                    class="search-input"
                    placeholder="Pesquisar por WIS, company, status, developer..."
                    type="text"
                />
              </div>

              <div class="date-filter">
                <label for="startDate">De</label>
                <input
                    id="startDate"
                    v-model="startDate"
                    class="date-input"
                    type="date"
                />
              </div>

              <div class="date-filter">
                <label for="endDate">Até</label>
                <input
                    id="endDate"
                    v-model="endDate"
                    class="date-input"
                    type="date"
                />
              </div>

              <button
                  v-if="selectedCompany !== 'Todos' || searchTerm || startDate || endDate || selectedStatuses.length"
                  class="clear-filters-button"
                  @click="clearFilters"
              >
                <X class="clear-icon"/>
                Limpar filtros
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="loading" class="empty-state">
        <h2>A carregar...</h2>
      </div>

      <div v-else-if="filteredMoldes.length > 0" class="molds-list">
        <article
            v-for="molde in displayedMoldes"
            :key="molde.wis"
            class="mold-card"
        >
          <router-link :to="`/molde/${molde.wkzBauerNr}`" class="mold-card-link">
            <div class="mold-image-wrap">
              <img
                  v-if="molde.image"
                  :alt="molde.order"
                  :src="molde.image"
                  class="mold-image"
              />

              <div v-else class="mold-image-placeholder">
                <Package class="mold-placeholder-icon"/>
              </div>
            </div>

            <div class="mold-info">
              <div class="mold-header">
                <h2 class="mold-title">WIS: {{ molde.wis }}</h2>

                <div :class="getStatusClass(molde.status)" class="status-badge">
                  {{ molde.status }}
                </div>
              </div>

              <p class="mold-description"><b>Wkz-Bauer Nr:</b> {{ molde.wkzBauerNr || '-' }}</p>

              <div class="mold-meta">
                <p>
                  <span>Company:</span> {{ molde.company || '-' }}
                </p>
                <p>
                  <span>Developer:</span> {{ molde.responsibleDeveloper || '-' }}
                </p>
                <p>
                  <span>Anticipated end date:</span> {{ formatDate(molde.anticipatedEndDate) }}
                </p>
              </div>
            </div>
          </router-link>

          <div class="mold-actions">
            <button class="edit-button" @click="openEditForm(molde)">
              <Pencil class="action-icon"/>
              Editar
            </button>

            <button
                aria-label="Remover molde"
                class="delete-button"
                title="Remover molde"
                @click="handleDeleteMolde(molde.wkzBauerNr)"
            >
              <Trash2 class="action-icon"/>
            </button>
          </div>
        </article>
      </div>

      <p class="pagination-info">
        A mostrar {{ displayedMoldes.length }} de {{ filteredMoldes.length }} moldes
      </p>
      <div
          v-if="filteredMoldes.length > itemsPerPage"
          class="pagination"
      >
        <button
            class="pagination-button"
            :disabled="currentPage === 1"
            @click="goToPreviousPage"
        >
          Anterior
        </button>


        <div class="pagination-pages">
          <button
              v-for="page in totalPages"
              :key="page"
              class="pagination-page"
              :class="{ active: currentPage === page }"
              @click="goToPage(page)"
          >
            {{ page }}
          </button>
        </div>

        <button
            class="pagination-button"
            :disabled="currentPage === totalPages"
            @click="goToNextPage"
        >
          Seguinte
        </button>
      </div>

      <div v-else class="empty-state">
        <h2>Nenhum molde encontrado</h2>
        <p>
          {{
            selectedCompany === 'Todos' && !searchTerm && !startDate && !endDate
                ? 'Adiciona o primeiro molde para começar.'
                : 'Não existem moldes com os filtros aplicados.'
          }}
        </p>

        <button
            v-if="selectedCompany !== 'Todos' || searchTerm || startDate || endDate"
            class="add-button empty-button"
            @click="clearFilters"
        >
          Limpar filtros
        </button>
      </div>
    </main>

    <MoldeForm
        v-if="isFormOpen"
        :mold="editingMolde"
        @close="closeForm"
        @save="editingMolde ? handleEditMolde : handleAddMolde"
    />

    <UploadMoldesModal
        v-if="isUploadModalOpen"
        :companies="companies"
        @close="isUploadModalOpen = false"
        @uploaded="handleUploadSuccess"
    />
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {Package, Pencil, Search, Trash2, Upload, X} from 'lucide-vue-next'
import MoldeForm from '../components/MoldeForm.vue'
import UploadMoldesModal from '../components/UploadMoldesModal.vue'
import {getCompany, getMoldes, getStatus} from '../services/moldeService.js'
import Navbar from '@/components/Navbar.vue'

const moldes = ref([])
const companies = ref([])
const statuses = ref([])
const loading = ref(true)

const isFormOpen = ref(false)
const editingMolde = ref(null)
const isUploadModalOpen = ref(false)

const selectedCompany = ref('Todos')
const selectedStatuses = ref([])
const searchTerm = ref('')
const startDate = ref('')
const endDate = ref('')

const currentPage = ref(1)
const itemsPerPage = 10

onMounted(async () => {
  try {
    const [moldesData, companiesData, statusesData] = await Promise.all([
      getMoldes(),
      getCompany(),
      getStatus()
    ])

    moldes.value = moldesData || []
    companies.value = companiesData || []
    statuses.value = statusesData || []
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  } finally {
    loading.value = false
  }
})

const companyTabs = computed(() => {
  return ['Todos', ...companies.value]
})

const statusOptions = computed(() => {
  return statuses.value || []
})

const filteredMoldes = computed(() => {
  let result = [...moldes.value]

  if (selectedCompany.value !== 'Todos') {
    result = result.filter((m) => m.company === selectedCompany.value)
  }

  if (selectedStatuses.value.length > 0) {
    result = result.filter((m) => selectedStatuses.value.includes(m.status))
  }

  const term = searchTerm.value.trim().toLowerCase()
  if (term) {
    result = result.filter((m) => {
      return (
          String(m.wis || '').toLowerCase().includes(term) ||
          String(m.wkzBauerNr || '').toLowerCase().includes(term) ||
          String(m.company || '').toLowerCase().includes(term) ||
          String(m.status || '').toLowerCase().includes(term) ||
          String(m.responsibleDeveloper || '').toLowerCase().includes(term)
      )
    })
  }

  if (startDate.value) {
    const selectedStart = new Date(startDate.value)
    selectedStart.setHours(0, 0, 0, 0)

    result = result.filter((m) => {
      if (!m.anticipatedEndDate) return false
      const moldeDate = new Date(m.anticipatedEndDate)
      return moldeDate >= selectedStart
    })
  }

  if (endDate.value) {
    const selectedEnd = new Date(endDate.value)
    selectedEnd.setHours(23, 59, 59, 999)

    result = result.filter((m) => {
      if (!m.anticipatedEndDate) return false
      const moldeDate = new Date(m.anticipatedEndDate)
      return moldeDate <= selectedEnd
    })
  }

  return result
})

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredMoldes.value.length / itemsPerPage))
})

const displayedMoldes = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage

  return filteredMoldes.value.slice(start, end)
})

watch(
    [selectedCompany, selectedStatuses, searchTerm, startDate, endDate],
    () => {
      currentPage.value = 1
    }
)
watch(totalPages, (newTotalPages) => {
  if (currentPage.value > newTotalPages) {
    currentPage.value = newTotalPages
  }
})

const goToPreviousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const goToNextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const goToPage = (page) => {
  currentPage.value = page
}

const getCompanyCount = (company) => {
  if (company === 'Todos') return moldes.value.length
  return moldes.value.filter((m) => m.company === company).length
}

const clearFilters = () => {
  selectedCompany.value = 'Todos'
  selectedStatuses.value = []
  searchTerm.value = ''
  startDate.value = ''
  endDate.value = ''
}

const handleAddMolde = (molde) => {
  moldes.value.push(molde)
  isFormOpen.value = false
}

const handleEditMolde = (molde) => {
  if (!editingMolde.value) return

  const index = moldes.value.findIndex(
      (m) => m.wkzBauerNr === editingMolde.value.wkzBauerNr
  )

  if (index !== -1) {
    moldes.value[index] = {
      ...moldes.value[index],
      ...molde,
      wkzBauerNr: editingMolde.value.wkzBauerNr
    }
  }

  editingMolde.value = null
  isFormOpen.value = false
}

const handleDeleteMolde = (wkzBauerNr) => {
  if (confirm('Tem certeza que deseja remover este molde?')) {
    moldes.value = moldes.value.filter((m) => m.wkzBauerNr !== wkzBauerNr)
  }
}

const openEditForm = (molde) => {
  editingMolde.value = molde
  isFormOpen.value = true
}

const closeForm = () => {
  isFormOpen.value = false
  editingMolde.value = null
}

const getStatusClass = (status) => {
  switch (status) {
    case 'Order':
      return 'status-order'

    case 'Design':
      return 'status-design'

    case 'Manufacturing':
      return 'status-manufacturing'

    case 'Qualification Process':
      return 'status-qualification'

    case 'Endurance Run':
      return 'status-endurance'

    case 'Sampling':
      return 'status-sampling'

    case 'Waiting For Wago':
      return 'status-waiting'

    case 'Changes by Wago':
      return 'status-changes'

    case 'Spare Parts Production':
      return 'status-spare'

    case 'Correction':
      return 'status-correction'

    case 'Done':
      return 'status-done'

    default:
      return 'status-default'
  }
}
const handleUploadSuccess = async () => {
  try {
    loading.value = true
    moldes.value = await getMoldes()
  } catch (error) {
    console.error('Erro ao recarregar moldes após upload:', error)
  } finally {
    loading.value = false
    isUploadModalOpen.value = false
  }
}

function formatDate(date) {
  if (!date) return '-'

  const d = new Date(date)

  if (Number.isNaN(d.getTime())) return '-'

  const day = String(d.getDate()).padStart(2, '0')
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const year = d.getFullYear()

  return `${day}/${month}/${year}`
}
</script>

<style scoped>
.molds-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 40px 32px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
}

.molds-count {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #666666;
}

.add-button {
  height: 48px;
  padding: 0 28px;
  border: none;
  border-radius: 12px;
  background: #7cc12b;
  color: #ffffff;
  font-size: 16px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: background 0.2s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

.add-button:hover {
  background: #6eb023;
}

.button-icon {
  width: 18px;
  height: 18px;
}

.toolbar {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 28px;
}

.toolbar-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.company-tabs {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 4px;
  scrollbar-width: thin;
}

.company-tab {
  flex: 0 0 auto;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 42px;
  padding: 0 16px;
  border: 1px solid #dddddd;
  border-radius: 12px;
  background: #ffffff;
  color: #4a4d45;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.company-tab:hover {
  background: #f7f7f7;
}

.company-tab.active {
  background: #7cc12b;
  border-color: #7cc12b;
  color: #ffffff;
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 22px;
  padding: 0 7px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.08);
  font-size: 12px;
  font-weight: 700;
}

.company-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.22);
}

.filters-card {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 22px;
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.filters-section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.filters-section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #4a4d45;
}

.filters-divider {
  height: 1px;
  background: #efefef;
}
.status-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.status-filter-chip {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 38px;
  padding: 0 12px;
  border: 1px solid transparent;
  border-radius: 999px;
  cursor: pointer;
  user-select: none;
  transition: all 0.18s ease;
}

.status-filter-chip:hover {
  transform: translateY(-1px);
}

.status-filter-input {
  position: absolute;
  opacity: 0;
  pointer-events: none;
}

.status-filter-check {
  width: 16px;
  height: 16px;
  border-radius: 5px;
  border: 1.5px solid currentColor;
  background: transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.18s ease;
}

.status-filter-check svg {
  width: 10px;
  height: 10px;
  opacity: 0;
  transform: scale(0.7);
  transition: all 0.18s ease;
}

.status-filter-label {
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
}

.status-filter-chip.selected {
  border-color: currentColor;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.status-filter-chip.selected .status-filter-check {
  background: currentColor;
  color: #ffffff;
}

.status-filter-chip.selected .status-filter-check svg {
  opacity: 1;
  transform: scale(1);
  width: 20px;
  height: 20px;
  color:#4a4d45;

}

.filters-row {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) 160px 160px auto;
  gap: 14px;
  align-items: end;
}

.search-box {
  position: relative;
  min-width: 0;
}

.search-icon {
  position: absolute;
  top: 50%;
  left: 14px;
  width: 18px;
  height: 18px;
  color: #8a8d85;
  transform: translateY(-50%);
}

.search-input {
  width: 100%;
  height: 46px;
  padding: 0 14px 0 44px;
  border: 1px solid #dcdcdc;
  border-radius: 12px;
  background: #ffffff;
  font-size: 14px;
  color: #333333;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.search-input:focus,
.date-input:focus {
  border-color: #7cc12b;
  box-shadow: 0 0 0 3px rgba(124, 193, 43, 0.12);
}

.date-filter {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.date-filter label {
  font-size: 13px;
  font-weight: 600;
  color: #4a4d45;
}

.date-input {
  height: 46px;
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

.clear-filters-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 46px;
  padding: 0 16px;
  border: 1px solid #dcdcdc;
  border-radius: 12px;
  background: #ffffff;
  color: #555555;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.clear-filters-button:hover {
  background: #f6f6f6;
}

.clear-icon {
  width: 16px;
  height: 16px;
}

.molds-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.mold-card {
  display: flex;
  min-height: 180px;
  background: #ffffff;
  border: 1px solid #dddddd;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.mold-card-link {
  flex: 1;
  min-width: 0;
  display: flex;
  color: inherit;
  text-decoration: none;
}

.mold-image-wrap {
  width: 220px;
  min-width: 220px;
  overflow: hidden;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mold-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.mold-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f7f8;
}

.mold-placeholder-icon {
  width: 52px;
  height: 52px;
  color: #9ca3af;
}

.mold-info {
  flex: 1;
  min-width: 0;
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.mold-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.mold-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.mold-description {
  margin: 0 0 18px;
  font-size: 14px;
  line-height: 1.7;
  color: #6a6a6a;
}

.mold-meta p {
  margin: 0 0 4px;
  font-size: 14px;
  line-height: 1.6;
  color: #666666;
}

.mold-meta span {
  font-weight: 700;
  color: #575757;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-order {
  background: #fff4e6;
  color: #c2410c;
}

.status-design {
  background: #fce7f3;
  color: #be185d;
}

.status-manufacturing {
  background: #e0f2fe;
  color: #0369a1;
}

.status-qualification {
  background: #eef2ff;
  color: #3730a3;
}

.status-endurance {
  background: #e0e7ff;
  color: #4338ca;
}

.status-sampling {
  background: #f3e8ff;
  color: #7e22ce;
}

.status-waiting {
  background: #f1f5f9;
  color: #334155;
}

.status-changes {
  background: #fef3c7;
  color: #92400e;
}

.status-spare {
  background: #ecfccb;
  color: #3f6212;
}

.status-correction {
  background: #fee2e2;
  color: #b91c1c;
}

.status-done {
  background: #dcfce7;
  color: #166534;
}

.mold-actions {
  width: 140px;
  flex-shrink: 0;
  padding: 20px;
  border-left: 1px solid #e3e3e3;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
}

.edit-button,
.delete-button {
  height: 40px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.edit-button {
  width: 100%;
  background: #5a5d54;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.edit-button:hover {
  background: #4a4d45;
}

.delete-button {
  width: 100%;
  background: #df1844;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.delete-button:hover {
  background: #c9143c;
}

.action-icon {
  width: 16px;
  height: 16px;
}

.empty-state {
  padding: 80px 32px;
  background: #ffffff;
  border: 1px dashed #d8d8d8;
  border-radius: 12px;
  text-align: center;
}

.empty-state h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #4a4d45;
}

.empty-state p {
  margin: 12px 0 0;
  font-size: 15px;
  color: #6a6a6a;
}

.empty-button {
  margin-top: 24px;
}

@media (max-width: 1024px) {
  .page-header {
    gap: 16px;
  }

  .mold-card {
    overflow: hidden;
  }

  .mold-card-link {
    flex-direction: column;
  }

  .mold-image-wrap {
    width: 100%;
    height: 220px;
  }

  .mold-actions {
    width: 100%;
    border-left: none;
    border-top: 1px solid #ececec;
    padding: 16px 20px;
    justify-content: flex-end;
  }

  .mold-card,
  .mold-card-link {
    flex-direction: column;
  }

  .mold-image-wrap {
    width: 100%;
    height: 240px;
  }

  .mold-actions {
    width: 100%;
    border-left: none;
    border-top: 1px solid #e3e3e3;
    flex-direction: row;
    align-items: center;
  }

  .edit-button,
  .delete-button {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .page-content {
    padding: 24px 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .molds-count {
    font-size: 18px;
  }

  .add-button {
    width: 100%;
    justify-content: center;
  }

  .filters-row {
    display: flex;
    flex-direction: column;
    align-items: stretch;
  }

  .search-box,
  .date-filter,
  .clear-filters-button {
    width: 100%;
  }

  .mold-info {
    padding: 18px;
  }

  .mold-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .mold-title {
    font-size: 20px;
  }

  .mold-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .edit-button,
  .delete-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .mold-image-wrap {
    height: 180px;
  }

  .company-tab {
    height: 40px;
    padding: 0 14px;
    font-size: 13px;
  }

  .search-input,
  .date-input,
  .clear-filters-button {
    height: 44px;
  }
}

.pagination {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.pagination-pages {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-button,
.pagination-page {
  height: 40px;
  min-width: 40px;
  padding: 0 14px;
  border: 1px solid #dddddd;
  border-radius: 10px;
  background: #ffffff;
  color: #4a4d45;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-button:hover,
.pagination-page:hover {
  background: #f7f7f7;
}

.pagination-page.active {
  background: #7cc12b;
  border-color: #7cc12b;
  color: #ffffff;
}

.pagination-button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  background: #f5f5f5;
}

.pagination-info {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666666;
}
</style>