<script setup>
import { ref, onMounted, computed } from "vue";
import { makeRequestWithToken } from "@/utils/requests";
import { useStore } from "vuex";
import { ArrowDownUp,BookOpenCheck } from "lucide-vue-next"
import TicketPopup from "./TicketDetailsView.vue";
import router from "@/router";

const tickets = ref([]);
const store = useStore();

// Popup state
const showTicketPopup = ref(false);
const selectedTicketId = ref(null);

// Filter & Sort state
const filterStatus = ref("All");
const sortByLatest = ref("desc");

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
};

const openPopup = (id) => {
  selectedTicketId.value = Number(id);
  showTicketPopup.value = true;
};

const filteredTickets = computed(() => {
  let filtered = tickets.value;

  if (filterStatus.value !== "All") {
    filtered = filtered.filter(ticket => ticket.status === filterStatus.value);
  }

  filtered.sort((a, b) => {
    const dateA = new Date(a.updatedAt);
    const dateB = new Date(b.updatedAt);
    return sortByLatest.value === "desc" ? dateB - dateA : dateA - dateB;
  });

  return filtered;
});

// Raise Ticket button action
const raiseTicket = () => {
  router.push('/raise-ticket')
};
</script>

<template>
  <div class="max-w-6xl mx-auto p-6 bg-gray-50 relative">
    <div class="flex justify-between items-center mb-8">
      <h2 class="text-3xl flex gap-2 items-center font-bold text-gray-900"> <BookOpenCheck /> My Tickets</h2>
      <button
        @click="raiseTicket"
        class="bg-black hover:bg-blue-700 text-white px-5 py-2 rounded-lg shadow-md transition transform hover:-translate-y-0.5 hover:shadow-lg"
      >
        + Raise Ticket
      </button>
    </div>

    <!-- Filters & Sort -->
    <div class="flex flex-wrap justify-between items-center mb-6 gap-4">
      <select v-model="filterStatus" class="border rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none">
        <option>All</option>
        <option>OPEN</option>
        <option>RESOLVED</option>
        <option>CLOSED</option>
      </select>
      
          
      <div class="flex items-center gap-3">
        <ArrowDownUp class="w-4 h-4 text-gray-400" />
        
        <div 
          @click="sortByLatest = sortByLatest === 'desc' ? 'asc' : 'desc'"
          class="relative inline-flex items-center cursor-pointer select-none"
        >
          <!-- Track -->
          <div 
            class="w-32 h-8 rounded-full transition-colors duration-300"
            :class="sortByLatest === 'desc' ? 'bg-blue-100' : 'bg-gray-100'"
          >
          </div>
          
          <!-- Handle -->
          <div
            class="absolute left-0.5 top-0.5 w-14 h-7 bg-white rounded-full shadow-sm transition-transform duration-300 flex items-center justify-center"
            :class="sortByLatest === 'desc' ? 'translate-x-16' : 'translate-x-0'"
          >
            <span class="text-xs font-medium text-gray-600">
              {{ sortByLatest === 'desc' ? 'New' : 'Old' }}
            </span>
          </div>
        </div>
      </div>

    </div>

    <!-- Tickets List -->
    <div v-if="tickets.length" class="space-y-6">
      <div
        v-for="ticket in filteredTickets"
        :key="ticket.id"
        class="bg-white border rounded-2xl p-6 shadow-md hover:shadow-xl transition duration-300"
      >
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-xl font-semibold text-gray-800">{{ ticket.subject }}</h3>
            <p class="text-sm text-gray-500">Ticket #{{ ticket.id }}</p>
            <p class="text-xs font-medium text-blue-600 bg-blue-100 px-2 py-1 rounded inline-block mt-1">
              • Request: {{ ticket.type }}
            </p>
          </div>
          <span class="px-3 py-1 text-sm font-medium rounded-full" :class="statusClass(ticket.status)">
            {{ ticket.status }}
          </span>
        </div>
        <p class="text-gray-700 mb-4">{{ ticket.description }}</p>
        <div class="flex flex-wrap text-xs text-gray-500 space-x-6 mb-4">
          <p>Created: {{ ticket.createAt }}</p>
          <p>Updated: {{ ticket.updatedAt }}</p>
        </div>
        <div v-if="ticket.response" class="mt-3 p-3 bg-gray-50 border-l-4 border-black rounded">
          <p class="text-sm text-gray-700"><span class="font-medium">Admin Response:</span> {{ ticket.response }}</p>
        </div>
        <button
          class="mt-4 bg-black text-white px-5 py-2 rounded-lg hover:bg-gray-800 transition-colors duration-200 shadow-sm"
          @click="openPopup(ticket.id)"
        >
          View Details
        </button>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-12">
      <p class="text-lg">No tickets created yet </p>
    </div>

    <!-- Ticket Popup -->
    <TicketPopup
      v-if="showTicketPopup"
      :ticket-id="selectedTicketId"
      @close="showTicketPopup = false"
    />
  </div>
</template>
