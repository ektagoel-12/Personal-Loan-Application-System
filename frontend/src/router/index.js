import LoanComponent from '@/views/LoanComponent.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path : '/loan' , component : LoanComponent}
  ],
})

export default router
