<template>
  <div class="space-y-6">
    <!-- Back button and header -->
    <div v-if="selectedLoan" class="flex items-center gap-4">
      <!-- Back button -->
      <button
        class="border rounded px-3 py-2 flex items-center gap-2 hover:bg-gray-50"
        @click="clearSelection"
      >
        <ArrowLeft class="w-5 h-5" />
        Back to Loans
      </button>
      <div>
        <h1 class="text-3xl font-semibold">
          Repayment Schedule - {{ selectedLoan.id }}
        </h1>
        <p class="text-gray-500">
          {{ selectedLoan.purpose }} | ₹{{
            selectedLoan.amount.toLocaleString()
          }}
          | {{ selectedLoan.interestRate }}% | {{ selectedLoan.tenure }} years
        </p>
      </div>
    </div>

    <!-- Loan selection -->
    <div v-else>
      <!-- Page header -->
      <div>
        <h1 class="text-3xl font-bold">Repayment Schedule</h1>
        <p class="text-gray-500">
          View detailed repayment schedules for your approved loans
        </p>
      </div>

      <!-- Card-like section -->
      <div class="border rounded-2xl p-6 mt-4 bg-white shadow-sm">
        <div class="mb-4">
          <h2 class="flex items-center gap-2 text-lg font-semibold">
            <ArrowLeft class="w-5 h-5" />
            Select Approved Loan
          </h2>
          <p class="text-gray-500">
            Choose a loan to view its detailed repayment schedule
          </p>
        </div>

        <!-- Filters -->
        <div class="mb-6 relative w-64">
          <!-- Left icon -->
          <Filter
            class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500"
          />

          <!-- Dropdown -->
          <select
            v-model="filterPurpose"
            class="w-full border rounded-lg pl-10 pr-8 py-2 bg-gray-50 appearance-none"
          >
            <option value="all">All Purposes</option>
            <option value="Personal Use">Personal Use</option>
            <option value="Home Purchase">Home Purchase</option>
            <option value="Car Purchase">Car Purchase</option>
            <option value="Education">Education</option>
            <option value="Business">Business</option>
            <option value="Medical Emergency">Medical Emergency</option>
            <option value="Debt Consolidation">Debt Consolidation</option>
            <option value="Home Improvement">Home Improvement</option>
          </select>

          <!-- Right arrow -->
          <ChevronDown
            class="absolute right-2.5 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500 pointer-events-none"
          />
        </div>

        <!-- Loans List -->
        <div class="grid gap-4">
          <div
            v-for="loan in filteredLoans"
            :key="loan.id"
            class="border rounded-2xl p-6 shadow-sm hover:shadow-md transition cursor-pointer bg-white flex justify-between items-center"
          >
            <!-- Left side (loan details) -->
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-4">
                <h3 class="text-lg font-semibold">{{ loan.id }}</h3>
                <span
                  class="bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  APPROVED
                </span>
              </div>

              <!-- Grid for Loan Details -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm mb-4">
                <div>
                  <p class="text-gray-500 text-base">Amount</p>
                  <p class="text-lg font-medium">
                    ₹{{ loan.amount.toLocaleString() }}
                  </p>
                </div>
                <div>
                  <p class="text-gray-500 text-base">Purpose</p>
                  <p class="text-lg font-medium">{{ loan.purpose }}</p>
                </div>
                <div>
                  <p class="text-gray-500 text-base">Interest Rate</p>
                  <p class="text-lg font-medium">
                    {{ loan.interestRate }}% p.a.
                  </p>
                </div>
                <div>
                  <p class="text-gray-500 text-base">Tenure</p>
                  <p class="text-lg font-medium">{{ loan.tenure }} years</p>
                </div>
              </div>

              <!-- Bottom row (EMI + Applied date) -->
              <div class="flex gap-6 text-sm">
                <p>
                  <span class="text-gray-500">Monthly EMI: </span>
                  <span class="font-medium"
                    >₹{{ getEmiForLoan(loan).toLocaleString() }}</span
                  >
                </p>
                <p>
                  <span class="text-gray-500">Applied: </span>
                  <span class="font-medium">{{ loan.appliedDate }}</span>
                </p>
              </div>
            </div>

            <!-- Right side button (centered vertically) -->
            <div class="flex items-center ml-6">
              <button
                class="bg-black text-white px-4 py-2 rounded-lg text-sm hover:bg-gray-800"
                @click="selectLoan(loan)"
              >
                View Schedule
              </button>
            </div>
          </div>
        </div>

        <!-- Empty state if no loans -->
        <div v-if="filteredLoans.length === 0" class="text-center py-8">
          <ArrowLeft class="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <h3 class="text-lg font-semibold">No Approved Loans Found</h3>
          <p class="text-gray-500">
            {{
              filterPurpose !== "all"
                ? "Try adjusting your filter criteria"
                : "You don't have any approved loans yet."
            }}
          </p>
        </div>
      </div>
    </div>

    <!-- Summary stats -->
    <div v-if="summaryStats" class="grid gap-6 md:grid-cols-3">
      <div class="border rounded-lg p-6 shadow-sm">
        <h3 class="flex items-center gap-2 text-black-600 text-lg font-normal">
          <DollarSign class="w-5 h-5 text-green-600" />
          Monthly EMI
        </h3>
        <p class="text-xl font-semibold mt-2">
          ₹{{ summaryStats.monthlyEmi.toLocaleString() }}
        </p>
      </div>
      <div class="border rounded-lg p-6 shadow-sm">
        <h3 class="flex items-center gap-2 text-black-600 text-lg font-normal">
          <TrendingDown class="w-5 h-5 text-red-600" />
          Total Interest
        </h3>
        <p class="text-xl font-semibold mt-2 text-black-600">
          ₹{{ summaryStats.totalInterest.toLocaleString() }}
        </p>
      </div>
      <div class="border rounded-lg p-6 shadow-sm">
        <h3 class="flex items-center gap-2 text-black-600 text-lg font-normal">
          <Calculator class="w-5 h-5 text-blue-600" />
          Total Amount
        </h3>
        <p class="text-xl font-semibold mt-2 text-black-600">
          ₹{{ summaryStats.totalAmount.toLocaleString() }}
        </p>
      </div>
    </div>

    <!-- Schedule -->
    <div v-if="selectedLoan" class="border rounded-lg shadow-sm">
      <div class="flex items-center justify-between p-4">
        <div>
          <h3 class="text-lg font-semibold">Repayment Schedule</h3>
          <p class="text-gray-500 text-sm">
            Detailed breakdown of each monthly payment for {{ selectedLoan.id }}
          </p>
        </div>
        <!-- Export CSV Button -->
        <button
          class="border px-3 py-2 rounded flex items-center gap-2 hover:bg-gray-50"
          @click="exportToCSV"
        >
          <Download class="w-5 h-5" />
          Export CSV
        </button>
      </div>

      <!-- Filters -->
      <div class="flex gap-4 items-center px-4 pb-4">
        <div class="relative w-40">
  <select
    v-model="filterYear"
    class="w-full border rounded-lg pl-3 pr-10 py-2 bg-white text-sm appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500"
  >
    <option value="all">All Years</option>
    <option v-for="year in selectedLoan.tenure" :key="year" :value="year">
      Year {{ year }}
    </option>
  </select>

  <!-- Right arrow -->
  <ChevronDown
    class="absolute right-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500 pointer-events-none"
  />
