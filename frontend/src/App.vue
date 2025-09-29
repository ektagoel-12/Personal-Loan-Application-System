<template>
  <div class="flex flex-col h-screen">
    <!-- Topbar -->
    <TopbarComponent v-if="!isAuthRoute" />

    <!-- Sidebar + Page Content -->
    <div class="flex flex-1 overflow-hidden">
      <SidebarComponent v-if="!isAuthRoute" />

      <!-- Routed views go here -->
      <main :class="['flex-1','p-0','min-h-full','m-w-full','overflow-y-auto', 'bg-gray-50', !isAuthRoute ? 'p-0' : '']">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed, onMounted } from 'vue'
import SidebarComponent from './components/SidebarComponent.vue'
import TopbarComponent from './components/TopbarComponent.vue'
import { useStore } from 'vuex'

const route = useRoute()
const store = useStore()
const isAuthRoute = computed(() => ['/', '/login-form', '/registration-form'].includes(route.path))
const isLoggedIn = computed(()=>store.getters.isLoggedIn)

onMounted(()=>{
  if(isLoggedIn){
    if(store.state.user && store.state.user.role === "ADMIN"){
      store.dispatch("fetchDashboardData")
    }
    store.dispatch("getAllLoans")
    store.dispatch("fetchTickets",store.state.user.email)
  }
})

</script>

<style scoped>
html, body, #app {
  height: 100%;
  margin: 0;
}
</style>
