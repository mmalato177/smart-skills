<template>
  <div class="mold-detail-page">
    <Navbar />

    <div v-if="loading" class="mold-detail-state-wrapper">
      <div class="mold-detail-state-card">
        <h2>A carregar...</h2>
      </div>
    </div>

    <div v-else-if="!molde" class="mold-detail-state-wrapper">
      <div class="mold-detail-state-card">
        <h2>Molde não encontrado</h2>
        <button @click="router.push('/')" class="mold-detail-primary-button">
          Voltar para início
        </button>
      </div>
    </div>

    <main v-else class="mold-detail-content">
      <div class="mold-detail-header-bar">
        <button class="mold-detail-back-button" @click="goBack">
          <ArrowLeft class="mold-detail-back-icon" />
          Voltar
        </button>
      </div>

      <section class="mold-detail-layout">
        <aside class="mold-detail-card mold-detail-image-card">
          <div class="mold-detail-image-card-header">
            <h2>Imagem do molde</h2>
          </div>

          <div class="mold-detail-image-wrap">
            <img
                v-if="molde.image"
                :src="molde.image"
                :alt="molde.order || molde.wis"
                class="mold-detail-image"
            />

            <div v-else class="mold-detail-image-placeholder">
              <Package class="mold-detail-placeholder-icon" />
              <p>Sem imagem disponível</p>
            </div>
          </div>
        </aside>

        <section class="mold-detail-card mold-detail-info-card">
          <div class="mold-detail-top">
            <div class="mold-detail-top-left">
              <p class="mold-detail-eyebrow">Detalhes do molde</p>
              <h1 class="mold-detail-title">WIS: {{ molde.wis || '-' }}</h1>
              <p class="mold-detail-subtitle">
                <span>Wkz-Bauer Nr:</span> {{ molde.wkzBauerNr || '-' }}
              </p>
            </div>

            <div
                class="mold-detail-status-badge"
                :class="getStatusClass(molde.status)"
            >
              {{ molde.status || 'Sem estado' }}
            </div>
          </div>

          <div class="mold-detail-section">
            <h2>Informação principal</h2>

            <div class="mold-detail-info-grid">
              <div class="mold-detail-info-item">
                <div class="mold-detail-info-icon-wrap">
                  <Factory class="mold-detail-info-icon" />
                </div>
                <div class="mold-detail-info-text">
                  <span>Company</span>
                  <strong>{{ molde.company || '-' }}</strong>
                </div>
              </div>

              <div class="mold-detail-info-item">
                <div class="mold-detail-info-icon-wrap">
                  <User class="mold-detail-info-icon" />
                </div>
                <div class="mold-detail-info-text">
                  <span>Responsible Developer</span>
                  <strong>{{ molde.responsibleDeveloper || '-' }}</strong>
                </div>
              </div>

              <div class="mold-detail-info-item">
                <div class="mold-detail-info-icon-wrap">
                  <Package class="mold-detail-info-icon" />
                </div>
                <div class="mold-detail-info-text">
                  <span>Order</span>
                  <strong>{{ molde.order || '-' }}</strong>
                </div>
              </div>

              <div class="mold-detail-info-item">
                <div class="mold-detail-info-icon-wrap">
                  <Calendar class="mold-detail-info-icon" />
                </div>
                <div class="mold-detail-info-text">
                  <span>Date</span>
                  <strong>{{ formatDate(molde.date) }}</strong>
                </div>
              </div>
            </div>
          </div>

          <div class="mold-detail-section">
            <h2>Datas de acompanhamento</h2>

            <div class="mold-detail-technical-grid">
              <div class="mold-detail-technical-card">
                <span>PO Date</span>
                <strong>{{ formatDate(molde.poDate) }}</strong>
              </div>

              <div class="mold-detail-technical-card">
                <span>PO Delivery Date</span>
                <strong>{{ formatDate(molde.poDeliveryDate) }}</strong>
              </div>

              <div class="mold-detail-technical-card">
                <span>Anticipated Delivery Date</span>
                <strong>{{ formatDate(molde.anticipatedDeliveryDate) }}</strong>
              </div>

              <div class="mold-detail-technical-card">
                <span>PO Qualification Date</span>
                <strong>{{ formatDate(molde.poQualificationEndDate) }}</strong>
              </div>

              <div class="mold-detail-technical-card">
                <span>Anticipated Qualification Date</span>
                <strong>{{ formatDate(molde.anticipatedQualificationEndDate) }}</strong>
              </div>

              <div class="mold-detail-technical-card">
                <span>Anticipated End Date</span>
                <strong>{{ formatDate(molde.anticipatedEndDate) }}</strong>
              </div>
            </div>
          </div>

          <div class="mold-detail-section">
            <h2>Observações</h2>

            <div class="mold-detail-remarks-card">
              <div class="mold-detail-remarks-header">
                <NotebookPen class="mold-detail-remarks-icon" />
                <span>Status Remarks</span>
              </div>
              <p>{{ molde.statusRemarks || '-' }}</p>
            </div>
          </div>
        </section>
      </section>
    </main>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeft,
  Ruler,
  Package,
  Calendar,
  User,
  Factory,
  NotebookPen,
  PackageCheck,
  ShieldCheck,
  CalendarCheck
} from 'lucide-vue-next'
import { getMoldeByWKZ } from '../services/MoldeService'
import Navbar from '@/components/Navbar.vue'

const route = useRoute()
const router = useRouter()

const molde = ref(null)
const loading = ref(true)

const goBack = () => {
  router.back()
}

onMounted(async () => {
  try {
    const wkz = route.params.wkz
    molde.value = await getMoldeByWKZ(wkz)
  } catch (error) {
    console.error('Erro ao carregar molde:', error)
  } finally {
    loading.value = false
  }
})

