<script setup>
import { ref } from "vue"
import { useStore } from "vuex"
import { makeRequestWithToken } from "@/utils/requests";

const store=useStore();

const requestTypes = [
  "Application_Status",
  "Document_Upload_Issue",
  "EMI_Query",
  "Loan_Closure",
  "Others"
]

const formData = ref({
  type: "",
  subject: "",
  description: "",
  userEmail:store.getters.currentUser.email,
  LoanId:null
})

const handleSubmit = async() => {
  if (!formData.value.type || !formData.value.subject || !formData.value.description) {
    alert("⚠️ Please fill out all fields before submitting.")
    return
  }
  const response= await makeRequestWithToken("POST","/ticket/user",formData.value)

  console.log("✅ Ticket submitted:", response.data)

  formData.value = { type: "", subject: "", description: "" }
}
</script>

<template>
  <div class="max-w-2xl w-full mx-auto bg-white shadow-lg rounded-2xl p-6 border border-gray-200">
    <h2 class="text-2xl font-bold mb-6 text-gray-800 text-center">
      Raise a Support Ticket
    </h2>

    <form @submit.prevent="handleSubmit" class="space-y-5">
      <!-- Request Type -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Request Type</label>
        <select
          v-model="formData.type"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          <option disabled value="">-- Select Request Type --</option>
          <option v-for="type in requestTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>
      </div>

      <!-- Subject -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Subject</label>
        <input
          v-model="formData.subject"
          type="text"
          placeholder="Enter subject"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
      </div>

      <!-- Description -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
        <textarea
          v-model="formData.description"
          placeholder="Describe your issue or request"
          rows="4"
          class="w-full border border-gray-300 rounded-lg px-3 py-2 text-gray-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        ></textarea>
      </div>

      <!-- Submit Button -->
      <div class="text-right">
        <button
          type="submit"
          class="bg-black text-white px-5 py-2 rounded-lg font-medium hover:bg-gray-800 transition-colors duration-200"
        >
          Submit Ticket
        </button>
      </div>
    </form>
  </div>
</template>
