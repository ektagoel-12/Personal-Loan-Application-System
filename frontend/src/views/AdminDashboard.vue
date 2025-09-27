<template>
  <div class="dashboard p-6">
    <h1 class="text-2xl font-bold mb-2">Admin Dashboard</h1>
    <p class="mb-6 text-gray-600">Monitor and manage loan applications</p>

    <div class="grid grid-cols-4 gap-4 mb-8">
      <div class="card">
        <h3>Total Applications</h3>
        <p class="text-2xl">{{ stats?.totalApplications }}</p>
        <small>+{{ stats?.growth }}% from last month</small>
      </div>
      <div class="card">
        <h3>Approval Rate</h3>
        <p class="text-2xl">{{ stats?.approvalRate }}%</p>
        <small>+{{ stats?.approvalGrowth }}% from last month</small>
      </div>
      <div class="card">
        <h3>Pending Review</h3>
        <p class="text-2xl">{{ stats?.pending }}</p>
        <small>Requires attention</small>
      </div>
      <div class="card">
        <h3>Avg. Income</h3>
        <p class="text-2xl">₹{{ stats?.avgIncome }}</p>
        <small>Monthly average</small>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-6 mb-8">
      <div class="card">
        <h3 class="mb-2">Application Trends</h3>
        <BarChart v-if="barChartData.months.length" :data="barChartData" />
      </div>
      <div class="card">
        <h3 class="mb-2">Application Status</h3>
        <PieChart v-if="pieChartData.approved + pieChartData.pending + pieChartData.rejected > 0" 
          :data="pieChartData" />
      </div>
    </div>

    <div class="card">
      <h3 class="mb-4">List of Pending Applications</h3>
      <div class="flex items-center gap-4 mb-4">
      <input v-model="search" placeholder="Search applications..." class="border rounded px-3 py-2 mb-4" />
      </div>

      <table class="w-full border">
        <thead>
          <tr class="bg-gray-100 text-left">
            <th>Application ID</th>
            <th>Applicant</th>
            <th>Amount</th>
            <th>Income</th>
            <th>Credit Score</th>
            <th>Loan Type</th>
            <th>Applied Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="app in filteredApps" :key="app.id">
            <td>{{ app.id }}</td>
            <td>{{ app.name }}</td>
            <td>₹{{ app.amount }}</td>
            <td>₹{{ app.income }}</td>
            <td>
              <span :class="['px-2 py-1 rounded text-white',
                  app.creditScore >= 700 ? 'bg-green-600' : 'bg-red-500'
                ]">
                {{ app.creditScore }}
              </span>
            </td>
            <td>{{ app.loanType }}</td>
            <td>{{ app.applicationDate }}</td>
            <td>
              <span :class="['status', app.status.toLowerCase()]">
                {{ app.status }}
              </span>
            </td>
            <td>
              <button @click="goToEdit(app.id)"
                class="bg-blue-500 text-white px-3 py-1 rounded"> Edit </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import BarChart from "./BarChart.vue";
import PieChart from "./PieChart.vue";

const store = useStore();
const router = useRouter();

const search = ref("");

const stats = computed(() => store.getters.stats);
const applications = computed(() => store.getters.applications);
const isLoading = computed(() => store.getters.isLoading);

const filteredApps = computed(() =>
  applications.value.filter((a) =>
    (a.applicant?.toLowerCase() ?? '').includes(search.value?.toLowerCase() ?? '') && (a.status === 'PENDING' || a.status==='NEW') )
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
});
</script>

<style scoped>
.card {
  @apply bg-white shadow rounded p-4;
}
</style>
