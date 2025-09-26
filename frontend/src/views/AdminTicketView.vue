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

const statusOptions = ["OPEN", "RESOLVED", "CLOSED"]
// Function to update ticket status via API
const updateStatus = async (ticket, newStatus) => {
  try {
    const endpoint = `/ticket/admin/${ticket.id}/${newStatus}`;
    const response = await makeRequestWithToken("PATCH", endpoint);
    
    // Update the local ticket object with new status
    ticket.status = newStatus;

    alert(`Status updated to ${newStatus} for ticket #${ticket.id}`);
    console.log("API response:", response.data);
  } catch (err) {
    console.error("Error updating ticket status:", err);
    alert(`Failed to update status for ticket #${ticket.id}`);
  }
};

const updateResponse = async (ticket) => {
  if (!ticket.response || ticket.response.trim() === "") {
    alert("Please write a response before updating.");
    return;
  }

  try {
    console.log(ticket.response)
    const endpoint = "/ticket/admin/" +ticket.id+"/response";
    const res = await makeRequestWithToken("PATCH", endpoint, {
      response: ticket.response,
    });

    console.log("Updated ticket:", res);
    if(res==null) return;

    // Find index of the updated ticket
    const index = tickets.value.findIndex((t) => t.id === ticket.id);
    if (index !== -1) {
      // Replace old ticket with updated one (reactive update)
      tickets.value[index] = res.data;
    }

    alert(`Response updated for ticket #${ticket.id}`);
  } catch (err) {
    console.error("Error during response update:", err);
    alert("Failed to update ticket response. Please try again.");
  }
};


</script>

<template>
  <div class="max-w-6xl mx-auto p-6 bg-gray-50">
    <h2 class="text-3xl font-bold mb-8 text-gray-900 text-center">
      🎟️ Admin Ticket Dashboard
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
              Ticket #{{ ticket.id }} • User ID: {{ ticket.userId }}
            </p>
          </div>

          <div class="flex items-center space-x-2">
            <span
              class="text-xs font-medium px-2 py-1 rounded-full bg-blue-100 text-blue-700"
            >
              {{ ticket.type }}
            </span>
            <select
              v-model="ticket.status"
              @change="updateStatus(ticket, ticket.status)"
              class="border rounded px-3 py-1 text-sm focus:ring-2 focus:ring-indigo-500"
            >
              <option
                v-for="status in statusOptions"
                :key="status"
                :value="status"
              >
                {{status }}
              </option>
            </select>
          </div>
        </div>

        <!-- Description -->
        <p class="text-gray-700 mb-4">{{ ticket.description }}</p>

        <!-- Timestamps -->
        <div class="flex items-center text-xs text-gray-500 space-x-6 mb-4">
          <p class="flex items-center space-x-1">
            <span class="material-icons text-gray-400 text-sm">schedule</span>
            <span>Created: {{ new Date(ticket.createAt).toLocaleString() }}</span>
          </p>
          <p class="flex items-center space-x-1">
            <span class="material-icons text-gray-400 text-sm">update</span>
            <span>Updated: {{ new Date(ticket.updatedAt).toLocaleString() }}</span>
          </p>
        </div>

        <!-- Admin Response -->
        <div class="mt-3">
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Admin Response</label
          >
          <textarea
            v-model="ticket.response"
            rows="2"
            placeholder="Write your response..."
            class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          ></textarea>
                <button
            @click="updateResponse(ticket)"
            class="mt-3 bg-black text-white px-5 py-2 rounded-lg hover:bg-gray-800 transition-colors duration-200 shadow-sm"
          >
            Update Response
          </button>
        </div>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-12">
      <p class="text-lg">No tickets available 🚫</p>
    </div>
  </div>
</template>
