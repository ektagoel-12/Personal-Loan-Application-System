import AdminDashboard from '../views/AdminDashboard.vue'
import ApplicationStatus from '../views/ApplicationStatus.vue'
import EmiCalculator from '../views/EmiCalculator.vue'
import HomePage from '../views/HomePage.vue'
import LoanApplicationForm from '../views/LoanApplicationForm.vue'
import MyTickets from '../views/MyTickets.vue'
import RepaymentSchedule from '../views/RepaymentSchedule.vue'
import SupportForm from '../views/SupportForm.vue'
import UserLoginForm from '../views/UserLoginForm.vue'
import UserRegistrationForm from '../views/UserRegistrationForm.vue'
import { createRouter, createWebHistory } from 'vue-router'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: HomePage },
    { path: '/admin', component: AdminDashboard },
    { path: '/application/status', component: ApplicationStatus },
    { path: '/emi', component: EmiCalculator },
    { path: '/loan', component: LoanApplicationForm },
    { path: '/tickets', component: MyTickets },
    { path: '/repayment-schedule', component: RepaymentSchedule },
    { path: '/support-form', component: SupportForm },
    { path: '/login-form', component: UserLoginForm },
    { path: '/registration-form', component: UserRegistrationForm },
  ],
})

export default router
