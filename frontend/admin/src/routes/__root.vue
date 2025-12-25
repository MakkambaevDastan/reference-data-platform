<script lang="ts">
import { createRootRouteWithContext } from '@tanstack/vue-router'
import type { RouterContext } from '../main'

export const Route = createRootRouteWithContext<RouterContext>()({
})
</script>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Outlet } from '@tanstack/vue-router'

const { locale } = useI18n()
</script>

<template>
  <div class="layout">
    <header class="navbar">
      <div class="logo">Admin Panel</div>
      <div class="lang-switcher">
        <select v-model="locale">
          <option value="ru">RU</option>
          <option value="en">EN</option>
        </select>
      </div>
    </header>

    <main class="content">
      <Outlet />
    </main>
  </div>
</template>

<style scoped>
.layout {
  font-family: sans-serif;
}
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #2c3e50;
  color: white;
}
.content {
  padding: 2rem;
}
select {
  padding: 4px 8px;
  border-radius: 4px;
}
</style>