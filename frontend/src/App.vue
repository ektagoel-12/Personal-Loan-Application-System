<template>
  <div class="flex flex-col h-screen">
    <!-- Topbar -->
    <TopbarComponent v-if="!isAuthRoute" />

    <!-- Sidebar + Page Content -->
    <div class="flex flex-1 overflow-hidden">
      <SidebarComponent v-if="!isAuthRoute" />

      <!-- Routed views go here -->
      <main :class="['flex-1', 'overflow-y-auto', 'bg-gray-50', !isAuthRoute ? 'p-6' : '']">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed, onUpdated } from 'vue'
import SidebarComponent from './components/SidebarComponent.vue'
import TopbarComponent from './components/TopbarComponent.vue'

const route = useRoute()
const isAuthRoute = computed(() => ['/', '/login-form', '/registration-form'].includes(route.path))
</script>

<style scoped>
html, body, #app {
  height: 100%;
  margin: 0;
}
</style>
