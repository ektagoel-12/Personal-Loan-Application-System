<script setup>
import { ref } from "vue";
import { useStore } from "vuex";
import { useToast } from "vue-toastification";
import router from "@/router";

const store = useStore();
const toast = useToast();

const requestTypes = [
  "Application_Status",
  "Document_Upload_Issue",
  "EMI_Query",
  "Loan_Closure",
  "Others"
];

const formData = ref({
  type: "",
  subject: "",
  description: "",
  userId: store.getters.currentUser.id,
  LoanId: null
});

const handleSubmit = async () => {
  // Basic validation
  if (!formData.value.type || !formData.value.subject || !formData.value.description) {
    toast.error(" Please fill out all fields before submitting.");
    return;
  }

  try {
    // Dispatch Vuex action to submit ticket
    await store.dispatch("submitTicket", formData.value);
    router.push('/user-ticket-view').then(toast.success("Ticket submitted successfully!"))

  } catch (err) {
    console.error(err);
    toast.success("Failed to submit ticket. Please try again.");
  }
};
</script>

<template>
  <div
    class="my-auto mt-10 max-w-2xl p-6 w-full mx-auto bg-white shadow-lg rounded-2xl border border-gray-200 font-inter"
  >
    <h2 class="text-2xl font-bold mb-6 text-center text-gray-800">
      Raise a Support Ticket
    </h2>

    <form @submit.prevent="handleSubmit" class="space-y-5">
      <!-- Request Type -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Request Type</label>
        <select
          v-model="formData.type"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 
                 focus:outline-none focus:ring-2 focus:ring-purple-700 focus:border-purple-700"
        >
          <option disabled value="">-- Select Request Type --</option>
          <option v-for="type in requestTypes" :key="type" :value="type">{{ type }}</option>
        </select>
      </div>

      <!-- Subject -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Subject</label>
        <input
          v-model="formData.subject"
          type="text"
          placeholder="Enter subject"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 
                 focus:outline-none focus:ring-2 focus:ring-purple-700 focus:border-purple-700"
        />
      </div>

      <!-- Loan ID -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Loan ID</label>
        <input
          v-model="formData.LoanId"
          type="number"
          placeholder="Enter Loan ID if applicable"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 
                 focus:outline-none focus:ring-2 focus:ring-purple-700 focus:border-purple-700"
        />
      </div>

      <!-- Description -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
        <textarea
          v-model="formData.description"
          placeholder="Describe your issue or request"
          rows="4"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 
                 focus:outline-none focus:ring-2 focus:ring-purple-700 focus:border-purple-700"
        ></textarea>
      </div>

      <!-- Submit Button -->
      <div class="text-right">
        <button
          type="submit"
          class="bg-purple-700 text-white px-5 py-2 rounded-lg font-medium 
                 hover:bg-purple-800 transition-colors duration-200 
                 focus:ring-2 focus:ring-yellow-400 shadow-md"
        >
          Submit Ticket
        </button>
      </div>
    </form>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.font-inter {
  font-family: 'Inter', sans-serif;
}
</style>
