<template>
  <div class="space-y-6 p-6 font-sans text-textdark">
    <!-- Back button and header -->
    <div v-if="selectedLoan" class="flex items-center justify-between gap-4">
      <!-- Back button -->
      <div>
        <h1 class="text-3xl font-semibold text-primary">
          Repayment Schedule - {{ selectedLoan.id }}
        </h1>
        <p class="text-gray-500">
          {{ selectedLoan.loanType }} | ₹{{
            selectedLoan.amount.toLocaleString()
          }}
          | {{ selectedLoan.interestRate }}% | {{ selectedLoan.tenure/12 }} years
        </p>
      </div>
       <button
        class="border border-primary/30 rounded px-3 py-2 flex items-center gap-2 hover:bg-secondary transition"
        @click="clearSelection"
        >
        <ArrowLeft class="w-5 h-5 text-primary" />
        Back to Loans
        </button>   
    </div>

    <!-- Loan selection-->
    <div v-else>
      <!-- Page header -->
      <div>
        <h1 class="text-3xl font-bold ">Repayment Schedule</h1>
        <p class="text-gray-500">
          View detailed repayment schedules for your approved loans
        </p>
      </div>

      <!-- Card-like section -->
      <div class="border border-secondary rounded-2xl p-6 mt-4 bg-white shadow-sm">
        <div class="mb-4">
          <h2 class="flex items-center gap-2 text-lg font-semibold text-primary">
            Select Approved Loan
          </h2>
          <p class="text-gray-500">
            Choose a loan to view its detailed repayment schedule
          </p>
        </div>

        <!-- Filters -->
        <div class="mb-6 relative w-64">
          <Filter
            class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-primary"
          />
          <select
            v-model="filterPurpose"
            class="w-full border border-primary/30 rounded-lg pl-10 pr-8 py-2 bg-secondary text-textdark appearance-none focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="all">All Purposes</option>
            <option
              v-for="(purpose, key) in loanPurposes"
              :key="key"
              :value="purpose.label"
            >
              {{ purpose.label }}
            </option>
          </select>
          <ChevronDown
            class="absolute right-2.5 top-1/2 transform -translate-y-1/2 w-4 h-4 text-primary pointer-events-none"
          />
        </div>

        <!-- Loans List -->
        <div class="grid gap-4">
          <div
            v-for="loan in filteredLoans"
            :key="loan.id"
            class="border border-secondary rounded-2xl p-6 shadow-sm hover:shadow-md hover:border-primary transition cursor-pointer bg-white flex justify-between items-center"
          >
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-4">
                <h3 class="text-lg font-semibold text-primary">{{ loan.id }}</h3>
                <span
                  class="bg-green-100 text-green-700 px-2.5 py-0.5 rounded-full text-xs font-medium"
                >
                  APPROVED
                </span>
              </div>

              <!-- Loan Details Grid -->
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm mb-4">
                <div>
                  <p class="text-gray-500">Amount</p>
                  <p class="text-lg font-medium">₹{{ loan.amount.toLocaleString() }}</p>
                </div>
                <div>
                  <p class="text-gray-500">Purpose</p>
                  <p class="text-lg font-medium">{{ loanPurposes[loan.purpose].label }}</p>
                </div>
                <div>
                  <p class="text-gray-500">Interest Rate</p>
                  <p class="text-lg font-medium">{{ loan.interestRate }}% p.a.</p>
                </div>
                <div>
                  <p class="text-gray-500">Tenure</p>
                  <p class="text-lg font-medium">{{ loan.tenure/12 }} years</p>
                </div>
              </div>

              <!-- EMI + Applied Date -->
              <div class="flex gap-6 text-sm">
                <p>
                  <span class="text-gray-500">Monthly EMI: </span>
                  <span class="font-medium text-primary">
                    ₹{{ getEmiForLoan(loan).toLocaleString() }}
                  </span>
                </p>
                <p>
                  <span class="text-gray-500">Applied: </span>
                  <span class="font-medium">{{ loan.appliedDate }}</span>
                </p>
              </div>
            </div>

            <!-- View button -->
            <div class="flex items-center ml-6">
              <button
                class="bg-primary text-white px-4 py-2 rounded-lg text-sm hover:bg-primary/90 transition"
                @click="selectLoan(loan)"
              >
                View Schedule
              </button>
            </div>
          </div>
        </div>

        <!-- Empty state -->
        <div v-if="filteredLoans.length === 0" class="text-center py-8">
          <ArrowLeft class="h-12 w-12 text-primary mx-auto mb-4" />
          <h3 class="text-lg font-semibold text-primary">No Approved Loans Found</h3>
          <p class="text-gray-500">
            {{ filterPurpose !== "all"
              ? "Try adjusting your filter criteria"
              : "You don't have any approved loans yet." }}
          </p>
        </div>
      </div>
    </div>

    <!-- Summary stats -->
    <div v-if="summaryStats" class="grid gap-6 md:grid-cols-3">
      <div class="border border-secondary rounded-lg p-6 shadow-sm bg-white">
        <h3 class="flex items-center gap-2 text-lg font-medium text-primary">
          <IndianRupee class="w-5 h-5 text-green-600" />
          Monthly EMI
        </h3>
        <p class="text-xl font-semibold mt-2">
          ₹{{ summaryStats.monthlyEmi.toLocaleString() }}
        </p>
      </div>
      <div class="border border-secondary rounded-lg p-6 shadow-sm bg-white">
        <h3 class="flex items-center gap-2 text-lg font-medium text-primary">
          <TrendingDown class="w-5 h-5 text-red-600" />
          Total Interest
        </h3>
        <p class="text-xl font-semibold mt-2">
          ₹{{ summaryStats.totalInterest.toLocaleString() }}
        </p>
      </div>
      <div class="border border-secondary rounded-lg p-6 shadow-sm bg-white">
        <h3 class="flex items-center gap-2 text-lg font-medium text-primary">
          <Calculator class="w-5 h-5 text-blue-600" />
          Total Amount
        </h3>
        <p class="text-xl font-semibold mt-2">
          ₹{{ summaryStats.totalAmount.toLocaleString() }}
        </p>
      </div>
    </div>

    <!-- Schedule -->
    <div v-if="selectedLoan" class="border border-secondary rounded-lg shadow-sm bg-white">
      <div class="flex items-center justify-between p-4">
        <div>
          <h3 class="text-lg font-semibold text-primary">Repayment Schedule</h3>
          <p class="text-gray-500 text-sm">
            Detailed breakdown of each monthly payment for {{ selectedLoan.id }}
          </p>
        </div>
        <button
          class="border border-primary/40 px-3 py-2 rounded flex items-center gap-2 hover:bg-secondary transition"
          @click="exportToCSV"
        >
          <Download class="w-5 h-5 text-primary" />
          Export CSV
        </button>
      </div>

      <!-- Filters -->
      <div class="flex gap-4 items-center px-4 pb-4">
        <div class="relative w-40">
          <select
            v-model="filterYear"
            class="appearance-none w-full border border-primary/30 rounded-lg pl-3 pr-10 py-2 bg-white text-sm focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="all">All Years</option>
            <option v-for="year in selectedLoan.tenure" :key="year" :value="year">
              Year {{ year }}
            </option>
          </select>
          <ChevronDown
            class="absolute right-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-primary pointer-events-none"
          />
        </div>

        <span class="border border-secondary rounded px-3 py-2 text-sm text-gray-600">
          {{ sortedSchedule.length }} payments
        </span>
      </div>

      <!-- Table -->
      <table class="w-full border-t border-secondary text-sm">
        <thead>
          <tr class="bg-secondary text-textdark">
            <th
              class="px-4 py-2 cursor-pointer hover:bg-secondary/70"
              @click="handleSort('month')"
            >
              <div class="flex items-center justify-center gap-1">
                Month
                <ArrowUpDown class="w-4 h-4 text-primary" />
              </div>
            </th>
            <th
              class="px-4 py-2 cursor-pointer hover:bg-secondary/70"
              @click="handleSort('emi')"
            >
              <div class="flex items-center justify-center gap-1">
                EMI
                <ArrowUpDown class="w-4 h-4 text-primary" />
              </div>
            </th>
            <th
              class="px-4 py-2 cursor-pointer hover:bg-secondary/70"
              @click="handleSort('principalAmount')"
            >
              <div class="flex items-center justify-center gap-1">
                Principal
                <ArrowUpDown class="w-4 h-4 text-primary" />
              </div>
            </th>
            <th
              class="px-4 py-2 cursor-pointer hover:bg-secondary/70"
              @click="handleSort('interestAmount')"
            >
              <div class="flex items-center justify-center gap-1">
                Interest
                <ArrowUpDown class="w-4 h-4 text-primary" />
              </div>
            </th>
            <th
              class="px-4 py-2 cursor-pointer hover:bg-secondary/70"
              @click="handleSort('balanceRemaining')"
            >
              <div class="flex items-center justify-center gap-1">
                Balance
                <ArrowUpDown class="w-4 h-4 text-primary" />
              </div>
            </th>
            <th class="px-4 py-2 cursor-pointer hover:bg-gray-50">
              <div class="flex items-center justify-center gap-1">Paid</div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(row, index) in paginatedSchedule"
            :key="row.id"
            class="border-t"
          >
            <td class="px-4 py-2">
              {{ getMonthName(row.month, selectedLoan.lastUpdated) }}
            </td>

            <td class="px-4 py-2">₹{{ row.emi.toLocaleString() }}</td>
            <td class="px-4 py-2 text-green-600">
              ₹{{ row.principalAmount.toLocaleString() }}
            </td>
            <td class="px-4 py-2 text-red-600">
              ₹{{ row.interestAmount.toLocaleString() }}
            </td>
            <td class="px-4 py-2">
              ₹{{ row.balanceRemaining.toLocaleString() }}
            </td>
            <td class="px-4 py-2">
              <button
                v-if="!row.isPaid"
                @click="markAsPaid(selectedLoan.id, row.id)"
                class="bg-primary text-white px-4 py-1.5 rounded-md text-sm font-medium shadow-sm hover:bg-blue-700 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="!canPay(row)"
              >
                Pay Now
              </button>
              <span
                v-else
                class="bg-green-100 text-green-700 px-3 py-1.5 rounded-md text-sm font-medium"
              >
                Paid
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex justify-between items-center p-4 text-sm">
        <span>
          Showing {{ (currentPage - 1) * rowsPerPage + 1 }} to
          {{ Math.min(currentPage * rowsPerPage, sortedSchedule.length) }} of
          {{ sortedSchedule.length }} payments
        </span>

        <div class="flex items-center gap-3">
          <!-- Prev Button -->
          <button
            class="border border-primary/30 px-2 py-1 rounded hover:bg-secondary transition"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            ← Prev
          </button>

          <!-- Page Indicator -->
          <span class="text-gray-700">
            Page {{ currentPage }} of {{ totalPages }}
          </span>

          <!-- Next Button -->
          <button
            class="border px-3 py-1 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
            :disabled="currentPage === totalPages"
            @click="currentPage++"
          >
            Next →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useStore } from "vuex";