</div>


        <span class="border rounded px-3 py-2 text-sm text-gray-600">
          {{ sortedSchedule.length }} payments
        </span>
      </div>

      <!-- Table -->
      <table class="w-full border-t">
        <thead>
          <tr class="bg-gray-100">
            <th
              class="px-4 py-2  cursor-pointer hover:bg-gray-50"
              @click="handleSort('month')"
            >
              <div class="flex items-center justify-center gap-1">
                Month
                <ArrowUpDown class="w-4 h-4" />
              </div>
            </th>

            <th
              class="px-4 py-2 cursor-pointer hover:bg-gray-50"
              @click="handleSort('emi')"
            >
              <div class="flex items-center justify-center gap-1">
                EMI
                <ArrowUpDown class="w-4 h-4" />
              </div>
            </th>

            <th
              class="px-4 py-2 cursor-pointer hover:bg-gray-50"
              @click="handleSort('principalAmount')"
            >
              <div class="flex items-center justify-center gap-1">
                Principal
                <ArrowUpDown class="w-4 h-4" />
              </div>
            </th>

            <th
              class="px-4 py-2 cursor-pointer hover:bg-gray-50"
              @click="handleSort('interestAmount')"
            >
              <div class="flex items-center justify-center gap-1">
                Interest
                <ArrowUpDown class="w-4 h-4" />
              </div>
            </th>

            <th
              class="px-4 py-2 cursor-pointer hover:bg-gray-50"
              @click="handleSort('balanceRemaining')"
            >
              <div class="flex items-center justify-center gap-1">
                Balance
                <ArrowUpDown class="w-4 h-4" />
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in paginatedSchedule"
            :key="row.month"
            class="border-t"
          >
            <!-- <td class="px-4 py-2">
              {{ row.month }}
              <span class="ml-2 text-xs border rounded px-1">
                Year {{ Math.ceil(row.month / 12) }}
              </span>
            </td> -->
            <td class="px-4 py-2">
  {{ getMonthName(row.month, selectedLoan.appliedDate) }}
