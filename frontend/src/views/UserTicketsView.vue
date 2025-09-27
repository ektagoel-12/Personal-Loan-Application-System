<script setup>
import { ref, onMounted, computed } from "vue";
import { makeRequestWithToken } from "@/utils/requests";
import { useStore } from "vuex";
import TicketPopup from "./TicketDetailsView.vue";

const tickets = ref([]);
const store = useStore();

// Popup state
const showTicketPopup = ref(false);
const selectedTicketId = ref(null);

// Filter & Sort state
const filterStatus = ref("All"); // default: show all
const sortByLatest = ref("desc"); // "desc" or "asc"

onMounted(async () => {
  try {
    const endpoint = '/ticket/user/email/' + store.getters.currentUser.email;
    const response = await makeRequestWithToken("GET", endpoint);
    tickets.value = response.data || [];
  } catch (err) {
    console.error("Error fetching tickets:", err);
  }
});

const statusClass = (status) => {
  switch (status) {
    case "Open": return "bg-yellow-100 text-yellow-800";
    case "In Progress": return "bg-blue-100 text-blue-800";
    case "Resolved": return "bg-green-100 text-green-800";
    case "Closed": return "bg-gray-100 text-gray-800";
    default: return "bg-gray-100 text-gray-800";
  }
}

// Open popup
const openPopup = (id) => {
  selectedTicketId.value = Number(id);
  showTicketPopup.value = true;
};

// Computed filtered & sorted tickets
const filteredTickets = computed(() => {
  let filtered = tickets.value;

  // Filter by status
  if (filterStatus.value !== "All") {
    filtered = filtered.filter(ticket => ticket.status === filterStatus.value);
  }

  // Sort by updatedAt
  filtered.sort((a, b) => {
    const dateA = new Date(a.updatedAt);
    const dateB = new Date(b.updatedAt);
    return sortByLatest.value === "desc" ? dateB - dateA : dateA - dateB;
  });

  return filtered;
});
</script>



<template>
  <div class="max-w-5xl mx-auto p-6 bg-gray-50 relative">
    <h2 class="text-3xl font-bold mb-8 text-gray-900 text-center">🎟️ My Tickets</h2>

    <div class="flex justify-between items-center mb-6">
  <!-- Filter by Status -->
  <select v-model="filterStatus" class="border rounded px-3 py-1">
    <option>All</option>
    <option>OPEN</option>
    <option>RESOLVED</option>
    <option>CLOSED</option>
  </select>

  <!-- Sort by Latest Update -->
  <button @click="sortByLatest = sortByLatest === 'desc' ? 'asc' : 'desc'"
          class="bg-gray-100 px-3 py-1 rounded hover:bg-gray-200">
    Sort by Latest Update: {{ sortByLatest === 'desc' ? 'Newest First' : 'Oldest First' }}
  </button>
</div>


    <!-- Tickets list -->
    <div v-if="tickets.length" class="space-y-6">
      <div v-for="ticket in filteredTickets" :key="ticket.id" class="bg-white border rounded-xl p-6 shadow-md hover:shadow-lg transition duration-200">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-xl font-semibold text-gray-800">{{ ticket.subject }}</h3>
            <p class="text-sm text-gray-500">Ticket #{{ ticket.id }} </p>
            <p class="text-xs font-medium text-blue-600 bg-blue-100 px-2 py-1 rounded inline-block mt-1">• Request: {{ ticket.type }}</p>
          </div>
          <span class="px-3 py-1 text-sm font-medium rounded-full" :class="statusClass(ticket.status)">
            {{ ticket.status }}
          </span>
        </div>
        <p class="text-gray-700 mb-4">{{ ticket.description }}</p>
        <div class="flex items-center text-xs text-gray-500 space-x-6 mb-4">
          <p>Created: {{ ticket.createAt }}</p>
          <p>Updated: {{ ticket.updatedAt }}</p>
        </div>
        <div v-if="ticket.response" class="mt-3 p-3 bg-gray-50 border-l-4 border-black rounded">
          <p class="text-sm text-gray-700"><span class="font-medium">Admin Response:</span> {{ ticket.response }}</p>
        </div>
        <button class="mt-4 bg-black text-white px-5 py-2 rounded-lg hover:bg-gray-800 transition-colors duration-200 shadow-sm"
                @click="openPopup(ticket.id)">
          View Details
        </button>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-12">
      <p class="text-lg">No tickets created yet 🚫</p>
    </div>

    <!-- Ticket Popup -->
    <TicketPopup
      v-if="showTicketPopup"
      :ticket-id="selectedTicketId"
      @close="showTicketPopup = false"
    />
  </div>
</template>
