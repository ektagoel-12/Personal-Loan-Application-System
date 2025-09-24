<template>
  <div class="space-y-6">
    <!-- Welcome Section -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-3xl">Welcome back, {{ user?.name }}</h1>
        <p class="text-muted-foreground">
          Here's an overview of your loan portfolio
        </p>
      </div>
      <button class="flex items-center px-4 py-2 rounded-lg bg-primary text-white hover:bg-primary/80">
        <FileText class="mr-2 h-4 w-4" />
        Apply for New Loan
      </button>
    </div>

    <!-- Quick Stats -->
    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <div class="rounded-lg border p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Total Borrowed</h2>
          <DollarSign class="h-4 w-4 text-muted-foreground" />
        </div>
        <div class="text-2xl">₹{{ mockLoanData.quickStats.totalBorrowed.toLocaleString() }}</div>
        <p class="text-xs text-muted-foreground">Across all your loans</p>
      </div>

      <div class="rounded-lg border p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Amount Repaid</h2>
          <TrendingUp class="h-4 w-4 text-muted-foreground" />
        </div>
        <div class="text-2xl">₹{{ mockLoanData.quickStats.totalRepaid.toLocaleString() }}</div>
        <p class="text-xs text-muted-foreground">+12% from last month</p>
      </div>

      <div class="rounded-lg border p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Credit Score</h2>
          <TrendingUp class="h-4 w-4 text-muted-foreground" />
        </div>
        <div class="text-2xl">{{ mockLoanData.quickStats.creditScore }}</div>
        <p class="text-xs text-muted-foreground">Excellent rating</p>
      </div>

      <div class="rounded-lg border p-4 shadow-sm">
        <div class="flex items-center justify-between pb-2">
          <h2 class="text-sm font-medium">Next EMI Due</h2>
          <Calendar class="h-4 w-4 text-muted-foreground" />
        </div>
        <div class="text-2xl">₹{{ mockLoanData.quickStats.nextEmiDue.toLocaleString() }}</div>
        <p class="text-xs text-muted-foreground">Due on 15th Jan</p>
      </div>
    </div>

    <!-- Active Loan + Recent Applications -->
    <div class="grid gap-6 md:grid-cols-2">
      <!-- Active Loan -->
      <div class="rounded-lg border p-4 shadow-sm">
        <h2 class="flex items-center gap-2 font-medium">
          <DollarSign class="h-5 w-5" />
          Active Loan
        </h2>
        <p class="text-sm text-muted-foreground">Your current loan details and progress</p>

        <div class="space-y-4 mt-4">
          <div class="flex justify-between text-sm">
            <span>Loan Amount</span>
            <span>₹{{ mockLoanData.activeLoan.amount.toLocaleString() }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span>Remaining Balance</span>
            <span>₹{{ mockLoanData.activeLoan.remainingBalance.toLocaleString() }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span>Monthly EMI</span>
            <span>₹{{ mockLoanData.activeLoan.emi.toLocaleString() }}</span>
          </div>

          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span>Repayment Progress</span>
              <span>{{ progressPercentage }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div
                class="bg-blue-600 h-2 rounded-full"
                :style="{ width: progressPercentage + '%' }"
              ></div>
            </div>
          </div>

          <div class="flex items-center gap-2 p-3 bg-blue-50 rounded-lg">
            <Clock class="h-4 w-4 text-blue-600" />
            <span class="text-sm">Next EMI due on {{ mockLoanData.activeLoan.nextDueDate }}</span>
          </div>
        </div>
      </div>

      <!-- Recent Applications -->
      <div class="rounded-lg border p-4 shadow-sm">
        <h2 class="flex items-center gap-2 font-medium">
          <FileText class="h-5 w-5" />
          Recent Applications
        </h2>
        <p class="text-sm text-muted-foreground">Track your latest loan applications</p>

        <div class="space-y-4 mt-4">
          <div
            v-for="app in mockLoanData.recentApplications"
            :key="app.id"
            class="flex items-center justify-between p-3 border rounded-lg"
          >
            <div class="space-y-1">
              <p class="text-sm">₹{{ app.amount.toLocaleString() }}</p>
              <p class="text-xs text-muted-foreground">{{ app.purpose }}</p>
              <p class="text-xs text-muted-foreground">{{ app.date }}</p>
            </div>
            <span
              class="px-2 py-1 text-xs rounded"
              :class="getStatusColor(app.status)"
            >
              {{ app.status.replace('_', ' ') }}
            </span>
          </div>
        </div>

        <button
          class="w-full mt-4 px-4 py-2 rounded-lg border hover:bg-gray-200"
        >
          View All Applications
        </button>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="rounded-lg border p-4 shadow-sm">
      <h2 class="font-medium">Quick Actions</h2>
      <p class="text-sm text-muted-foreground">Common tasks and tools for managing your loans</p>

      <div class="grid gap-4 md:grid-cols-3 mt-4">
        <button
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border hover:bg-gray-200"
        >
          <Calculator class="h-5 w-5" />
          <span>EMI Calculator</span>
        </button>
        <button
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border hover:bg-gray-200"
        >
          <FileText class="h-5 w-5" />
          <span>New Application</span>
        </button>
        <button
          class="flex items-center justify-center gap-2 h-20 px-4 py-2 rounded-lg border hover:bg-gray-200"
        >
          <AlertCircle class="h-5 w-5" />
          <span>Get Support</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import {
  TrendingUp,
  Calendar,
  DollarSign,
  Clock,
  FileText,
  Calculator,
  AlertCircle,
} from "lucide-vue-next";

// Mock Auth context
const user = ref({ name: "John Doe" });

// Mock loan data
const mockLoanData = {
  activeLoan: {
    amount: 250000,
    remainingBalance: 180000,
    emi: 15000,
    nextDueDate: "2024-01-15",
    status: "ACTIVE",
  },
  recentApplications: [
    { id: "1", amount: 100000, status: "UNDER_REVIEW", date: "2024-01-05", purpose: "Home Improvement" },
    { id: "2", amount: 50000, status: "APPROVED", date: "2024-01-02", purpose: "Personal" },
  ],
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

function getStatusColor(status: string) {
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
</script>
