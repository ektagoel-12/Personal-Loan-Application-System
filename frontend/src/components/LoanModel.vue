<script setup>
const { isOpen, application, statusMap } = defineProps({
  isOpen: Boolean,
  application: Object,
  statusMap: Object,
})

const emit = defineEmits(["close"])

function closeModal() {
  emit("close")
}
</script>

<template>
  <!-- Modal Backdrop -->
  <div
    v-if="isOpen"
    class="fixed inset-0 bg-black bg-opacity-10 flex items-center justify-center z-50 font-sans"
  >
    <!-- Modal Container -->
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6 border border-gray-100">
      <!-- Header -->
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold text-[#1f2937]">
          Application Details - {{ application.id }}
        </h2>
        <span
          class="px-2 py-1 text-xs font-semibold rounded-full"
          :class="statusMap[application.status].class"
        >
          {{ statusMap[application.status].label }}
        </span>
      </div>

      <p class="text-sm text-gray-500 mb-4">
        Detailed information about your loan application
      </p>

      <hr class="my-4 border-gray-200" />

      <!-- Loan Info -->
      <div class="grid grid-cols-2 gap-6 mb-6 text-sm">
        <div>
          <p class="text-gray-500">Loan Amount</p>
          <p class="font-medium text-[#1f2937]">₹{{ application.amount.toLocaleString() }}</p>
        </div>
        <div>
          <p class="text-gray-500">Purpose</p>
          <p class="font-medium text-[#1f2937]">{{ application.purpose }}</p>
        </div>
        <div>
          <p class="text-gray-500">Applied Date</p>
          <p class="font-medium text-[#1f2937]">{{ application.appliedDate }}</p>
        </div>
        <div>
          <p class="text-gray-500">Last Updated</p>
          <p class="font-medium text-[#1f2937]">{{ application.lastUpdated }}</p>
        </div>
      </div>

      <!-- Terms -->
      <div
        v-if="application.status === 'APPROVED'"
        class="grid grid-cols-3 gap-6 mb-6 text-sm"
      >
        <div>
          <p class="text-gray-500">Monthly EMI</p>
          <p class="font-medium text-[#1f2937]">₹{{ application.emi }}</p>
        </div>
        <div>
          <p class="text-gray-500">Interest Rate</p>
          <p class="font-medium text-[#1f2937]">{{ application.interestRate }}% p.a.</p>
        </div>
        <div>
          <p class="text-gray-500">Tenure</p>
          <p class="font-medium text-[#1f2937]">{{ application.tenure/12 }} yrs</p>
        </div>
      </div>

      <!-- Remarks -->
      <div
        v-if="application.remarks && application.remarkedBy"
        class="bg-[#f3e8ff] text-[#1f2937] px-3 py-2 rounded-md text-sm mb-4"
      >
        <p>{{ application.remarks }}</p>
        <p class="text-xs text-right text-gray-500 mt-1">
          — {{ application.remarkedBy }}
        </p>
      </div>

      <!-- Footer -->
      <div class="flex justify-end gap-3 mt-4">
        <button
          @click="closeModal"
          class="px-4 py-2 rounded-lg border border-gray-300 text-[#1f2937] hover:bg-gray-100 transition"
        >
          Close
        </button>
      </div>
    </div>
  </div>
</template>
