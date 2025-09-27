<template>
  <div class="max-w-3xl mx-auto p-6 bg-gray-50 min-h-screen">
    <h2 class="text-3xl font-bold text-gray-900 mb-8 text-center">Ticket Details</h2>

    <div v-if="ticket" class="bg-white rounded-2xl shadow-lg p-6 hover:shadow-xl transition duration-200">
      <!-- Header -->
      <div class="flex justify-between items-start mb-4">
        <div>
          <h3 class="text-2xl font-semibold text-gray-800">{{ ticket.subject }}</h3>
          <p class="text-sm text-gray-500 mt-1">Ticket #{{ ticket.id }}</p>
          <p class="inline-block mt-2 px-2 py-1 text-xs font-medium text-blue-600 bg-blue-100 rounded">
            {{ ticket.type }}
          </p>
        </div>
        <span
          class="px-3 py-1 text-sm font-medium rounded-full"
          :class="statusClass(ticket.status)"
        >
          {{ ticket.status }}
        </span>
      </div>

      <!-- Ticket Info -->
      <div class="text-sm text-gray-600 space-y-2 mb-4">
        <p v-if="ticket.userId"><span class="font-medium">User ID:</span> {{ ticket.userId }}</p>
        <p v-if="ticket.loanId"><span class="font-medium">Loan ID:</span> {{ ticket.loanId }}</p>
        <p><span class="font-medium">Created:</span> {{ ticket.createAt }}</p>
        <p><span class="font-medium">Updated:</span> {{ ticket.updatedAt }}</p>
      </div>

      <!-- Description -->
      <p class="text-gray-700 mb-4"><span class="font-medium">Description:</span> {{ ticket.description }}</p>

      <!-- Admin Response -->
      <div v-if="ticket.response" class="mt-3 p-3 bg-gray-50 border-l-4 border-black rounded">
        <p class="text-sm text-gray-700">
          <span class="font-medium">Admin Response:</span> {{ ticket.response }}
        </p>
      </div>

      <!-- Optional button -->
      <button
        class="mt-5 bg-black text-white px-5 py-2 rounded-lg hover:bg-gray-800 transition-colors duration-200 shadow-sm"
      >
        🔍 View More
      </button>
    </div>

    <div v-else class="text-center text-gray-600 py-10">
      Loading ticket details...
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { makeRequestWithToken } from "@/utils/requests";

const ticket = ref(null);
const route = useRoute();

// Status badge helper
const statusClass = (status) => {
  switch (status) {
    case "OPEN": return "bg-yellow-100 text-yellow-800";
    case "RESOLVED": return "bg-green-100 text-green-800";
    case "CLOSED": return "bg-red-100 text-red-800";
    default: return "bg-gray-100 text-gray-800";
  }
};

// Fetch ticket data dynamically on mount
onMounted(async () => {
  try {
    const ticketId = route.params.ticketId; // assume route param :ticketId
    const response = await makeRequestWithToken("GET", `/ticket/admin/${ticketId}`);
    ticket.value = response.data;
    console.log("Fetched ticket:", ticket.value);
  } catch (err) {
    console.error("Error fetching ticket:", err);
  }
});
</script>
