import HomeDashboard from '@/views/HomeDashboard.vue'
import UserLoginForm from '@/views/UserLoginForm.vue'
import UserRegistrationForm from '@/views/UserRegistrationForm.vue'
import LoanComponent from '@/views/LoanComponent.vue'
import { createRouter, createWebHistory } from 'vue-router'
import RaiseTicketView from '@/views/RaiseTicketView.vue'
import AdminTicketView from '@/views/AdminTicketView.vue'
import UserTicketsView from '@/views/UserTicketsView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {path:'/',component:HomeDashboard,name:"HomeDashboard"},
    {path:'/login-form',component:UserLoginForm,name:"UserLoginForm"},
    {path:'/registration-form',component:UserRegistrationForm,name:"UserRegistrationForm"},
    { path : '/loan' , component : LoanComponent},
    {path: '/raise-ticket',component: RaiseTicketView},
    {path: '/admin-ticket-view', component:AdminTicketView},
    {path: '/user-ticket-view', component: UserTicketsView}
  ],
})

export default router
