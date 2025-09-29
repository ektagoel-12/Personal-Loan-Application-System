<template>
  <div class="p-4 space-y-6 font-inter text-[#1f2937] bg-white">
    <!-- Filters Section -->
    <div class="flex space-x-4 bg-[#f3e8ff] p-4 rounded-lg shadow">
      <!-- Name Filter -->
      <div class="flex flex-col w-1/3">
        <label for="nameFilter" class="font-medium text-sm mb-1"
          >Filter by Name</label
        >
        <input
          id="nameFilter"
          v-model="nameFilter"
          type="text"
          class="px-3 py-2 border border-[#7e22ce]/30 rounded-lg focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
          placeholder="Search by name"
        />
      </div>

      <!-- Email Filter -->
      <div class="flex flex-col w-1/3">
        <label for="emailFilter" class="font-medium text-sm mb-1"
          >Filter by Email</label
        >
        <input
          id="emailFilter"
          v-model="emailFilter"
          type="text"
          class="px-3 py-2 border border-[#7e22ce]/30 rounded-lg focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
          placeholder="Search by email"
        />
      </div>

      <!-- Role Filter -->
      <div class="flex flex-col w-1/3">
        <label for="roleFilter" class="font-medium text-sm mb-1"
          >Filter by Role</label
        >
        <select
          id="roleFilter"
          v-model="roleFilter"
          class="px-3 py-2 border border-[#7e22ce]/30 rounded-lg focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
        >
          <option value="">All Roles</option>
          <option value="ADMIN">Admin</option>
          <option value="USER">User</option>
        </select>
      </div>
    </div>

    <!-- Table Section -->
    <div class="overflow-x-auto">
      <table
        class="min-w-full table-auto border-collapse border border-[#f3e8ff] shadow rounded-lg"
      >
        <thead>
          <tr class="bg-[#7e22ce]/10 text-left text-sm font-semibold text-[#7e22ce]">
            <th class="px-4 py-3 border-b">User ID</th>
            <th class="px-4 py-3 border-b">Name</th>
            <th class="px-4 py-3 border-b">Email</th>
            <th class="px-4 py-3 border-b">Income</th>
            <th class="px-4 py-3 border-b">Credit Score</th>
            <th class="px-4 py-3 border-b">Aadhar</th>
            <th class="px-4 py-3 border-b">Role</th>
            <!-- <th class="px-4 py-3 border-b text-center">Make Admin</th> -->
            <th class="px-4 py-3 border-b text-center">change role</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="user in filteredUsers"
            :key="user.id"
            class="border-t hover:bg-[#f3e8ff]/40 transition cursor-pointer"
            @click="toggleShowLoanDetails(user.id)"
          >
            <td class="px-4 py-2 text-sm">{{ user.id }}</td>
            <td class="px-4 py-2 text-sm font-medium">{{ user.name }}</td>
            <td class="px-4 py-2 text-sm">{{ user.email }}</td>
            <td class="px-4 py-2 text-sm">{{ user.income }}</td>
            <td class="px-4 py-2 text-sm">
              <span
                :class="[
                  'px-2 py-1 rounded-md text-xs font-semibold',
                  user.creditScore >= 700
                    ? 'bg-green-100 text-green-700'
                    : 'bg-red-100 text-red-700',
                ]"
              >
                {{ user.creditScore }}
              </span>
            </td>
            <td class="px-4 py-2 text-sm">{{ user.aadhar }}</td>
            <td class="px-4 py-2 text-sm capitalize">{{ user.role }}</td>
            <td class="px-4 py-2 text-center" v-show="user.role === `USER`">
              <button
                @click.stop="changeToAdmin(user.id)"
                class="bg-[#7e22ce] text-white px-3 py-1.5 rounded-lg hover:bg-[#6b21a8] transition text-sm"
              >
                Change Role
              </button>
            </td>
            <td class="px-4 py-2 text-center" v-show="user.role === `ADMIN`">
              <button
                @click.stop="changeToUser(user.id)"
                class="bg-red-500 text-white px-3 py-1.5 rounded-lg hover:bg-red-600 transition text-sm"
              >
                Change Role
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Loan History Modal -->
    <div
      v-if="show"
      class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
    >
      <div
        class="bg-white w-3/4 max-h-[85vh] rounded-xl shadow-2xl overflow-y-auto p-6 relative animate-fade-in"
      >
        <!-- Close Button -->
        <button
          @click="show = false"
          class="absolute top-4 right-4 text-gray-400 hover:text-red-500 text-2xl font-bold"
        >
          ✕
        </button>

        <h2 class="text-2xl font-bold text-[#7e22ce] mb-6">Loan History</h2>

        <div v-if="filteredApplications.length" class="space-y-5">
          <div
            v-for="loan in filteredApplications"
            :key="loan.id"
            class="border border-[#f3e8ff] rounded-lg p-4 bg-[#f9f9f9] shadow-sm hover:shadow-md transition"
          >
            <div class="flex justify-between items-center mb-2">
              <h3 class="text-lg font-semibold">
                Loan #{{ loan.id }}
              </h3>
              <span
                :class="[
                  'px-3 py-1 rounded-full text-xs font-semibold',
                  loan.status === 'APPROVED'
                    ? 'bg-green-100 text-green-700'
                    : loan.status === 'REJECTED'
                    ? 'bg-red-100 text-red-700'
                    : 'bg-[#eab308]/20 text-[#eab308]',
                ]"
              >
                {{ loan.status }}
              </span>
            </div>

            <div class="grid grid-cols-2 gap-x-6 gap-y-2 text-sm">
              <p><span class="font-medium">Amount:</span> ₹{{ loan.amount }}</p>
              <p><span class="font-medium">EMI:</span> ₹{{ loan.emi }}</p>
              <p><span class="font-medium">Interest Rate:</span> {{ loan.interestRate }}%</p>
              <p><span class="font-medium">Tenure:</span> {{ loan.tenure/12 }} yrs</p>
              <p><span class="font-medium">Purpose:</span> {{ loan.purpose }}</p>
              <p><span class="font-medium">Applied Date:</span> {{ loan.appliedDate }}</p>
              <p><span class="font-medium">Application Date:</span> {{ loan.applicationDate }}</p>
              <p><span class="font-medium">Last Updated:</span> {{ loan.lastUpdated }}</p>
            </div>
          </div>
        </div>

        <p v-else class="text-gray-500 text-center py-8">
          No loan history available.
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.font-inter {
  font-family: 'Inter', sans-serif;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.25s ease-out;
}
</style>

