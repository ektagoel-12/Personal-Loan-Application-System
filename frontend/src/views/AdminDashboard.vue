<template>
  <div class="dashboard p-6">
    <h1 class="text-2xl font-bold mb-2">Admin Dashboard</h1>
    <p class="mb-6 text-gray-600">Monitor and manage loan applications</p>

    <!-- Top Cards -->
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

    <!-- Charts -->
    <div class="grid grid-cols-2 gap-6 mb-8">
      <div class="card">
        <h3 class="mb-2">Application Trends</h3>
        <BarChart :data="stats?.monthlyTrends" />
      </div>
      <div class="card">
        <h3 class="mb-2">Application Status</h3>
        <PieChart :data="stats?.statusDistribution" />
      </div>
    </div>

    <!-- Pending Applications Table -->
    <div class="card">
      <h3 class="mb-4">Loan Applications</h3>
      <div class="flex items-center gap-4 mb-4">
      <input v-model="search" placeholder="Search applications..." class="border rounded px-3 py-2 mb-4" />
        <select v-model="selectedStatus" class="border rounded px-3 py-2 mb-4">
          <option value="">All Status</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="UNDER_REVIEW">Under Review</option>
        </select>

      </div>


      <table class="w-full border">
        <thead>
          <tr class="bg-gray-100 text-left">
            <th>Application ID</th>
            <th>Applicant</th>
            <th>Amount</th>
            <th>Income</th>
            <th>Credit Score</th>
            <th>Purpose</th>
            <th>Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="app in filteredApps" :key="app.id">
            <td>{{ app.id }}</td>
            <td>{{ app.applicant }}</td>
            <td>₹{{ app.amount }}</td>
            <td>₹{{ app.income }}</td>
            <td>
              <span :class="['px-2 py-1 rounded text-white',
                  app.creditScore >= 700 ? 'bg-green-600' : 'bg-red-500'
                ]">
                {{ app.creditScore }}
              </span>
            </td>
            <td>{{ app.purpose }}</td>
            <td>{{ app.appliedDate }}</td>
            <td>
              <span :class="['status', app.status.toLowerCase()]">
                {{ app.status }}
              </span>
            </td>
            <td>
              <button @click="updateStatus(app.id,'APPROVED')"
                class="bg-green-500 text-white px-2 py-1 rounded">✔</button>
              <button @click="updateStatus(app.id, 'REJECTED')"
                class="bg-red-500 text-white px-2 py-1 rounded ml-2">✖</button>
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
import BarChart from "./BarChart.vue";
import PieChart from "./PieChart.vue";

// Vuex store instance
const store = useStore();

// local state
const search = ref("");

// getters (from vuex)
const stats = computed(() => store.getters.stats);
const applications = computed(() => store.getters.applications);
const isLoading = computed(() => store.getters.isLoading);

const selectedStatus = ref("");


// derived computed
const filteredApps = computed(() =>{
  return applications.value.filter(a =>{
    const matchesSearch = a.applicant.toLowerCase().includes(search.value.toLowerCase());
    const matchesStatus = selectedStatus.value ? a.status === selectedStatus.value : true;

    return matchesSearch && matchesStatus;
  });
});

// actions (dispatch)
const fetchDashboardData = () => store.dispatch("fetchDashboardData");

const updateStatus= (id, status) => {store.dispatch("updateApplicationStatus", { id, status });}
// lifecycle
onMounted(() => {
  fetchDashboardData();
});
</script>


<style scoped>

.card {
  @apply bg-white shadow rounded p-4;
}
</style>
