<script setup>
import { ref, onMounted } from "vue"
import { makeRequestWithToken } from "@/utils/requests"
import {useStore} from 'vuex'
// Mock tickets data (replace with API call)
const tickets = ref([])
const store=useStore()

// import ticketList from '../store/ticket.json' //dummy data


onMounted( async() => {
  try {
    const endpoint='/ticket/admin';
    const response = await makeRequestWithToken("GET",endpoint);
    console.log("Raw response:", response.data);

    tickets.value = response.data ? response.data : [];

    console.log("Tickets loaded:", tickets.value);
    tickets.value.forEach(ticket=>console.log(ticket))
  } catch (err) {
    console.error("Error fetching tickets:", err);
  }
})

// Status options
const statusOptions = ["OPEN", "RESOLVED", "CLOSED"]

// Function to update ticket status
const updateStatus = (ticket, newStatus) => {
  ticket.status = newStatus
  alert(`Status updated to ${newStatus} for ticket #${ticket.id}`)
}

// Function to update admin response
const updateResponse = async(ticket) => {
  if (!ticket.adminResponse) {
    try {
    const endpoint='/'+ticket.id+'/response';
    const response = await makeRequestWithToken("POST",endpoint);
    console.log("Raw response:", response.data);
    ticket=response.data
  } catch (err) {
    console.error("Error during response update:", err);
  }
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
           <p class="text-sm font-medium text-blue-600 bg-blue-100 px-2 py-1 rounded">
              Type: {{ ticket.type }}  </p>
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
