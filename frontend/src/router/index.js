import HomeDashboard from '@/views/HomeDashboard.vue'
import UserLoginForm from '@/views/UserLoginForm.vue'
import UserRegistrationForm from '@/views/UserRegistrationForm.vue'
import LoanComponent from '@/views/LoanComponent.vue'
import RaiseTicketView from '@/views/RaiseTicketView.vue'
import AdminTicketView from '@/views/AdminTicketView.vue'
import UserTicketsView from '@/views/UserTicketsView.vue'
import EmiCalculator from '@/views/EmiCalculator.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import LandingPage from '../views/LandingPage.vue'
import { useStore } from 'vuex'
import { createRouter, createWebHistory } from 'vue-router'
import RepaymentSchedule from '@/views/RepaymentSchedule.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/',component: LandingPage, name: "LandingPage" },
    { path: '/user-dashboard', meta: { requiresAuth: true } ,component: HomeDashboard, name: "HomeDashboard" },
    { path: '/login-form', component: UserLoginForm, name: "UserLoginForm" },
    { path:'/registration-form',component:UserRegistrationForm,name:"UserRegistrationForm"},
    { path : '/loan' , meta: { requiresAuth: true },component : LoanComponent},
    {path: '/raise-ticket',meta: { requiresAuth: true },component: RaiseTicketView},
    {path: '/admin-ticket-view',meta: { requiresAdminAuth: true }, component:AdminTicketView},
    {path: '/user-ticket-view',meta: { requiresAuth: true },component: UserTicketsView},
    { path: '/calculator', meta: { requiresAuth: true },component: EmiCalculator },
    { path: '/admin',meta: { requiresAdminAuth: true },component: AdminDashboard},
    { path: '/repayment', component: RepaymentSchedule}
  ]
})

router.beforeEach((to, from, next) => {
  const store = useStore()
  const user = store.getters.currentUser
  console.log(user)
  if(to.meta.requiresAdminAuth){
    if(user && user.role === "ADMIN"){
      next()
    }
    else{
      alert("Pls login as admin to view this page")
      return
    }
  }
  else if(to.meta.requiresAuth){
    if(user){
      next()
    }
    else{
      alert("You are not authorized to visit this page")
      next("/login-form")
    }
  }
  else{
      next()
  }
})

export default router
