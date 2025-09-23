import UserLoginForm from '@/views/UserLoginForm.vue'
import UserRegistrationForm from '@/views/UserRegistrationForm.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path:'/login-form',component:UserLoginForm,name:"UserLoginForm"},
    {path:'/registration-form',component:UserRegistrationForm,name:"UserRegistrationForm"},
  ],
})

export default router
