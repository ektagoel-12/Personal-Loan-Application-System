import { createRouter, createWebHistory } from 'vue-router'
import EmiCalculator from '@/views/EmiCalculator.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/emi',        
      name: 'EmiCalculator',
      component: EmiCalculator
    }
  ]
})

export default router
