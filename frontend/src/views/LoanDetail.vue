<template>
  <div class="p-6 max-w-4xl mx-auto">
    <!-- Back Button -->
    <button 
      @click="goBack" 
      class="mb-6 flex items-center gap-2 text-sm font-medium text-gray-500 hover:text-blue-600 transition back-btn"
    >
      ← Back
    </button>

    <!-- Card -->
    <div class="bg-white shadow-xl rounded-2xl p-8 border border-gray-100">
      <!-- Header -->
      <div class="flex items-center justify-between mb-8">
        <h2 class="flex gap-2 items-center text-2xl font-extrabold text-gray-800 tracking-tight">
          <Search class="w-6 h-6 text-blue-600"/>
          Loan Application #{{ loan.id }}
        </h2>¸
        <span 
          class="px-4 py-1.5 rounded-full text-sm font-semibold shadow-sm"
          :class="{
            'bg-blue-50 text-blue-700 border border-blue-200': loan?.status === 'NEW',
            'bg-yellow-50 text-yellow-700 border border-yellow-200': loan?.status === 'PENDING',
          }"
        >
          {{ loan.status }}
        </span>
      </div>

     <div class="grid grid-cols-2 gap-6 mb-8 text-gray-700">
  <p><span class="font-semibold text-gray-900">Applicant:</span> {{ loan?.name ?? "-" }}</p>
  <p><span class="font-semibold text-gray-900">Amount:</span> ₹{{ loan?.amount?.toLocaleString('en-IN') ?? "-" }}</p>
  <p><span class="font-semibold text-gray-900">Income:</span> ₹{{ loan?.income?.toLocaleString() ?? "-" }}</p>
  <p><span class="font-semibold text-gray-900">Credit Score:</span> {{ loan?.creditScore ?? "-" }}</p>
  <p><span class="font-semibold text-gray-900">Loan Type:</span> {{ loanTypeLabel[loan?.loanType]?.label ?? "N/A" }}</p>
  <p><span class="font-semibold text-gray-900">Applied Date:</span> {{ loan?.applicationDate ?? "-" }}</p>
</div>


      <!-- Remarks -->
      <div class="mb-8">
        <label class="block mb-2 font-semibold text-gray-800">Review Remarks</label>
        <textarea 
          v-model="remarks" 
          rows="4" 
          class="w-full border border-gray-300 rounded-xl p-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 focus:outline-none shadow-sm resize-none"
          placeholder="Write your remarks here..."
        ></textarea>
      </div>

      <!-- Action Buttons -->
      <div class="flex gap-4">
        <button 
          @click="submitReview('APPROVED')" 
          class="flex-1 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white font-semibold px-4 py-3 rounded-xl shadow-md transition-transform transform hover:scale-[1.02]"
        >
          Approve
        </button>
        <button 
          @click="submitReview('REJECTED')" 
          class="flex-1 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white font-semibold px-4 py-3 rounded-xl shadow-md transition-transform transform hover:scale-[1.02]"
        >
           Reject
        </button>
      </div>

      <!-- Response Message -->
      <div 
        v-if="responseMessage" 
        class="mt-8 text-center font-medium text-lg"
        :class="responseMessage.includes('successfully') ? 'text-green-600' : 'text-red-600'"
      >
        {{ responseMessage }}
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted , computed } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import { Search } from "lucide-vue-next";

const store = useStore();
const route = useRoute();
const router = useRouter();

const id = Number(route.params.id);
const loan = computed(() => store.getters.selectedApplication(id));
const remarks = ref("");
const responseMessage = ref("");
const adminUser = store.state.user;
const loanTypeLabel = store.state.interestRate
console.log(loanTypeLabel)

function goBack() {
  router.back();
}

async function submitReview(status) {
  if (!loan.value) return;

  const payload = {
    status,
    reviewedAt: new Date().toISOString(),
    reviewedBy: adminUser?.name,
    reviewRemarks: remarks.value
  };

  try {
    await store.dispatch("updateApplicationStatus", { id: loan.value.id, payload });
    responseMessage.value = `Application ${status.toLowerCase()} successfully.`;
    router.push('/admin');
  } catch (err) {
    console.error(err);
    responseMessage.value = "Failed to update application";
  }
}

</script>
