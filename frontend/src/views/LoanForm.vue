<script setup>
import { computed, ref, watch } from "vue";
import { Landmark, ScrollText } from "lucide-vue-next";
import { useStore } from "vuex";
import router from "@/router";


const store = useStore()

// Define a single loan object to hold all the loan application data
const loan = ref({
  userId: store.state.user.id,
  name : store.state.user.name,
  creditScore: store.state.user.creditScore,
  income: 0,
  amount: 0,
  purpose: "",
  status:"PENDING",
  employmentType:"",
  applicationDate: new Date().toISOString().split('T')[0], // Format the current date as 'YYYY-MM-DD'
  rateOfInterest: 0,
  tenure: 0,
});

const userDetails = ref({
    fullName: store.state.user.name,
    email: store.state.user.email,
    phoneNo: "",
    address:""
})

const step = ref(1);

// Interest rates for loan purposes
const interestPerLoan = {
  "HOME_LOAN": { label: "Home Loan", rate: 7.5 },
  "PERSONAL_LOAN": { label: "Personal Loan", rate: 10.0 },
  "CAR_LOAN": { label: "Car Loan", rate: 8.0 },
  "EDUCATION_LOAN": { label: "Education Loan", rate: 6.5 },
  "BUSINESS_LOAN": { label: "Business Loan", rate: 12.5 },
  "GOLD_LOAN": { label: "Gold Loan", rate: 9.0 },
  "CREDIT_CARD_LOAN": { label: "Credit Card Loan", rate: 18.0 },
  "PAYDAY_LOAN": { label: "Payday Loan", rate: 30.0 },
  "HOME_EQUITY_LOAN": { label: "Home Equity Loan", rate: 8.5 },
  "STUDENT_LOAN": { label: "Student Loan", rate: 5.0 }
};


// Watchers and computed properties
const interestRate = computed(() => interestPerLoan[loan.value.purpose]?.rate || 0);

const emi = computed(()=>{
    let P = loan.value.amount;
    let R = interestRate.value / 100 / 12;
    let N = loan.value.tenure * 12; // Get tenure in months

    if (P && R && N) {
      const emiCalculated = (P * R * Math.pow(1 + R, N)) / (Math.pow(1 + R, N) - 1);
      return Math.round(emiCalculated);
    }
    return 0;
})

const eligibilityScore = computed(() => {
  let score = 0;

  if (loan.value.income >= 50000) score += 30;
  else if (loan.value.income >= 30000) score += 20;
  else if (loan.value.income >= 20000) score += 10;

  if (loan.value.creditScore >= 750) score += 35;
  else if (loan.value.creditScore >= 700) score += 25;
  else if (loan.value.creditScore >= 650) score += 15;

  if (loan.value.amount !== 0 && loan.value.amount <= loan.value.income * 5) score += 30;
  else if (loan.value.amount !== 0 && loan.value.amount <= loan.value.income * 8) score += 20;

  return Math.min(score, 100);
});




