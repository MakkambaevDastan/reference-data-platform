<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useVueTable, getCoreRowModel, FlexRender } from '@tanstack/vue-table'

interface ColumnConfig {
  key: string
  labels: Record<string, string>
}

const props = defineProps<{
  rows: any[]
  columnsConfig: ColumnConfig[]
}>()

const { t, locale } = useI18n()

const tableColumns = computed(() => {
  return props.columnsConfig.map(col => ({
    accessorKey: col.key,
    header: col.labels[locale.value] || col.labels['en'] || col.key,
    cell: (info: any) => info.getValue() ?? t('ui.no_data')
  }))
})

const table = useVueTable({
  get data() { return props.rows },
  get columns() { return tableColumns.value },
  getCoreRowModel: getCoreRowModel(),
})
</script>

<template>
  <div class="table-container">
    <table class="api-table">
      <thead>
      <tr v-for="headerGroup in table.getHeaderGroups()" :key="headerGroup.id">
        <th v-for="header in headerGroup.headers" :key="header.id">
          <FlexRender
              :render="header.column.columnDef.header"
              :props="header.getContext()"
          />
        </th>
      </tr>
      </thead>

      <tbody>
      <template v-if="table.getRowModel().rows.length > 0">
        <tr v-for="row in table.getRowModel().rows" :key="row.id">
          <td v-for="cell in row.getVisibleCells()" :key="cell.id">
            <FlexRender
                :render="cell.column.columnDef.cell"
                :props="cell.getContext()"
            />
          </td>
        </tr>
      </template>

      <tr v-else>
        <td :colspan="props.columnsConfig.length" class="empty-cell">
          {{ t('ui.empty_table') || 'No records found' }}
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.table-container {
  overflow-x: auto;
  margin-top: 1rem;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.api-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
}

.api-table th {
  background-color: #f8fafc;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 0.05em;
}

.api-table th, .api-table td {
  border-bottom: 1px solid #e2e8f0;
  padding: 12px 16px;
  text-align: left;
}

.api-table tr:last-child td {
  border-bottom: none;
}

.api-table tr:hover td {
  background-color: #f1f5f9;
}

.empty-cell {
  text-align: center !important;
  padding: 3rem !important;
  color: #94a3b8;
  font-style: italic;
}
</style>