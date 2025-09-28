<template>
  <div class="dashboard p-6 font-sans bg-[#ffffff] text-[#1f2937]">
    <h1 class="text-2xl font-bold text-[#1f2937] mb-2">Admin Dashboard</h1>
    <p class="mb-6 text-[#6b7280]">Monitor and manage loan applications</p>

    <div class="grid grid-cols-4 gap-4 mb-8">
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-[#7e22ce] mb-1">Total Applications</h3>
        <p class="text-2xl font-bold text-[#1f2937]">{{ stats?.totalApplications }}</p>
        <small class="text-sm ">+{{ stats?.growth }}% from last month</small>
      </div>
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-[#7e22ce] mb-1">Approval Rate</h3>
        <p class="text-2xl font-bold text-[#1f2937]">{{ stats?.approvalRate }}%</p>
        <small class="text-sm ">+{{ stats?.approvalGrowth }}% from last month</small>
      </div>
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-[#7e22ce] mb-1">Pending Review</h3>
        <p class="text-2xl font-bold text-[#1f2937]">{{ stats?.pending }}</p>
        <small class="text-sm ">Requires attention</small>
      </div>
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-[#7e22ce] mb-1">Avg. Income</h3>
        <p class="text-2xl font-bold text-[#1f2937]">₹{{ stats?.avgIncome }}</p>
        <small class="text-sm ">Monthly average</small>
      </div>
    </div>

    <div class="card bg-[#ffffff] border border-[#e5e7eb] rounded-xl p-6 shadow-sm grid grid-cols-2 gap-6 mb-8">
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class=" text-sm font-medium text-[#7e22ce] mb-2">Application Trends</h3>
        <BarChart v-if="barChartData.months.length" :data="barChartData" />
      </div>
      <div class="card bg-[#f3e8ff] border border-[#e9d5ff] rounded-xl p-4 shadow-sm">
        <h3 class="text-sm font-medium text-[#7e22ce] mb-2">Application Status</h3>
        <PieChart v-if="pieChartData.approved + pieChartData.pending + pieChartData.rejected > 0" 
          :data="pieChartData" />
      </div>
    </div>
    <div class="bg-white shadow-lg rounded-xl p-6 border border-gray-100">
  <!-- Title -->
  <h3 class="text-xl flex gap-2 items-center font-bold text-[#1f2937] mb-6">
  <ClipboardList class="text-[#7e22ce]" />
  List of Pending Applications
</h3>

  <!-- Search -->
    <div class="flex items-center mb-6">
      <input 
        v-model="search" 
        placeholder="Search applications..." 
        class="w-72 border border-[#d1d5db] rounded-lg px-4 py-2 shadow-sm focus:ring-2 focus:ring-[#7e22ce] focus:border-[#7e22ce] outline-none transition"
      />
    </div>

    <!-- Table -->
    <div class="overflow-x-auto rounded-lg border border-gray-200">
      <table class="w-full text-sm text-left text-gray-700">
       <thead class="bg-[#f3e8ff] text-[#7e22ce] text-sm uppercase font-semibold">
          <tr>
            <th class="px-4 py-3">Application ID</th>
            <th class="px-4 py-3">Applicant</th>
            <th class="px-4 py-3">Amount</th>
            <th class="px-4 py-3">Income</th>
            <th class="px-4 py-3">Credit Score</th>
            <th class="px-4 py-3">Loan Type</th>
            <th class="px-4 py-3">Applied Date</th>
            <th class="px-4 py-3">Status</th>
            <th class="px-4 py-3 text-center">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="app in filteredApps" 
            :key="app.id"
            class="hover:bg-gray-50 transition"
          >
            <td class="px-4 py-3 font-medium text-gray-900">#{{ app.id }}</td>
            <td class="px-4 py-3">{{ app.name }}</td>
            <td class="px-4 py-3">₹{{ app.amount.toLocaleString() }}</td>
            <td class="px-4 py-3">₹{{ app.income.toLocaleString() }}</td>
            <td class="px-4 py-3">
              <span 
                :class="[
                  'px-2 py-1 rounded-md text-xs font-semibold',
                  app.creditScore >= 700 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                ]"
              >
                {{ app.creditScore }}
              </span>
            </td>
            <td v-if="loanTypeLabel[app.loanType]" class="px-4 py-3">{{ loanTypeLabel[app.loanType].label }}</td>
            <td class="px-4 py-3">{{ app.applicationDate }}</td>
            <td class="px-4 py-3">
              <span 
                :class="[
                  'px-3 py-1 rounded-full text-xs font-semibold shadow-sm',
                  app.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700 border border-yellow-200' :
                  'bg-blue-100 text-blue-700 border border-blue-200'
                ]"
              >
                {{ app.status }}
              </span>
            </td>
            <td class="px-4 py-3 text-center">
              <button 
                @click="goToEdit(app.id)"
                class="bg-[#7e22ce] hover:bg-[#6b21a8] text-white px-4 py-2 rounded-lg shadow-sm text-sm font-medium transition"              >
                Edit
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { ClipboardList } from "lucide-vue-next";
import BarChart from "./BarChart.vue";
import PieChart from "./PieChart.vue";

const store = useStore();
const router = useRouter();

const search = ref("");

const stats = computed(() => store.getters.stats);
const applications = computed(() => store.getters.applications)
const loanTypeLabel = computed(()=>(store.state.interestRate))


const filteredApps = computed(() =>
  applications.value.filter((a) => {
    const term = search.value?.toLowerCase() ?? "";

    return (
      (a.name?.toLowerCase() ?? "").includes(term) || String(a.id).includes(term) ||           
      (loanTypeLabel[a.loanType]?.label?.toLowerCase() ?? "").includes(term) // loan type
    ) && (a.status === "PENDING" || a.status === "NEW");
  })
);



const barChartData = computed(() => {
  const trends = stats.value?.monthlyTrends;

  if (Array.isArray(trends)) {
    // Case: API response (array of objects)
    const months = [];
    const values = [];
    trends.forEach((trend) => {
      const month = Object.keys(trend)[0];
      months.push(month);
      values.push(trend[month]);
    });
    return { months, values };
  } else if (trends && trends.months && trends.values) {
    // Case: initial state (object with months and values)
    return { months: trends.months, values: trends.values };
  } else {
    return { months: [], values: [] };
  }
});

// Computed for PieChart (status distribution)
const pieChartData = computed(() => {
  const dist = stats.value?.statusDistribution || {};
  return {
    approved: dist.APPROVED ?? dist.approved ?? 0,
    pending: dist.PENDING ?? dist.pending ?? 0,
    rejected: dist.REJECTED ?? dist.rejected ?? 0,
  };
});

const fetchDashboardData = () => store.dispatch("fetchDashboardData");

const updateStatus= (id, status) => {
  const payload = {
    status,
    reviewedBy: "Admin User",
    reviewedAt: new Date().toISOString(),
    reviewRemarks: "Auto status update"
  };
  store.dispatch("updateApplicationStatus", { id, payload });
}

const autoUpdateStatuses = () => {
  const now = new Date();
  applications.value.forEach(app => {
    if (app.status === 'PENDING') {
      const appDate = new Date(app.appliedDate + "T00:00:00"); // ensures valid Date
      const hoursPassed = (now - appDate) / (1000 * 60 * 60);
      if (hoursPassed < 48) {
        updateStatus(app.id, 'NEW');
      }
    }
  });
};

const goToEdit = (id) => {
  router.push(`/admin/loans/${id}`);
};

onMounted(() => {
  fetchDashboardData();
  autoUpdateStatuses();
  console.log("Mounting")
  for(let loans of applications.value){
  console.log(loanTypeLabel[loans.status])
}
});
</script>

<style scoped>
.card {
  @apply bg-white shadow rounded p-4;
}
</style>
