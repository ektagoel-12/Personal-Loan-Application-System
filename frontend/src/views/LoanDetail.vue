<template>
  <div class="p-6 max-w-3xl mx-auto">
    <button @click="$router.back()" class="mb-4 text-sm underline">← Back</button>

    <div v-if="loading">Loading...</div>

    <div v-else class="card">
      <h2 class="text-xl font-bold mb-2">Loan Application #{{ loan?.id }}</h2>

      <div class="mb-4">
        <p><strong>Applicant:</strong> {{ loan?.applicant }}</p>
        <p><strong>Amount:</strong> ₹{{ loan?.amount }}</p>
        <p><strong>Income:</strong> ₹{{ loan?.income }}</p>
        <p><strong>Credit Score:</strong> {{ loan?.creditScore }}</p>
        <p><strong>Purpose:</strong> {{ loan?.purpose }}</p>
        <p><strong>Applied Date:</strong> {{ loan?.appliedDate }}</p>
        <p><strong>Status:</strong> {{ loan?.status }}</p>
      </div>

      <div class="mb-4">
        <label class="block mb-1 font-semibold">Review Remarks</label>
        <textarea v-model="remarks" rows="4" class="w-full border rounded p-2"></textarea>
      </div>

      <div class="flex gap-3">
        <button @click="submitReview('APPROVED')" class="bg-green-600 text-white px-4 py-2 rounded">Approve</button>
        <button @click="submitReview('REJECTED')" class="bg-red-600 text-white px-4 py-2 rounded">Reject</button>
      </div>

      <div v-if="responseMessage" class="mt-4 text-sm">
        {{ responseMessage }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";

const store = useStore();
const route = useRoute();
const router = useRouter();

const id = route.params.id;
const loan = ref(null);
const loading = ref(true);
const remarks = ref("");
const responseMessage = ref("");

const adminUser = ref({
  id: 1,
  name: "Admin User",
  email: "admin@example.com"
});

async function loadLoan() {
  loading.value = true;
  try {
    const res = await store.dispatch("fetchLoanById", id);
    loan.value = res;
  } catch (err) {
    console.error(err);
    responseMessage.value = "Failed to load application";
  } finally {
    loading.value = false;
  }
}

function localIsoWithOffset(date = new Date()) {
  const tzOffset = -date.getTimezoneOffset();
  const diff = tzOffset >= 0 ? "+" : "-";
  const pad = (n) => String(Math.floor(Math.abs(n))).padStart(2, "0");
  const hours = pad(tzOffset / 60);
  const minutes = pad(tzOffset % 60);
  const iso = date.toISOString().replace("Z", "");
  return `${iso}${diff}${hours}:${minutes}`;
}

async function submitReview(status) {
  if (!loan.value) return;

  try {
    await store.dispatch("updateApplicationStatus", {
      id: loan.value.id,
      payload: {
        status,
        reviewedAt: localIsoWithOffset(),
        reviewedBy: adminUser.value,
        reviewRemarks: remarks.value
      }
    });

    responseMessage.value = `Application ${status.toLowerCase()} successfully.`;
    router.push({ name: "AdminDashboard" }); 
  } catch (err) {
    console.error(err);
    responseMessage.value = "Failed to update application";
  }
}

onMounted(() => {
  loadLoan();
});
</script>

<style scoped>
.card { @apply bg-white shadow rounded p-4; }
</style>
