<template>
  <div class="error-screen">
    <div class="card">
      <div class="icon">⚠️</div>
      <h1>{{ t('ui.error_title') }}</h1>
      <p>{{ t('ui.error_description') }}</p>
      <button @click="retry" class="retry-btn">
        {{ t('ui.retry') }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
const { t } = useI18n()

const retry = () => {
  window.location.reload()
}
</script>

<style scoped>
.error-screen {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  z-index: 9999;
}
.card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  text-align: center;
  max-width: 400px;
}
.icon { font-size: 50px; margin-bottom: 20px; }
h1 { color: #1e293b; margin-bottom: 15px; font-family: sans-serif; }
p { color: #64748b; line-height: 1.6; margin-bottom: 30px; font-family: sans-serif; }
.retry-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
</style>