<script setup>
import { computed, ref, watch } from "vue";
import { Landmark, ScrollText } from "lucide-vue-next";

const step = ref(1);

const loanAmount = ref(0);
const tenure = ref("");
const purpose = ref("");

const monthlyIncome = ref(0);
const employmentType = ref("");
const creditScore = ref(500)

const fullName = ref("");
const email = ref("");
const phone = ref("");
const address = ref("");

const interestPerLoan = {
  "Home Loan": 7.5,
  "Personal Loan": 10.0,
  "Car Loan": 8.0,
  "Education Loan": 6.5,
  "Business Loan": 12.5,
  "Gold Loan": 9.0,
  "Credit Card Loan": 18.0,
  "Payday Loan": 30.0,
  "Home Equity Loan": 8.5,
  "Student Loan": 5.0
}

const interestRate = computed(()=> interestPerLoan[purpose.value])
const emi = ref(0);

const eligibilityScore = computed(() => {
  let score = 0;

  if (monthlyIncome.value >= 50000) score += 30;
  else if (monthlyIncome.value >= 30000) score += 20;
  else if (monthlyIncome.value >= 20000) score += 10;


  if (creditScore.value >= 750) score += 35;
  else if (creditScore.value >= 700) score += 25;
  else if (creditScore.value >= 650) score += 15;


  if (loanAmount.value != 0 && loanAmount.value <= monthlyIncome.value * 5) score += 30;
  else if (loanAmount.value !=0 && loanAmount.value <= monthlyIncome.value * 8) score += 20;

  if (employmentType.value === 'Salaried') score += 20;
  else if (employmentType.value === 'Business') score += 10;

  return Math.min(score, 100);
});


// Calculate EMI
const calculateEMI = () => {
  const P = loanAmount.value;
  const R = interestRate.value / 100 / 12;
  const N = parseInt(tenure.value.split(' ')[0]) * 12; // Get tenure in months

  if (P && R && N) {
    const emiCalculated = (P * R * Math.pow(1 + R, N)) / (Math.pow(1 + R, N) - 1);
    emi.value = Math.round(emiCalculated);
  }
};


// Watch for changes and recalculate EMI
watch([loanAmount, tenure, interestRate], calculateEMI, { immediate: true });


//validation
const validateStep = () => {
  if (step.value === 1) {
    if (!(loanAmount.value > 0)) {
      alert("Please enter a valid loan amount.");
      return false;
    }
    if (!tenure.value) {
      alert("Please select a loan tenure.");
      return false;
    }
    if (!purpose.value) {
      alert("Please select a loan purpose.");
      return false;
    }
    return true;
  } 
  
  else if (step.value === 2) {
    if (!(monthlyIncome.value > 0)) {
      alert("Please enter a valid monthly income.");
      return false;
    }

    if (isNaN(creditScore.value) || creditScore.value < 300 || creditScore.value > 900) {
      alert("Credit score must be a number between 300 and 900.");
      return false;
    }

    if (!employmentType.value) {
      alert("Please select your employment type.");
      return false;
    }

    return true;
  } 
  
  else if (step.value === 3) {
    if (!fullName.value.trim()) {
      alert("Please enter your full name.");
      return false;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email.value)) {
      alert("Please enter a valid email address.");
      return false;
    }

    const phonePattern = /^\d{10}$/;
    if (!phonePattern.test(phone.value)) {
      alert("Phone number must be exactly 10 digits.");
      return false;
    }

    if (!address.value.trim()) {
      alert("Please enter your address.");
      return false;
    }

    return true;
  }

  return true; // step 4 (review) needs no validation
};



function nextStep() {
  if (validateStep()) {
    if (step.value < 4) step.value++;
  } 
}

function prevStep() {
  if (step.value > 1) step.value--;
}

function applyLoan() {
  alert("🎉 Loan Application Submitted Successfully!");
}
</script>


