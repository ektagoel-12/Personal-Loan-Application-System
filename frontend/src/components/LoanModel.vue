<script setup>
import { Apple } from 'lucide-vue-next'

const { isOpen, application } = defineProps({
isOpen: Boolean,
application: Object,
statusMap: Object
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
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
  >
    <!-- Modal Container -->
    <div class="bg-white rounded-2xl shadow-xl w-lvw max-w-lg p-6 ">
      <!-- Header -->
      <div class="flex justify-between items-center mb-4 ">
        <h2 class="text-lg font-semibold">
          Application Details - {{ application.id }}
        </h2>
        <span
          class="px-2 py-1 text-xs font-semibold rounded"
          :class="statusMap[application.status].class"
        >
             {{ statusMap[application.status].label }}
        </span>
      </div>

      <p class="text-gray-500 mb-4">
        Detailed information about your loan application
      </p>

      <hr class="m-2 max-w-6xl">
      <!-- Loan Info -->
      <div class="grid grid-cols-2 gap-6 mb-6">
        <div>
          <p class="text-sm text-gray-500">Loan Amount</p>
          <p class="font-medium">₹{{ application.amount.toLocaleString() }}</p>
        </div>
        <div>
          <p class="text-sm text-gray-500">Purpose</p>
          <p class="font-medium">{{ application.purpose }}</p>
        </div>
        <div>
          <p class="text-sm text-gray-500">Applied Date</p>
          <p class="font-medium">{{ application.appliedDate }}</p>
        </div>
        <div>
          <p class="text-sm text-gray-500">Last Updated</p>
          <p class="font-medium">{{ application.lastUpdated }}</p>
        </div>
      </div>

      <hr class=" m-2">

      <!-- Loan Terms (only for approved loans) -->
      <div v-if="application.status === 'APPROVED'" class="grid grid-cols-3 gap-6 mb-6">
        <div>
          <p class="text-sm text-gray-500">Monthly EMI</p>
          <p class="font-medium">₹{{ application.emi }}</p>
        </div>
        <div>
          <p class="text-sm text-gray-500">Interest Rate</p>
          <p class="font-medium">{{ application.interestRate }}% p.a.</p>
        </div>
        <div>
          <p class="text-sm text-gray-500">Tenure</p>
          <p class="font-medium">{{ application.tenure }}</p>
        </div>
      </div>

      <!-- Remarks -->
      <div
        v-if="application.remarks && application.remarkedBy"
        class="bg-gray-100 px-2 py-2 rounded-md text-sm mb-3 w-full"
      >
        <p class="text-gray-800">{{ application.remarks }}</p>
        <p class="text-xs text-gray-500 mt-1 text-right">
          — {{ application.remarkedBy }}
        </p>
      </div>

      <!-- Footer -->
      <div class="flex justify-end gap-3">
        <button
          @click="closeModal"
          class="px-4 py-2 rounded-lg border border-gray-300 hover:bg-gray-100"
        >
          Close
        </button>
        <button
          v-if="application.status === 'APPROVED'"
          class="px-4 py-2 rounded-lg bg-black text-white hover:bg-gray-800"
        >
          Download Documents
        </button>
      </div>
    </div>
  </div>
</template>