</td>

            <td class="px-4 py-2 ">
              ₹{{ row.emi.toLocaleString() }}
            </td>
            <td class="px-4 py-2 text-green-600">
              ₹{{ row.principalAmount.toLocaleString() }}
            </td>
            <td class="px-4 py-2 text-red-600">
              ₹{{ row.interestAmount.toLocaleString() }}
            </td>
            <td class="px-4 py-2 ">
              ₹{{ row.balanceRemaining.toLocaleString() }}
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div
        v-if="totalPages > 1"
        class="flex justify-between items-center p-4 text-sm"
      >
        <span>
          Showing {{ (currentPage - 1) * rowsPerPage + 1 }} to
          {{ Math.min(currentPage * rowsPerPage, sortedSchedule.length) }} of
          {{ sortedSchedule.length }} payments
        </span>
        <div class="flex gap-2">
          <button
            class="border px-2 py-1 rounded"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            Prev
          </button>
          <button
            v-for="page in totalPages"
            :key="page"
            class="border px-2 py-1 rounded"
            :class="{ 'bg-gray-300': page === currentPage }"
            @click="currentPage = page"
          >
            {{ page }}
          </button>
          <button
            class="border px-2 py-1 rounded"
            :disabled="currentPage === totalPages"
            @click="currentPage++"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import {
  Download,
  ArrowLeft,
  ArrowUpDown,
  Filter,
  ChevronDown,
  DollarSign,
  TrendingDown,
  Calculator,
} from "lucide-vue-next";

