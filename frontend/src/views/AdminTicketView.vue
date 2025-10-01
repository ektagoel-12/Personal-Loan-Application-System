<script setup>
import { ref, onMounted, computed } from "vue";
import { useStore } from "vuex";
import { useToast } from "vue-toastification";
import TicketPopup from "./TicketDetailsView.vue";

const store = useStore();
const toast = useToast();


const showTicketPopup = ref(false);
const selectedTicketId = ref(null);

const filterStatus = ref("All");
const sortByLatest = ref("desc");

const statusOptions = ["OPEN", "RESOLVED", "CLOSED"];


onMounted(async () => {
  try {
    await store.dispatch("fetchAllTickets"); // fetch all tickets for admin
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


const openPopup = (id) => {
  selectedTicketId.value = Number(id);
  showTicketPopup.value = true;
};

// Update ticket status
const updateStatus = async (ticket, newStatus) => {
  try {
    await store.dispatch("updateTicketStatus", { id: ticket.id, status: newStatus });
    toast.info(`Status updated to ${newStatus} for ticket #${ticket.id}`);
  } catch (err) {
    console.error(err);
    toast.error(`Failed to update status for ticket #${ticket.id}`);
  }
};

// Update admin response
const updateResponse = async (ticket) => {
  if (!ticket.response || ticket.response.trim() === "") {
    return toast.error("Response cannot be empty");
  }
  try {
    await store.dispatch("updateTicketResponse", { id: ticket.id, response: ticket.response });
    toast.info(`Response updated for ticket #${ticket.id}`);
  } catch (err) {
    console.error(err);
    toast.error("Failed to update response");
  }
};


const filteredTickets = computed(() => {
  let filtered = store.getters.allTickets || [];


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
</script>

<template>
  <div class="max-w-6xl mx-auto p-6 bg-white font-inter text-gray-800 relative">
  
    <h2 class="text-3xl font-bold mb-6 text-[#1f2937] text-center">
       Admin Ticket Dashboard
    </h2>

    <!-- Filter & Sort -->
    <div class="flex justify-between items-center mb-6">
      <select
        v-model="filterStatus"
        class="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-[#7e22ce] focus:border-[#7e22ce]"
      >
        <option>All</option>
        <option>OPEN</option>
        <option>RESOLVED</option>
        <option>CLOSED</option>
      </select>

      <!-- Sort Toggle -->
        <div
          @click="sortByLatest = sortByLatest === 'desc' ? 'asc' : 'desc'"
          class="relative inline-flex items-center cursor-pointer select-none"
        >
          <!-- Track -->
          <div
            class="w-32 h-8 rounded-full transition-colors duration-300"
            :class="sortByLatest === 'desc' ? 'bg-[#f3e8ff]' : 'bg-gray-100'"
          ></div>

          <!-- Handle -->
          <div
            class="absolute left-0.5 top-0.5 w-14 h-7 bg-white rounded-full shadow-sm transition-transform duration-300 flex items-center justify-center border border-gray-200"
            :class="sortByLatest === 'desc' ? 'translate-x-16' : 'translate-x-0'"
          >
            <span class="text-xs font-medium text-gray-600">
              {{ sortByLatest === 'desc' ? 'New' : 'Old' }}
            </span>
          </div>
        </div>

    </div>

    <!-- Tickets List -->
    <div v-if="filteredTickets.length" class="space-y-6">
      <div
        v-for="ticket in filteredTickets"
        :key="ticket.id"
        class="bg-white border border-gray-200 rounded-xl p-6 shadow hover:shadow-lg transition duration-200"
      >
\
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-xl font-semibold text-gray-900">
              {{ ticket.subject }}
            </h3>
            <p class="text-sm text-gray-500">
              Ticket #{{ ticket.id }} • User ID: {{ ticket.userId }}
            </p>
          </div>

          <div class="flex items-center space-x-2">
            <span
              class="text-xs font-medium px-2 py-1 rounded-full bg-[#f3e8ff] text-[#7e22ce]"
            >
              {{ ticket.type }}
            </span>
            <select
              v-model="ticket.status"
              @change="updateStatus(ticket, ticket.status)"
              class="border border-gray-300 rounded-lg px-3 py-1 text-sm focus:ring-2 focus:ring-[#7e22ce]"
            >
              <option
                v-for="status in statusOptions"
                :key="status"
                :value="status"
              >
                {{ status }}
              </option>
            </select>
          </div>
        </div>


        <p class="text-gray-700 mb-4">{{ ticket.description }}</p>


        <div class="flex items-center text-xs text-gray-500 space-x-6 mb-4">
          <p>Created: {{ new Date(ticket.createAt).toLocaleString() }}</p>
          <p>Updated: {{ new Date(ticket.updatedAt).toLocaleString() }}</p>
        </div>


        <div class="mt-3">
          <label class="block text-sm font-medium text-gray-700 mb-1"
            >Admin Response</label
          >
          <textarea
            v-model="ticket.response"
            rows="2"
            placeholder="Write your response..."
            class="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#7e22ce]"
          ></textarea>

          <div class="mt-3 flex justify-between items-center">
        
            <button
              @click="updateResponse(ticket)"
              class="bg-[#7e22ce] text-white px-5 py-2 rounded-lg hover:bg-[#6b21a8] transition-colors duration-200 shadow-sm"
            >
              Update Response
            </button>

            <button
              @click="openPopup(ticket.id)"
              class="bg-[#eab308] text-[#1f2937] px-5 py-2 rounded-lg hover:bg-yellow-500 transition-colors duration-200 shadow-sm"
            >
              View Details
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-gray-600 text-center py-12">
      <p class="text-lg">No tickets available 🚫</p>
    </div>


    <TicketPopup
      v-if="showTicketPopup"
      :ticket-id="selectedTicketId"
      @close="showTicketPopup = false"
    />
  </div>
</template>

<style>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap");

.font-inter {
  font-family: "Inter", sans-serif;
}
</style>

