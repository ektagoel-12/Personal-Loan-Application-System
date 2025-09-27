<script setup>
import { ref, onMounted } from "vue"
// import ticketList from '../store/ticket.json' //dummy data
import { makeRequestWithToken } from "@/utils/requests"
import {useStore} from 'vuex'
import router from "@/router"

// Tickets data (in real app, fetch from backend API)
const tickets = ref([])
const store=useStore()


onMounted( async() => {
  try {
    const endpoint='/ticket/user/'+store.getters.currentUser.email;
    const response = await makeRequestWithToken("GET",endpoint);
    console.log("Raw response:", response.data);

    // make sure it's an array
    tickets.value = response.data ? response.data : [];

    console.log("Tickets loaded:", tickets.value);
    tickets.value.forEach(ticket=>console.log(ticket))
  } catch (err) {
    console.error("Error fetching tickets:", err);
  }
})

// Function to style status
const statusClass = (status) => {
  switch (status) {
    case "Open":
      return "bg-yellow-100 text-yellow-800"
    case "In Progress":
      return "bg-blue-100 text-blue-800"
    case "Resolved":
      return "bg-green-100 text-green-800"
    case "Closed":
      return "bg-gray-100 text-gray-800"
    default:
      return "bg-gray-100 text-gray-800"
  }
}

// Navigate to ticket details page
const goToTicket = (id) => {
  router.push({ name: "TicketDetails", params: { ticketId: id } });
};
</script>

<template>
  <div class="max-w-5xl mx-auto p-6 bg-gray-50">
    <h2 class="text-3xl font-bold mb-8 text-gray-900 text-center">
      🎟️ My Tickets
    </h2>

    <div v-if="tickets.length" class="space-y-6">
      <div
        v-for="ticket in tickets"
        :key="ticket.id"
        class="bg-white border rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200"
      >
        <!-- Header -->
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-xl font-semibold text-gray-800">
              {{ ticket.subject }}
            </h3>
            <p class="text-sm text-gray-500">
              Ticket #{{ ticket.id }} • Request: {{ ticket.requestType }}
            </p>
            <p
              class="text-xs font-medium text-blue-600 bg-blue-100 px-2 py-1 rounded inline-block mt-1"
            >
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

        <!-- Description -->
        <p class="text-gray-700 mb-4">{{ ticket.description }}</p>

        <!-- Timestamps -->
        <div class="flex items-center text-xs text-gray-500 space-x-6 mb-4">
          <p class="flex items-center space-x-1">
            <span class="material-icons text-gray-400 text-sm">schedule</span>
            <span>Created: {{ ticket.createAt }}</span>
          </p>
          <p class="flex items-center space-x-1">
            <span class="material-icons text-gray-400 text-sm">update</span>
            <span>Updated: {{ ticket.updatedAt }}</span>
          </p>
        </div>

        <!-- Admin Response -->
        <div
          v-if="ticket.response"
          class="mt-3 p-3 bg-gray-50 border-l-4 border-black rounded"
        >
          <p class="text-sm text-gray-700">
            <span class="font-medium">Admin Response:</span> {{ ticket.response }}
          </p>
        </div>

        <!-- Black Button -->
        <button
          class="mt-4 bg-black text-white px-5 py-2 rounded-lg hover:bg-gray-800 transition-colors duration-200 shadow-sm"
          @click="goToTicket(ticket.id)"
        >
          🔍 View Details
        </button>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-12">
      <p class="text-lg">No tickets created yet 🚫</p>
    </div>
  </div>
</template>