export default {
  name: "RepaymentSchedule",
  components: {
    Download,
    ArrowLeft,
    ArrowUpDown,
    Filter,
    ChevronDown,
    DollarSign,
    TrendingDown,
    Calculator,
  },
  setup() {
    const store = useStore();
    const selectedLoan = ref(null);
    const currentPage = ref(1);
    const rowsPerPage = 12;
    const filterYear = ref("all");
    const filterPurpose = ref("all"); // NEW filter

    const sortField = ref("month");
    const sortDirection = ref("asc");

    const handleSort = (field) => {
      if (sortField.value === field) {
        sortDirection.value = sortDirection.value === "asc" ? "desc" : "asc";
      } else {
        sortField.value = field;
        sortDirection.value = "asc";
      }
    };

    const filteredLoans = computed(() => {
      let loans = store.state.applications.filter(
        (loan) => loan.status === "APPROVED"
      );
      if (filterPurpose.value !== "all") {
        loans = loans.filter((loan) => loan.purpose === filterPurpose.value);
      }
      return loans;
    });

    const repaymentSchedule = computed(() => {
      if (!selectedLoan.value) return [];
      const loan = selectedLoan.value;
      const monthlyRate = loan.interestRate / 100 / 12;
      const totalMonths = loan.tenure * 12;
      const emi =
        (loan.amount * monthlyRate * Math.pow(1 + monthlyRate, totalMonths)) /
        (Math.pow(1 + monthlyRate, totalMonths) - 1);

      const schedule = [];
      let balance = loan.amount;

      for (let m = 1; m <= totalMonths; m++) {
        const interest = balance * monthlyRate;
        const principal = emi - interest;
        balance = Math.max(0, balance - principal);

        schedule.push({
          month: m,
          emi: Math.round(emi),
          principalAmount: Math.round(principal),
          interestAmount: Math.round(interest),
          balanceRemaining: Math.round(balance),
          startDate: loan.appliedDate // keep applied date for month mapping
        });
      }
      return schedule;
    });

    const filteredSchedule = computed(() => {
      if (filterYear.value === "all") return repaymentSchedule.value;
      const start = (filterYear.value - 1) * 12 + 1;
      const end = filterYear.value * 12;
      return repaymentSchedule.value.filter(
        (row) => row.month >= start && row.month <= end
      );
    });

    const sortedSchedule = computed(() => {
      const data = [...filteredSchedule.value];
      return data.sort((a, b) => {
        const aVal = a[sortField.value];
        const bVal = b[sortField.value];
        if (typeof aVal === "number" && typeof bVal === "number") {
          return sortDirection.value === "asc" ? aVal - bVal : bVal - aVal;
        }
        return 0;
      });
    });

    const summaryStats = computed(() => {
      if (!repaymentSchedule.value.length || !selectedLoan.value) return null;
      return {
        monthlyEmi: repaymentSchedule.value[0]?.emi || 0,
        totalInterest: Math.round(
          repaymentSchedule.value.reduce((sum, r) => sum + r.interestAmount, 0)
        ),
        totalAmount:
          selectedLoan.value.amount +
          repaymentSchedule.value.reduce((sum, r) => sum + r.interestAmount, 0),
      };
    });

    const getEmiForLoan = (loan) => {
      const monthlyRate = loan.interestRate / 100 / 12;
      const totalMonths = loan.tenure * 12;
      const emi =
        (loan.amount * monthlyRate * Math.pow(1 + monthlyRate, totalMonths)) /
        (Math.pow(1 + monthlyRate, totalMonths) - 1);
      return Math.round(emi);
    };

    const paginatedSchedule = computed(() => {
      const start = (currentPage.value - 1) * rowsPerPage;
      return sortedSchedule.value.slice(start, start + rowsPerPage);
    });

    const totalPages = computed(() =>
      Math.ceil(sortedSchedule.value.length / rowsPerPage)
    );

    const selectLoan = (loan) => {
      selectedLoan.value = loan;
      currentPage.value = 1;
      filterYear.value = "all";
    };
    const getMonthName = (monthIndex, startDate = null) => {
  // If loan.appliedDate is available, use it as the start point
  const baseDate = startDate ? new Date(startDate) : new Date();
  baseDate.setMonth(baseDate.getMonth() + (monthIndex - 1));

  // Format as "Jan 2024" (you can change to "January" if you prefer full names)
  return baseDate.toLocaleString("default", { month: "short", year: "numeric" });
};


    const clearSelection = () => {
      selectedLoan.value = null;
      currentPage.value = 1;
      filterYear.value = "all";
    };

    const exportToCSV = () => {
      if (!selectedLoan.value) return;
      const headers = [
        "Month",
        "EMI",
        "Principal",
        "Interest",
        "Remaining Balance",
      ];
      const rows = repaymentSchedule.value.map((r) =>
        [
          r.month,
          r.emi,
          r.principalAmount,
          r.interestAmount,
          r.balanceRemaining,
        ].join(",")
      );
      const csv = [headers.join(","), ...rows].join("\n");
      const blob = new Blob([csv], { type: "text/csv" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `repayment-schedule-${selectedLoan.value.id}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    };

    return {
      filteredLoans,
      selectedLoan,
      selectLoan,
      clearSelection,
      repaymentSchedule,
      filteredSchedule,
      sortedSchedule,
      paginatedSchedule,
      summaryStats,
      filterYear,
      filterPurpose,
      currentPage,
      totalPages,
      rowsPerPage,
      sortField,
      sortDirection,
      handleSort,
      exportToCSV,
      getEmiForLoan,
      getMonthName,
    };
  },
};
</script>

<style scoped>
table {
  border-collapse: collapse;
}
th,
td {
  border: 1px solid #ddd;
  padding: 6px;
  text-align: center;

}
</style>
