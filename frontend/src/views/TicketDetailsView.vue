<template>
  <!-- Overlay -->
  <div class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
    <!-- Modal Card -->
    <div class="bg-white rounded-2xl shadow-lg max-w-md w-full p-6 relative">
      <!-- Close Button -->
      <button @click="$emit('close')" class="absolute top-3 right-3 text-gray-500 hover:text-gray-700 text-xl font-bold">&times;</button>

      <!-- Ticket Details -->
       <div v-if="ticket">
      <h3 class="text-2xl font-semibold text-gray-800 mb-2">{{ ticket.subject }}</h3>
      <p class="text-sm text-gray-500 mb-1">Ticket #{{ ticket.id }}</p>
      <p class="inline-block mt-1 px-2 py-1 text-xs font-medium text-blue-600 bg-blue-100 rounded">{{ ticket.type }}</p>
      <span class="px-3 py-1 text-sm font-medium rounded-full" :class="statusClass(ticket.status)">{{ ticket.status }}</span>

      <!-- User Info -->
      <div class="text-sm text-gray-600 mt-4 space-y-1">
        <p><span class="font-medium">User:</span> {{ ticket.user.name }}</p>
        <p><span class="font-medium">Email:</span> {{ ticket.user.email }}</p>
        <p v-if="ticket.loan"><span class="font-medium">Loan ID:</span> {{ ticket.loan.id }}</p>
      </div>

      <!-- Description -->
      <p class="text-gray-700 mt-4"><span class="font-medium">Description:</span> {{ ticket.description }}</p>
        <div class="flex items-center text-xs text-gray-500 space-x-6 mb-4">
          <p>Created: {{ ticket.createAt }}</p>
          <p>Updated: {{ ticket.updatedAt }}</p>
        </div>
      <!-- Admin Response -->
      <div v-if="ticket.response" class="mt-3 p-3 bg-gray-50 border-l-4 border-green-600 rounded">
        <p class="text-sm text-gray-700"><span class="font-medium">Admin Response:</span> {{ ticket.response }}</p>
      </div>
    </div>
   </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { makeRequestWithToken } from "@/utils/requests";

const props = defineProps({ ticketId: { type: Number, required: true } });
const ticket = ref(null);

const statusClass = (status) => {
  switch (status) {
    case "Open": return "bg-yellow-100 text-yellow-800";
    case "In Progress": return "bg-blue-100 text-blue-800";
    case "Resolved": return "bg-green-100 text-green-800";
    case "Closed": return "bg-gray-100 text-gray-800";
    default: return "bg-gray-100 text-gray-800";
  }
};

const fetchTicket = async (id) => {
  try {
    const response = await makeRequestWithToken("GET", `/ticket/admin/ticketId/${id}`);
    ticket.value = response.data;
  } catch (err) {
    console.error("Error fetching ticket:", err);
  }
};

onMounted(() => { if (props.ticketId) fetchTicket(props.ticketId); });
watch(() => props.ticketId, (newId) => { if (newId) fetchTicket(newId); });
</script>
