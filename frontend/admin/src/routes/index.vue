<script lang="ts">
import { createFileRoute } from '@tanstack/vue-router'

export const Route = createFileRoute('/')({
})
</script>

<script setup lang="ts">
import { inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { Link } from '@tanstack/vue-router'
import { ManifestKey } from '../config/manifest'
import type { ModuleConfig } from '../config/manifest'

const { t, locale } = useI18n()
const manifest = inject(ManifestKey)

const getModuleTitle = (mod: ModuleConfig) => {
  return mod.translations[locale.value]?.title || mod.translations['en']?.title || 'Unknown'
}
</script>

<template>
  <div class="dashboard">
    <h1>{{ t('ui.dashboard') }}</h1>

    <div v-if="manifest" class="grid">
      <Link
          v-for="(mod, id) in manifest.modules"
          :key="id"
          :to="'/$module'"
          :params="{ module: id }"
          class="card"
      >
        <h3>{{ getModuleTitle(mod) }}</h3>
        <span>{{ t('ui.open') || 'Open' }}</span>
      </Link>
    </div>

    <p v-else>{{ t('ui.loading') }}</p>
  </div>
</template>

<style scoped>
.dashboard { padding: 2rem; }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}
.card {
  display: block;
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
}
.card:hover { border-color: #42b883; }
</style>