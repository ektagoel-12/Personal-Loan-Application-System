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
        <div class="text-2xl font-semibold">₹{{ totalAmountBorrowed.toLocaleString() }}</div>
        <p class="text-xs text-gray-500">Across all your loans</p>
      </div>

      <div class="rounded-lg border border-secondary bg-white p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Amount Repaid</h2>
          <TrendingUp class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold text-primary">
          ₹{{ mockLoanData.totalRepaid.toLocaleString() }}
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
          <h2 class="text-sm font-medium">Borrowing Capacity</h2>
          <Wallet  class="h-4 w-4 text-primary" />
        </div>
        <div class="text-2xl font-semibold">
          ₹{{borrowingCapacity.toLocaleString() }}
        </div>
        <p class="text-xs text-gray-500">Available: {{ (borrowingCapacity - totalAmountBorrowed).toLocaleString() }}</p>
      </div>
    </div>

    <!-- Active Loan + Recent Applications -->
    <div class="grid gap-6 md:grid-cols-2">

      <!-- Active Loan -->
        <div class="rounded-lg gap-6 border border-secondary bg-white p-4 shadow-sm relative flex flex-col ">
          <h2 class="flex  items-center gap-2 text-lg font-medium text-primary"> 
             <Landmark class="h-5 w-5" />Active Loans 
            </h2>
        <!-- Scrollable container -->
          <div class=" flex gap-4 overflow-x-auto scrollbar-hide">
            <!-- Loan Cards -->
            <div
              v-for="loan in ongoingLoans"
              :key="loan.id"
              class="min-w-[300px]  max-w-sm min-h-full flex-shrink-0 rounded-lg border border-secondary bg-white p-4 shadow-sm hover:border-primary relative"
            >
            <div class="space-y-5 mt-0 ">
              <h2 class="flex items-center gap-2 font-medium text-primary">
                 Loan #{{ loan.id }}
              </h2>
              <p class="text-sm text-gray-500">Loan details</p>

              <div class="space-y-5 mt-4">
                <div class="flex justify-between text-sm">
                  <span>Loan Amount</span>
                  <span class="font-medium">₹{{ loan.amount.toLocaleString() }}</span>
                </div>
                <div class="flex justify-between text-sm">
                  <span>Pending EMIs</span>
                  <span class="font-medium">
                    {{ loan.tenure - emisPaid[loan.id]}}
                  </span>
                </div>
                <div class="flex justify-between text-sm ">
                  <span>Monthly EMI</span>
                  <span class="font-medium">₹{{ loan.emi }}</span>
                </div>
              </div>

                <div class="space-y-2 ">
                  <div class="flex justify-between text-sm">
                    <span>Repayment Progress</span>
                    <span class="text-primary">{{ Math.round((emisPaid[loan.id]/loan.tenure)*10000)/100 }}%</span>
                  </div>
                  <div class="w-full bg-secondary rounded-full h-2">
                    <div
                      class="bg-primary h-2 rounded-full"
                      :style="{ width: (emisPaid[loan.id]/loan.tenure)*100 +'%' }"
                    ></div>
                  </div>
                </div>
              </div>
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
              <p class="text-xs text-gray-500">{{ loanLabel[app.purpose].label }}</p>
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
import { ref, computed, onMounted, watch } from "vue";
import {
  TrendingUp,
  Wallet,
  Landmark,
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
const borrowingCapacity = computed(()=>store.getters.borrowingCapacity)
const ongoingLoans = computed(()=>(store.getters.ongoingLoans))
const emisPaid = computed(()=>(store.getters.getEmisPaid))
const loanLabel = computed(() => store.state.interestRate)



watch(ongoingLoans,()=>{
  for(let loan of ongoingLoans.value){
    if(!emisPaid.value[loan.id]){
      store.dispatch('fetchLoanProgress',loan.id)
    }
    loan.progress = Math.round((emisPaid[loan.id]/loan.tenure)*100);
  }
})



// Mock loan data
const mockLoanData = {
  activeLoan: {
    amount: 250000,
    remainingBalance: 180000,
    emi: 15000,
    nextDueDate: "2024-01-15",
    status: "ACTIVE",
  },
  totalRepaid: 10000
};


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
