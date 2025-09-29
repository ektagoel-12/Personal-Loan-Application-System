<template>
  <div class="space-y-6 font-sans p-6 text-textdark bg-neutral">
    <!-- Welcome Section -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl font-semibold">Welcome back, {{ user?.name }}</h1>
        <p class="text-sm text-gray-500">
          Here's an overview of your loan portfolio
        </p>
      </div>
      <button
        @click="router.push('applyLoan')"
        class="flex items-center px-4 py-2 rounded-lg bg-primary text-white hover:bg-primary/90 shadow-sm transition"
      >
        <FileText class="mr-2 h-4 w-4 text-white" />
        Apply for New Loan
      </button>
    </div>

    <!-- Quick Stats -->
    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Total Borrowed</h2>
          <IndianRupee  class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold">₹{{ totalAmountBorrowed }}</div>
        <p class="text-xs text-gray-500">Across all your loans</p>
      </div>

      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Amount Repaid</h2>
          <TrendingUp class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold text-primary">
          ₹{{ mockLoanData.quickStats.totalRepaid.toLocaleString() }}
        </div>
        <p class="text-xs text-accent font-medium">+12% from last month</p>
      </div>

      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Credit Score</h2>
          <TrendingUp class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold">{{ user.creditScore }}</div>
        <p v-if="user.creditScore >= 750" class="text-xs text-green-600">Excellent rating</p>
        <p v-else-if="user.creditScore >= 700" class="text-xs text-blue-600">Good rating</p>
        <p v-else-if="user.creditScore >= 650" class="text-xs text-yellow-600">Fair rating</p>
        <p v-else class="text-xs text-red-600">Poor rating</p>
      </div>

      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Next EMI Due</h2>
          <Calendar class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold">
          ₹{{ nextEmiDue}}
        </div>
        <p class="text-xs text-gray-500">Due on 15th Jan</p>
      </div>
    </div>

    <!-- Active Loan + Recent Applications -->
    <div class="grid gap-6 md:grid-cols-2">
      <!-- Active Loan -->
      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <h2 class="flex items-center gap-2 font-medium text-primary">
          <IndianRupee class="h-5 w-5" />
          Active Loan
        </h2>
        <p class="text-sm text-gray-500">Your current loan details and progress</p>

        <div class="space-y-4 mt-4">
          <div class="flex justify-between text-sm">
            <span>Loan Amount</span>
            <span class="font-medium">₹{{ mockLoanData.activeLoan.amount.toLocaleString() }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span>Remaining Balance</span>
            <span class="font-medium">₹{{ mockLoanData.activeLoan.remainingBalance.toLocaleString() }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span>Monthly EMI</span>
            <span class="font-medium">₹{{ mockLoanData.activeLoan.emi.toLocaleString() }}</span>
          </div>

          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span>Repayment Progress</span>
              <span class="text-primary">{{ progressPercentage }}%</span>
            </div>
            <div class="w-full bg-secondary rounded-full h-2">
              <div
                class="bg-primary h-2 rounded-full"
                :style="{ width: progressPercentage + '%' }"
              ></div>
            </div>
          </div>

          <div class="flex items-center gap-2 p-3 bg-secondary rounded-lg">
            <Clock class="h-4 w-4 text-primary" />
            <span class="text-sm">Next EMI due on {{ mockLoanData.activeLoan.nextDueDate }}</span>
          </div>
        </div>
      </div>

      <!-- Recent Applications -->
      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <h2 class="flex items-center gap-2 font-medium text-primary">
          <FileText class="h-5 w-5" />
          Recent Applications
        </h2>
        <p class="text-sm text-gray-500">Track your latest loan applications</p>

        <div class="space-y-4 mt-4">
          <div
            v-for="app in recentApplications"
            :key="app.id"
            class="flex items-center justify-between p-3 border rounded-lg hover:bg-secondary/40 transition"
          >
            <div class="space-y-1">
              <p class="text-sm font-medium">₹{{ app.amount.toLocaleString() }}</p>
              <p class="text-xs text-gray-500">{{ app.purpose }}</p>
              <p class="text-xs text-gray-400">{{ app.date }}</p>
            </div>
            <span
              class="px-2 py-1 text-xs rounded font-medium"
              :class="getStatusColor(app.status)"
            >
              {{ app.status.replace('_', ' ') }}
            </span>
          </div>
        </div>

        <button
          class="w-full mt-4 px-4 py-2 rounded-lg border border-primary text-primary hover:bg-primary hover:text-white transition"
          @click="router.push('/loan')"
        >
          View All Applications
        </button>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
      <h2 class="font-medium text-primary">Quick Actions</h2>
      <p class="text-sm text-gray-500">Common tasks and tools for managing your loans</p>

      <div class="grid gap-4 md:grid-cols-3 mt-4">
        <button
          @click="router.push('/calculator')"
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border border-primary text-primary hover:bg-primary hover:text-white transition"
        >
          <Calculator class="h-5 w-5" />
          <span>EMI Calculator</span>
        </button>
        <button
          @click="router.push('/applyLoan')"
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border border-primary text-primary hover:bg-primary hover:text-white transition"
        >
          <FileText class="h-5 w-5" />
          <span>New Application</span>
        </button>
        <button
          @click="router.push('/raise-ticket')"
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border border-primary text-primary hover:bg-primary hover:text-white transition"
        >
          <AlertCircle class="h-5 w-5" />
          <span>Get Support</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup >
import { ref, computed, onMounted } from "vue";
import {
  TrendingUp,
  Calendar,
  Clock,
  FileText,
  Calculator,
  AlertCircle,
  IndianRupee 
} from "lucide-vue-next";
import { useStore } from "vuex";
import router from "@/router";

const store = useStore()
// Mock Auth context
const user = ref(store.getters.currentUser);

const recentApplications = computed( ()=>store.getters.recentApplications)
const totalAmountBorrowed = computed(()=>store.getters.totalBorrowed)
// console.log(store.getters.ongoingLoans)
// const nextEmiDue = computed( ()=>(store.getters.ongoingLoans.reduce((prev,curr)=>prev+curr.emi)),0)
// console.log(nextEmiDue.value)

// Mock loan data
const mockLoanData = {
  activeLoan: {
    amount: 250000,
    remainingBalance: 180000,
    emi: 15000,
    nextDueDate: "2024-01-15",
    status: "ACTIVE",
  },
  quickStats: {
    totalBorrowed: 300000,
    totalRepaid: 120000,
    creditScore: 750,
    nextEmiDue: 15000,
  },
};

const progressPercentage = computed(() =>
  Math.round(
    ((mockLoanData.activeLoan.amount - mockLoanData.activeLoan.remainingBalance) /
      mockLoanData.activeLoan.amount) *
      100
  )
);

function getStatusColor(status) {
  switch (status) {
    case "APPROVED":
      return "bg-green-100 text-green-800";
    case "UNDER_REVIEW":
      return "bg-blue-100 text-blue-800";
    case "REJECTED":
      return "bg-red-100 text-red-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
}
onMounted(()=>{
  store.dispatch("getAllLoans")
})

</script>
