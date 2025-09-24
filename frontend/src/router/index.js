import AdminDashboard from '../views/AdminDashboard.vue'
import { createRouter, createWebHistory } from 'vue-router'


const routes = [
  { path: '/admin', component: AdminDashboard},
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
