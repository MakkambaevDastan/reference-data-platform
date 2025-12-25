<script lang="ts">
import { createFileRoute } from '@tanstack/vue-router'

export const Route = createFileRoute('/$module')({
})
</script>

<script setup lang="ts">
import { useParams } from '@tanstack/vue-router'
import { inject, computed } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { useI18n } from 'vue-i18n'
import axios from 'axios'
import DynamicTable from '../components/DynamicTable.vue'
import { ManifestKey } from '../config/manifest'

const manifest = inject(ManifestKey)
const { locale } = useI18n()
const params = useParams({ from: '/$module' })

const config = computed(() => {
  if (!manifest) return null
  return manifest.modules[params.value.module]
})

const displayTitle = computed(() => {
  if (!config.value) return ''
  return config.value.translations[locale.value]?.title ||
      config.value.translations['en']?.title ||
      params.value.module
})

const { data, isLoading } = useQuery({
  queryKey: ['module-data', computed(() => params.value.module)],
  queryFn: async () => {
    if (!config.value || !manifest) return null
    const res = await axios.get(`${manifest.apiBaseUrl}${config.value.apiPath}`)
    return res.data
  },
  enabled: computed(() => !!config.value)
})
</script>

<template>
  <div v-if="config" class="module-page">
    <h1>{{ displayTitle }}</h1>

    <div v-if="isLoading">Loading...</div>

    <DynamicTable
        v-else-if="data"
        :rows="data.content || data || []"
        :columns-config="config.columns"
    />
  </div>
</template>