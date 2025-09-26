<script setup>
import { ref, computed, onMounted } from "vue"
import { useStore } from "vuex"
import  { Eye,SquareArrowDown } from "lucide-vue-next"
import Model from "@/components/LoanModel.vue"

const store = useStore()

// Local filters
const searchTerm = ref("")
const statusFilter = ref("ALL")
const dateRange = ref({ from: null, to: null })
const selectLoan = ref(null)
const showModel = ref(false)

// Map for status display
const usersStatusMap = {
  APPROVED: { label: "Approved", class: "bg-green-100 text-green-800" },
  PENDING: { label: "Under Review", class: "bg-blue-100 text-blue-800" },
  NEW: { label: "Under Review", class: "bg-blue-100 text-blue-800" },
  REJECTED: { label: "Rejected", class: "bg-red-100 text-red-800" },
}

const adminStatusMap = {
  APPROVED: { label: "Approved", class: "bg-green-100 text-green-800" },
  PENDING: { label: "Pending", class: "bg-yellow-100 text-yellow-800" },
  NEW: { label: "New", class: "bg-blue-100 text-blue-800" },
  REJECTED: { label: "Rejected", class: "bg-red-100 text-red-800" },
}

const statusMap = store.state.user.role === 'ADMIN' ? adminStatusMap : usersStatusMap

// Filtering logic 
const filteredApplications = computed(() => {
  return store.state.applications.filter((app) => {
    const matchesSearch =
      app.id.toString().toLowerCase().includes(searchTerm.value.toLowerCase()) ||
      app.purpose.toLowerCase().includes(searchTerm.value.toLowerCase())

    const matchesStatus =
      statusFilter.value === "ALL" || app.status === statusFilter.value

    let matchesDate = true
    if (dateRange.value.from && dateRange.value.to) {
      const appDate = new Date(app.appliedDate)
      matchesDate =
        appDate >= new Date(dateRange.value.from) &&
        appDate <= new Date(dateRange.value.to)
    }

    return matchesSearch && matchesStatus && matchesDate
  })
})

const viewLoan = (loan) =>{
  selectLoan.value = loan
  showModel.value = true
}

onMounted(() =>{
  store.dispatch('getAllLoans')
})

</script>

<template>
  <div class="max-w-4xl mx-auto p-6">
    <!-- Header -->
    <h1 class="text-2xl font-bold text-gray-900">My Loan Applications</h1>
    <p class="text-gray-500 mb-6">Track the status of your loan applications</p>

    <!-- Filters -->
    <div class="flex gap-2 mb-6 flex-wrap">
      <input
        v-model="searchTerm"
        type="text"
        placeholder="Search by application ID or purpose..."
        class="flex-1 px-3 py-2 border rounded-lg"
      />
      <select v-model="statusFilter" class="px-3 py-2 border rounded-lg">
        <option value="ALL">All Status</option>
        <option value="APPROVED">Approved</option>
        <option value="PENDING">Under Review</option>
        <option value="REJECTED">Rejected</option>
      </select>
      <input
        type="date"
        v-model="dateRange.from"
        class="px-3 py-2 border rounded-lg"
      />
      <input
        type="date"
        v-model="dateRange.to"
        class="px-3 py-2 border rounded-lg"
      />
    </div>

    <!-- Loan Cards -->
    <div class="space-y-4">
      <div
        v-for="loan in filteredApplications"
        :key="loan.id"
        class="bg-white shadow rounded-lg p-4 flex flex-col gap-3"
      >
        <!-- Top Row -->
        <div class="flex justify-between items-center">
          <div class="flex items-center gap-2">
            <span class="font-semibold">{{ loan.id }}</span>
            <span
              class="text-xs px-2 py-1 rounded-full font-medium"
              :class="statusMap[loan.status].class"
            >
              {{ statusMap[loan.status].label }}
            </span>
          </div>
          <div class="flex gap-2 ">
            <button @click="viewLoan(loan)" class="  px-3 py-1 border rounded-lg hover:shadow-md hover:bg-slate-100">
              <div class="flex content-center text-xs">
                <Eye class="h-4 w-4 mr-1 p-0 mb-0" /> View Details
              </div>
            </button>
            <button
              v-if="loan.status === 'APPROVED'"
              class="px-3 py-1 border rounded-lg hover:shadow-md hover:bg-slate-100"
            >
              <div class="flex content-center text-xs ">
                <SquareArrowDown class="h-4 w-4 mr-1" /> Download
              </div>
            </button>
          </div>
        </div>

        <!-- Loan Info -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
          <div>
            <p class="text-gray-500">Amount</p>
            <p class="font-medium">₹{{ loan.amount.toLocaleString() }}</p>
          </div>
          <div>
            <p class="text-gray-500">Purpose</p>
            <p class="font-medium">{{ loan.purpose }}</p>
          </div>
          <div>
            <p class="text-gray-500">Applied On</p>
            <p class="font-medium">{{ loan.appliedDate }}</p>
          </div>
          <div>
            <p class="text-gray-500">Last Updated</p>
            <p class="font-medium">{{ loan.lastUpdated }}</p>
          </div>
        </div>

        <!-- Remarks -->
        <p
          v-if="loan.remarks!== null && loan.remarkedBy !== null"
          class="bg-gray-100 px-3 py-2 rounded-md text-sm"
        >
          {{ loan.remarks }}
        </p>

        <!-- EMI/Details -->
        <div v-if="loan.emi" class="flex gap-6 text-sm text-gray-700">
          <span>EMI: ₹{{ loan.emi.toLocaleString() }}</span>
          <span>Interest: {{ loan.interestRate }}%</span>
          <span>Tenure: {{ loan.tenure }} years</span>
        </div>

        <Model 
         :isOpen="showModel"
         :application = "selectLoan"
         @close="showModel=false"
         :statusMap="statusMap" />
      </div>
    </div>
  </div>
</template>