// Validation function
const validateStep = () => {
  if (step.value === 1) {
    if (!(loan.value.amount > 0)) {
      alert("Please enter a valid loan amount.");
      return false;
    }
    if (!loan.value.tenure) {
      alert("Please select a loan tenure.");
      return false;
    }
    if (!loan.value.purpose) {
      alert("Please select a loan purpose.");
      return false;
    }
    return true;
  } 

  else if (step.value === 2) {
    if (!(loan.value.income > 0)) {
      alert("Please enter a valid monthly income.");
      return false;
    }

    if (isNaN(loan.value.creditScore) || loan.value.creditScore < 300 || loan.value.creditScore > 900) {
      alert("Credit score must be a number between 300 and 900.");
      return false;
    }

    return true;
  } 

  else if (step.value === 3) {
    if (!userDetails.value.fullName || !userDetails.value.fullName.trim()) {
      alert("Please enter your full name.");
      return false;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(userDetails.value.email)) {
      alert("Please enter a valid email address.");
      return false;
    }

    const phonePattern = /^\d{10}$/;
    if (!phonePattern.test(userDetails.value.phoneNo)) {
      alert("Phone number must be exactly 10 digits.");
      return false;
    }

    if (!userDetails.value.address || !userDetails.value.address.trim()) {
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
  store.dispatch("addApplication",loan.value);
  router.push("/loan")
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
            <input v-model="loan.amount" type="number" class="w-full border rounded-lg px-3 py-2" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">Loan Tenure (Years)</label>
            <select v-model="loan.tenure" class="w-full border rounded-lg px-3 py-2">
              <option value="1">1 Year</option>
              <option value="2">2 Years</option>
              <option value="3">3 Years</option>
              <option value="4">4 Years</option>
              <option value="5">5 Years</option>
              <option value="6">6 Years</option>
              <option value="7">7 Years</option>
              <option value="8">8 Years</option>
              <option value="9">9 Years</option>
              <option value="10">10 Years</option>
            </select>
          </div>
        </div>
        <div class="mt-4">
          <label class="block text-sm font-medium mb-1">Loan Purpose</label>
          <select v-model="loan.purpose" class="w-full border rounded-lg px-3 py-2">
            <option 
              v-for="(loanObj, loanType) in interestPerLoan" 
              :key="loanType" 
              :value="loanType">
              {{ loanObj.label }}
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
            <input v-model="loan.income" type="number" class="w-full border rounded-lg px-3 py-2" /> 
          </div> 
          <div> 
            <label class="block text-sm font-medium mb-1">Credit Score</label> 
            <input v-model="loan.creditScore" type="number" class="w-full border rounded-lg px-3 py-2" /> 
          </div>
         </div> 
         <div class="mt-4"> 
          <label class="block text-sm font-medium mb-1">Employment Type</label>
           <select v-model="loan.employmentType" class="w-full border rounded-lg px-3 py-2">
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
                <input v-model="userDetails.fullName" type="text" class="w-full border rounded-lg px-3 py-2" />
               </div> 
               <div> 
                <label class="block text-sm font-medium mb-1">Email</label>
                 <input v-model="userDetails.email" type="email" class="w-full border rounded-lg px-3 py-2" /> 
                </div> 
                <div>
                   <label class="block text-sm font-medium mb-1" >Phone</label>
                    <input v-model="userDetails.phoneNo" type="tel" class="w-full border rounded-lg px-3 py-2" /> 
                  </div> 
                  <div> 
                    <label class="block text-sm font-medium mb-1">Address</label> 
                    <textarea v-model="userDetails.address" rows="3" class="w-full border rounded-lg px-3 py-2"></textarea> 
                  </div> 
                </div> 
              </div> 
              <!-- Step 4: Review & Apply --> 
              <div v-if="step === 4">
                <h3 class="font-semibold mb-4">Review Your Application</h3> 
                <div class="space-y-2 text-gray-700"> 
                  <p><b>Loan Amount:</b> ₹{{ loan.amount }}</p> 
                  <p><b>Tenure:</b> {{ loan.tenure }}</p> 
                  <p><b>Purpose:</b> {{ loan.purpose }}</p> 
                  <p><b>Monthly Income:</b> ₹{{ loan.income }}</p>
                  <p><b>Credit Score:</b> {{ loan.creditScore }}</p> 
                  <p><b>Employment:</b> {{ loan.employmentType }}</p>
                  <p><b>Name:</b> {{ userDetails.fullName }}</p> 
                  <p><b>Email:</b> {{ userDetails.email }}</p>
                  <p><b>Phone:</b> {{ userDetails.phoneNo }}</p>
                  <p><b>Address:</b> {{ userDetails.address }}</p> 
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
        <p>Loan Amount: ₹{{ loan.amount.toLocaleString() }}</p>
        <p>Tenure: {{ loan.tenure }}</p>
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