import { makeRequestWithToken } from "@/utils/requests";
import {
  Download,
  ArrowLeft,
  ArrowUpDown,
  Filter,
  ChevronDown,
  TrendingDown,
  Calculator,
  IndianRupee,
} from "lucide-vue-next";

    const store = useStore();
    const selectedLoan = ref(null);
    const currentPage = ref(1);
    const rowsPerPage = 12;
    const filterYear = ref("all");
    const filterPurpose = ref("all"); // NEW filter

    const sortField = ref("month");
    const sortDirection = ref("asc");
    const loanPurposes = computed(()=>(store.state.interestRate))

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
        loans = loans.filter((loan) => loanPurposes.value[loan.purpose].label === filterPurpose.value);
      }
      return loans;
    });
    const markAsPaid = async (loanId, scheduleId) => {
      try {
        await makeRequestWithToken(
          "POST",
          `/api/loans/${loanId}/schedule/${scheduleId}/pay`
        );

        // Refresh schedule after payment
        const response = await makeRequestWithToken(
          "GET",
          `/api/loans/${loanId}/schedule`
        );
        repaymentSchedule.value = response?.data || [];
      } catch (err) {
        console.error("Error marking payment:", err);
      }
    };

    //new reactive state to hold the schedule from API
    const repaymentSchedule = ref([]);

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
      if (!selectedLoan.value || repaymentSchedule.value.length === 0)
        return null;

      const monthlyEmi = Math.round(repaymentSchedule.value[0].emi);

      const totalInterest = repaymentSchedule.value.reduce(
        (sum, r) => sum + r.interestAmount,
        0
      );

      const totalAmount = Math.round(selectedLoan.value.amount + totalInterest);

      return {
        monthlyEmi,
        totalInterest: Math.round(totalInterest),
        totalAmount,
      };
    });

    const getEmiForLoan = (loan) => {
      const monthlyRate = loan.interestRate / 100 / 12;
      const totalMonths = loan.tenure;
      const emi =
        (loan.amount * monthlyRate * Math.pow(1 + monthlyRate, totalMonths)) /
        (Math.pow(1 + monthlyRate, totalMonths) - 1);
      return Math.round(emi);
    };
    // Only allow paying the first unpaid EMI in order
    const canPay = (row) => {
      // if already paid → disabled
      if (row.isPaid) return false;

      // find this row's index in the full schedule
      const index = sortedSchedule.value.findIndex((r) => r.id === row.id);

      // ensure all previous months are paid
      for (let i = 0; i < index; i++) {
        if (!sortedSchedule.value[i].isPaid) {
          return false; // found a previous unpaid EMI → block
        }
      }

      return true; //  allowed only if all before are paid
    };

    const paginatedSchedule = computed(() => {
      const start = (currentPage.value - 1) * rowsPerPage;
      return sortedSchedule.value.slice(start, start + rowsPerPage);
    });

    const totalPages = computed(() =>
      Math.ceil(sortedSchedule.value.length / rowsPerPage)
    );

    const selectLoan = async (loan) => {
      selectedLoan.value = loan;
      currentPage.value = 1;
      filterYear.value = "all";

      try {
        const response = await makeRequestWithToken(
          "GET",
          `/api/loans/${loan.id}/schedule`
        );
        repaymentSchedule.value = response?.data || [];
      } catch (err) {
        console.error("Error fetching schedule", err);
        repaymentSchedule.value = [];
      }
    };
    const getMonthName = (monthIndex, startDate ) => {
      const [day, month, year] = startDate.split("/").map(Number);
      const baseDate = new Date(year,month+monthIndex-1,day) 

      // Format as "Jan 2024" 
      return baseDate.toLocaleString("default", {
        month: "short",
        year: "numeric",
      });
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
