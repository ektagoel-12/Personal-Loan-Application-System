<script setup>
import { ref, onMounted } from "vue"
import ticketList from '../store/ticket.json' //dummy data

// Tickets data (in real app, fetch from backend API)
const tickets = ref([])

onMounted(() => {
  // Mock data, replace with API call later
  tickets.value = ticketList
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
</script>

<template>
  <div class="max-w-4xl mx-auto p-6 bg-white shadow-lg rounded-2xl">
    <h2 class="text-2xl font-bold text-gray-800 mb-6">My Tickets</h2>

    <div v-if="tickets.length > 0" class="space-y-4">
      <div
        v-for="ticket in tickets"
        :key="ticket.id"
        class="border rounded-lg p-4 shadow-sm"
      >
        <div class="flex justify-between items-center mb-2">
          <h3 class="text-lg font-semibold text-gray-800">{{ ticket.subject }}</h3>
          <span
            class="px-3 py-1 text-sm font-medium rounded-full"
            :class="statusClass(ticket.status)"
          >
            {{ ticket.status }}
          </span>
        </div>

        <p class="text-sm text-gray-600">
          <span class="font-medium">Request Type:</span> {{ ticket.requestType }}
        </p>

        <p class="mt-2 text-gray-700">{{ ticket.description }}</p>

        <div v-if="ticket.adminResponse" class="mt-3 p-3 bg-gray-50 border-l-4 border-indigo-500 rounded">
          <p class="text-sm text-gray-700">
            <span class="font-medium">Admin Update:</span> {{ ticket.adminResponse }}
          </p>
        </div>

        <!-- Black Button -->
        <button class="mt-3 px-4 py-2 bg-black text-white rounded hover:bg-gray-800">
          View Details
        </button>
      </div>
    </div>

    <div v-else class="text-center text-gray-600">
      No tickets created yet.
    </div>
  </div>
</template>