const getStatusClass = (status) => {
  switch (status) {
    case 'Manufacturing':
      return 'status-blue'
    case 'Order':
      return 'status-orange'
    case 'Sampling':
      return 'status-purple'
    case 'Waiting for WAGO':
      return 'status-gray'
    case 'Finished':
      return 'status-green'
    default:
      return 'status-default'
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
.mold-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.mold-detail-content {
  max-width: 1180px;
  margin: 0 auto;
  padding: 32px 24px 48px;
}

.mold-detail-header-bar {
  margin-bottom: 20px;
}

.mold-detail-back-button {
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

.mold-detail-back-button:hover {
  background: #f8faf7;
  border-color: #cfd8c2;
}

.mold-detail-back-icon {
  width: 18px;
  height: 18px;
}

.mold-detail-layout {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}

.mold-detail-card {
  background: #ffffff;
  border: 1px solid #e9edf2;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.05);
}

.mold-detail-image-card {
  overflow: hidden;
}

.mold-detail-image-card-header {
  padding: 24px 24px 0;
}

.mold-detail-image-card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #3f4637;
}

.mold-detail-image-wrap {
  min-height: 520px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #f8fbf3 0%, #f4f7fb 55%, #f2f4f7 100%);
}

.mold-detail-image {
  max-width: 100%;
  max-height: 420px;
  object-fit: contain;
  border-radius: 12px;
}

.mold-detail-image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-align: center;
  color: #98a2b3;
}

.mold-detail-placeholder-icon {
  width: 64px;
  height: 64px;
}

.mold-detail-image-placeholder p {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.mold-detail-info-card {
  padding: 28px;
}

.mold-detail-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 28px;
}

.mold-detail-eyebrow {
  margin: 0 0 6px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #75b72c;
}

.mold-detail-title {
  margin: 0;
  font-size: 34px;
  line-height: 1.15;
  font-weight: 700;
  color: #363a32;
}

.mold-detail-subtitle {
  margin: 10px 0 0;
  font-size: 15px;
  color: #6b7280;
}

.mold-detail-subtitle span {
  font-weight: 700;
  color: #4a4d45;
}

.mold-detail-section + .mold-detail-section {
  margin-top: 28px;
}

.mold-detail-section h2 {
  margin: 0 0 16px;
  font-size: 22px;
  font-weight: 700;
  color: #3f4637;
}

.mold-detail-info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.mold-detail-info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8faf7;
  border: 1px solid #e6eed8;
  border-radius: 12px;
  min-width: 0;
}

.mold-detail-info-icon-wrap {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: #eaf4de;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mold-detail-info-icon {
  width: 18px;
  height: 18px;
  color: #75b72c;
}

.mold-detail-info-text {
  min-width: 0;
}

.mold-detail-info-text span {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
}

.mold-detail-info-text strong {
  display: block;
  font-size: 15px;
  font-weight: 700;
  color: #374151;
  word-break: break-word;
}

.mold-detail-technical-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.mold-detail-technical-card {
  padding: 16px;
  border: 1px solid #e9edf2;
  border-radius: 12px;
  background: #ffffff;
  min-width: 0;
}

.mold-detail-technical-card span {
  display: block;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

.mold-detail-technical-card strong {
  display: block;
  font-size: 15px;
  color: #374151;
  word-break: break-word;
}

.mold-detail-remarks-card {
  padding: 18px;
  border: 1px solid #e9edf2;
  border-radius: 12px;
  background: #f9fbf7;
}

.mold-detail-remarks-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #4a4d45;
  font-weight: 700;
  font-size: 14px;
}

.mold-detail-remarks-icon {
  width: 17px;
  height: 17px;
  color: #75b72c;
}

.mold-detail-remarks-card p {
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
  color: #4b5563;
  white-space: pre-wrap;
  word-break: break-word;
}

.mold-detail-status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}

.status-blue {
  background: #e0f2fe;
  color: #0369a1;
}

.status-orange {
  background: #fff7ed;
  color: #c2410c;
}

.status-purple {
  background: #f3e8ff;
  color: #7e22ce;
}

.status-gray {
  background: #f3f4f6;
  color: #374151;
}

.status-green {
  background: #dcfce7;
  color: #166534;
}

.status-default {
  background: #eef2ff;
  color: #3730a3;
}

.mold-detail-state-wrapper {
  min-height: calc(100vh - 88px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.mold-detail-state-card {
  text-align: center;
  background: #ffffff;
  border: 1px solid #e9edf2;
  border-radius: 12px;
  padding: 36px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.05);
}

.mold-detail-state-card h2 {
  margin: 0;
  color: #3f4637;
  font-size: 24px;
}

.mold-detail-primary-button {
  margin-top: 20px;
  height: 46px;
  padding: 0 24px;
  border: none;
  border-radius: 12px;
  background: #75b72c;
  color: #ffffff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}

.mold-detail-primary-button:hover {
  background: #6db11f;
}

@media (max-width: 1100px) {
  .mold-detail-layout {
    grid-template-columns: 1fr;
  }

  .mold-detail-image-wrap {
    min-height: 320px;
  }
}

@media (max-width: 768px) {
  .mold-detail-content {
    padding: 24px 16px 40px;
  }

  .mold-detail-info-card {
    padding: 22px;
  }

  .mold-detail-title {
    font-size: 28px;
  }

  .mold-detail-top {
    flex-direction: column;
    align-items: flex-start;
  }

  .mold-detail-info-grid,
  .mold-detail-technical-grid {
    grid-template-columns: 1fr;
  }

  .mold-detail-image-wrap {
    min-height: 260px;
    padding: 20px;
  }
}
</style>
