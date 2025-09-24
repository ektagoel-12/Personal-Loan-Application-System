<script setup>
import { ref, onMounted } from "vue"

// Mock tickets data (replace with API call)
const tickets = ref([])
import ticketList from '../store/ticket.json' //dummy data
onMounted(() => {
  tickets.value = ticketList
})

// Status options
const statusOptions = ["Open", "In Progress", "Resolved", "Closed"]

// Function to update ticket status
const updateStatus = (ticket, newStatus) => {
  ticket.status = newStatus
  alert(`Status updated to ${newStatus} for ticket #${ticket.id}`)
}

// Function to update admin response
const updateResponse = (ticket) => {
  if (!ticket.adminResponse) {
    alert("Please enter a response before updating")
    return
  }
  alert(`Response updated for ticket #${ticket.id}`)
}
</script>

<template>
  <div class="max-w-6xl mx-auto p-6 bg-white shadow-lg rounded-2xl mt-6">
    <h2 class="text-2xl font-bold mb-6 text-gray-800">Admin Dashboard</h2>

    <div v-if="tickets.length" class="space-y-4">
      <div v-for="ticket in tickets" :key="ticket.id" class="border rounded-lg p-4 shadow-sm">
        <!-- Ticket Header -->
        <div class="flex justify-between items-center mb-2">
          <h3 class="text-lg font-semibold text-gray-800">{{ ticket.subject }}</h3>
          <select v-model="ticket.status" @change="updateStatus(ticket, ticket.status)" class="border rounded px-2 py-1 text-sm">
            <option v-for="status in statusOptions" :key="status" :value="status">{{ status }}</option>
          </select>
        </div>

        <p class="text-sm text-gray-600"><span class="font-medium">User:</span> {{ ticket.userName }}</p>
        <p class="text-sm text-gray-600"><span class="font-medium">Request Type:</span> {{ ticket.requestType }}</p>
        <p class="mt-2 text-gray-700">{{ ticket.description }}</p>

        <!-- Admin Response -->
        <div class="mt-3">
          <label class="block text-sm font-medium text-gray-700 mb-1">Admin Response:</label>
          <textarea
            v-model="ticket.adminResponse"
            rows="2"
            placeholder="Write your response..."
            class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          ></textarea>
          <button
            @click="updateResponse(ticket)"
            class="mt-2 bg-black text-white px-4 py-1 rounded hover:bg-gray-800 transition-colors duration-200"
          >
            Update Response
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-6">
      No tickets available.
    </div>
  </div>
</template>