<template>
  <div class="flex flex-col md:flex-row gap-6 p-6">
    <!-- Left Section -->
    <div class="flex-1 bg-white rounded-2xl shadow p-6">
      <h2 class="text-lg font-semibold mb-2">Loan Application</h2>
      <p class="text-gray-500 mb-6">Apply for a personal loan with competitive interest rates</p>

      <!-- Progress -->
      <div class="mb-6">
        <p class="text-sm font-medium mb-1">Step {{ step }} of 4</p>
        <div class="w-full h-2 bg-gray-200 rounded">
          <div
            class="h-2 bg-black rounded"
            :style="{ width: (step / 4) * 100 + '%' }"
          ></div>
        </div>
      </div>

      <!-- Step 1: Loan Details -->
      <div v-if="step === 1">
        <h3 class="font-semibold mb-4">Loan Details</h3>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-1">Loan Amount (₹)</label>
            <input v-model="loanAmount" type="number" class="w-full border rounded-lg px-3 py-2" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Loan Tenure (Years)</label>
            <select v-model="tenure" class="w-full border rounded-lg px-3 py-2">
              <option>1 Year</option>
              <option>2 Years</option>
              <option>3 Years</option>
              <option>4 Years</option>
              <option>5 Years</option>
              <option>6 Years</option>
              <option>7 Years</option>
              <option>8 Years</option>
              <option>9 Years</option>
              <option>10 Years</option>
            </select>
          </div>
        </div>
        <div class="mt-4">
          <label class="block text-sm font-medium mb-1">Loan Purpose</label>
          <select v-model="purpose" class="w-full border rounded-lg px-3 py-2">
             <option v-for="(interest, loanType) in interestPerLoan" :key="loanType" :value="loanType">
              {{ loanType }}
            </option>
          </select>
        </div>
        <div class="mt-4 text-gray-600">
          <span class="inline-flex items-center gap-2">
            <Landmark /> Estimated EMI: ₹{{ emi }} per month
          </span>
        </div>
      </div>

      <!-- Step 2: Personal & Financial Details --> 
       <div v-if="step === 2"> <h3 class="font-semibold mb-4">Personal & Financial Details</h3> 
        <div class="grid grid-cols-2 gap-4"> 
          <div> 
            <label class="block text-sm font-medium mb-1">Monthly Income (₹)</label> 
            <input v-model="monthlyIncome" type="number" class="w-full border rounded-lg px-3 py-2" /> 
          </div> 
          <div> 
            <label class="block text-sm font-medium mb-1">Credit Score</label> 
            <input v-model="creditScore" type="number" class="w-full border rounded-lg px-3 py-2" /> 
          </div>
         </div> 
         <div class="mt-4"> 
          <label class="block text-sm font-medium mb-1">Employment Type</label>
           <select v-model="employmentType" class="w-full border rounded-lg px-3 py-2">
             <option>Salaried</option>
              <option>Business</option> 
              <option>Self-Employed</option> 
            </select> 
          </div> 
        </div> 
        <!-- Step 3: Contact Details -->
          <div v-if="step === 3">
             <h3 class="font-semibold mb-4">Contact Details</h3>
              <div class="space-y-4"> <div> 
                <label class="block text-sm font-medium mb-1">Full Name</label> 
                <input v-model="fullName" type="text" class="w-full border rounded-lg px-3 py-2" />
               </div> 
               <div> 
                <label class="block text-sm font-medium mb-1">Email</label>
                 <input v-model="email" type="email" class="w-full border rounded-lg px-3 py-2" /> 
                </div> 
                <div>
                   <label class="block text-sm font-medium mb-1" >Phone</label>
                    <input v-model="phone" type="tel" class="w-full border rounded-lg px-3 py-2" /> 
                  </div> 
                  <div> 
                    <label class="block text-sm font-medium mb-1">Address</label> 
                    <textarea v-model="address" rows="3" class="w-full border rounded-lg px-3 py-2"></textarea> 
                  </div> 
                </div> 
              </div> 
              <!-- Step 4: Review & Apply --> 
              <div v-if="step === 4">
                <h3 class="font-semibold mb-4">Review Your Application</h3> 
                <div class="space-y-2 text-gray-700"> 
                  <p><b>Loan Amount:</b> ₹{{ loanAmount }}</p> 
                  <p><b>Tenure:</b> {{ tenure }}</p> 
                  <p><b>Purpose:</b> {{ purpose }}</p> 
                  <p><b>Monthly Income:</b> ₹{{ monthlyIncome }}</p>
                  <p><b>Credit Score:</b> {{ creditScore }}</p> 
                  <p><b>Employment:</b> {{ employmentType }}</p>
                  <p><b>Name:</b> {{ fullName }}</p> 
                  <p><b>Email:</b> {{ email }}</p>
                  <p><b>Phone:</b> {{ phone }}</p>
                  <p><b>Address:</b> {{ address }}</p> 
                </div> 
              </div>

      <!-- Navigation -->
      <div class="mt-6 flex justify-between">
        <button
          @click="prevStep"
          :disabled="step === 1"
          class="px-4 py-2 rounded-lg border text-gray-600 disabled:opacity-50"
        >
          Previous
        </button>
        <button
          v-if="step < 4"
          @click="nextStep"
          class="px-6 py-2 rounded-lg bg-black text-white"
        >
          Next
        </button>
        <button
          v-else
          @click="applyLoan"
          class="px-6 py-2 rounded-lg bg-green-600 text-white"
        >
          Apply
        </button>
      </div>
    </div>

    <!-- Right Section -->
    <div class="w-full md:w-1/3 flex flex-col gap-6">
      <!-- Application Summary -->
      <div class="bg-white rounded-2xl shadow p-6">
        <h3 class="font-semibold mb-4 flex gap-1">  <ScrollText/>  Application Summary</h3>
        <p>Loan Amount: ₹{{ loanAmount.toLocaleString() }}</p>
        <p>Tenure: {{ tenure }}</p>
        <p>Estimated EMI: ₹{{ emi }}</p>
        <p>Interest Rate: {{ interestRate }}% p.a.</p>
      </div>

      <!-- Eligibility Score -->
      <div class="bg-white rounded-2xl shadow p-6">
        <p :class="[
            'text-lg',
            'font-bold',
            { 
              'text-red-600': eligibilityScore < 40,
              'text-yellow-500': eligibilityScore >= 40 && eligibilityScore < 70,
              'text-green-600': eligibilityScore >= 70
            }
          ]">
            {{ eligibilityScore }}/100
          </p>

          <div class="w-full h-2 bg-gray-200 rounded mt-2">
            <div
              :class="[
                'h-2', 'rounded',
                { 
                  'bg-red-600': eligibilityScore < 40,
                  'bg-yellow-500': eligibilityScore >= 40 && eligibilityScore < 70,
                  'bg-green-600': eligibilityScore >= 70
                }
              ]"
              :style="{ width: eligibilityScore + '%' }"
            ></div>
          </div>
        <p  v-if="eligibilityScore>90" class="text-gray-500 mt-2 text-sm">Excellent eligibility</p>
      </div>
    </div>
  </div>
</template>