<script setup>
import { useStore } from "vuex";
import { makeRequestWithToken } from "@/utils/requests";
import { ref, onMounted, computed } from "vue";
import { useToast } from "vue-toastification";

const store = useStore();
const toast = useToast();

let users = ref([]);
let nameFilter = ref("");
let emailFilter = ref("");
let roleFilter = ref("");
let show = ref(false);
let currUserId = ref(-1);

let filteredApplications = computed(() => {
  return store.state.applications.filter(
    (app) => app.userId == currUserId.value
  );
});

const toggleShowLoanDetails = (id) => {
  currUserId.value = id;
  show.value = true;
};

const filteredUsers = computed(() => {
  return users.value.filter((user) => {
    const matchesName = user.name
      .toLowerCase()
      .includes(nameFilter.value.toLowerCase());
    const matchesEmail = user.email
      .toLowerCase()
      .includes(emailFilter.value.toLowerCase());
    const matchesRole = roleFilter.value
      ? user.role === roleFilter.value
      : true;
    return matchesName && matchesEmail && matchesRole;
  });
});

const changeToAdmin = async (userId) => {
  const response = await makeRequestWithToken("PATCH", `/users/${userId}`, {
    role: "ADMIN",
  });
  if (!response) {
    toast.error("Updating the user failed");
    return;
  }
  let response1 = await makeRequestWithToken("GET", "/users", null);
  if (!response1) {
    toast.error("Failed loading users");
    return;
  }
  response1.data.sort((user1,user2)=>user1.id - user2.id)
  users.value = response1.data;
  toast.info("Update Successfull")
};

const changeToUser = async (userId) => {
  const response = await makeRequestWithToken("PATCH", `/users/${userId}`, {
    role: "USER",
  });
  if (!response) {
    toast.error("Updating the user failed");
    return;
  }
  let response1 = await makeRequestWithToken("GET", "/users", null);
  if (!response1) {
    toast.error("Failed loading users");
    return;
  }
  response1.data.sort((user1,user2)=> user1.id-user2.id)
  users.value = response1.data;
  toast.info("Update Successfull")
};

// lifecycle
onMounted(async () => {
  let response = await makeRequestWithToken("GET", "/users", null);
  if (!response) {
    toast.error("Failed loading users");
    return;
  }
  response.data.sort((user1,user2)=> user1.id - user2.id)
  users.value = response.data;
});
</script>
