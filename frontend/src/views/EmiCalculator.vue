<template>
  <div class="space-y-6 p-6 font-inter text-[#1f2937] bg-white">
    <!-- Title -->
    <div>
      <h1 class="text-3xl font-bold">EMI Calculator</h1>
      <p class="text-gray-500">
        Calculate your monthly EMI and plan your loan repayment
      </p>
    </div>

    <div class="grid gap-6 lg:grid-cols-3">
      <!-- Input Section -->
      <div class="lg:col-span-1">
        <div class="border border-[#f3e8ff] rounded-lg shadow-sm p-6 space-y-6 bg-white">
          <div>
            <h2 class="text-lg font-semibold flex items-center gap-2 text-[#7e22ce]">
              Loan Parameters
            </h2>
            <p class="text-gray-500">Adjust the values to calculate your EMI</p>
          </div>

          <!-- Loan Amount -->
          <div class="space-y-2">
            <label class="block text-sm font-medium">Loan Amount</label>
            <input
              type="number"
              v-model.number="loanAmount"
              class="w-full border border-[#7e22ce]/30 rounded-md px-3 py-2 text-right focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
            />
            <input
              type="range"
              v-model.number="loanAmount"
              min="50000"
              max="10000000"
              step="50000"
              class="w-full accent-[#7e22ce]"
            />
            <div class="flex justify-between text-xs text-gray-500">
              <span>₹50K</span>
              <span>₹1Cr</span>
            </div>
            <p class="text-sm text-[#7e22ce] font-medium">
              ₹{{ loanAmount.toLocaleString() }}
            </p>
          </div>

          <!-- Interest Rate -->
          <div class="space-y-2">
            <label class="block text-sm font-medium">Interest Rate (% p.a.)</label>
            <input
              type="number"
              step="0.1"
              v-model.number="interestRate"
              class="w-full border border-[#7e22ce]/30 rounded-md px-3 py-2 text-right focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
            />
            <input
              type="range"
              v-model.number="interestRate"
              min="6"
              max="20"
              step="0.1"
              class="w-full accent-[#7e22ce]"
            />
            <div class="flex justify-between text-xs text-gray-500">
              <span>6%</span>
              <span>20%</span>
            </div>
            <p class="text-sm text-[#7e22ce] font-medium">
              {{ interestRate }}% per annum
            </p>
          </div>

          <!-- Tenure -->
          <div class="space-y-2">
            <label class="block text-sm font-medium">Loan Tenure (Years)</label>
            <input
              type="number"
              v-model.number="tenure"
              class="w-full border border-[#7e22ce]/30 rounded-md px-3 py-2 text-right focus:ring-2 focus:ring-[#7e22ce] focus:outline-none"
            />
            <input
              type="range"
              v-model.number="tenure"
              min="1"
              max="30"
              step="1"
              class="w-full accent-[#7e22ce]"
            />
            <div class="flex justify-between text-xs text-gray-500">
              <span>1 Year</span>
              <span>30 Years</span>
            </div>
            <p class="text-sm text-[#7e22ce] font-medium">
              {{ tenure }} years ({{ tenure * 12 }} months)
            </p>
          </div>

          <button
            @click="calculateEMI"
            class="w-full bg-[#7e22ce] text-white py-2 rounded-lg hover:bg-[#6b21a8] transition flex items-center justify-center gap-2"
          >
            Recalculate
          </button>
        </div>
      </div>

      <!-- Result Section -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Result Cards -->
        <div class="grid gap-4 md:grid-cols-2">
          <div class="border border-[#f3e8ff] rounded-lg p-4 bg-white">
            <h3 class="text-base font-semibold text-[#7e22ce]">Monthly EMI</h3>
            <div class="text-2xl mt-4 font-bold text-[#1f2937]">
              ₹{{ emiDetails.emi.toLocaleString() }}
            </div>
            <p class="text-xs text-gray-500">Fixed monthly payment</p>
          </div>

          <div class="border border-[#f3e8ff] rounded-lg p-4 bg-white">
            <h3 class="text-base font-semibold text-[#eab308]">Total Interest</h3>
            <div class="text-2xl mt-4 font-bold text-[#1f2937]">
              ₹{{ emiDetails.totalInterest.toLocaleString() }}
            </div>
            <p class="text-xs text-gray-500">
              {{ interestPercentage }}% of total amount
            </p>
          </div>

          <div class="border border-[#f3e8ff] rounded-lg p-4 bg-white">
            <h3 class="text-base font-semibold text-[#7e22ce]">Total Amount</h3>
            <div class="text-2xl mt-4 font-bold text-[#1f2937]">
              ₹{{ emiDetails.totalAmount.toLocaleString() }}
            </div>
            <p class="text-xs text-gray-500">Principal + Interest</p>
          </div>

          <div class="border border-[#f3e8ff] rounded-lg p-4 bg-white">
            <h3 class="text-base font-semibold text-[#7e22ce]">Total Payments</h3>
            <div class="text-2xl mt-4 font-bold text-[#1f2937]">
              {{ tenure * 12 }}
            </div>
            <p class="text-xs text-gray-500">Monthly payments</p>
          </div>
        </div>

        <!-- Loan Breakdown Pie Chart -->
        <div class="border border-[#f3e8ff] rounded-lg p-6 bg-white flex">
          <!-- Pie Chart -->
          <div class="w-1/2 flex items-center justify-center">
            <v-chart class="h-64 w-full" :option="pieOption" autoresize />
          </div>

          <!-- Right-side Labels -->
          <div class="w-1/2 pl-6 flex flex-col justify-center">
            <!-- Principal -->
            <div class="mb-6">
              <div class="flex items-center gap-2">
                <span class="w-3 h-3 rounded-full bg-[#7e22ce]"></span>
                <span class="font-normal">Principal Amount</span>
              </div>
              <div class="text-lg font-medium mt-1 text-[#1f2937]">
                ₹{{ emiDetails.principalAmount.toLocaleString() }}
              </div>
            </div>

            <!-- Interest -->
            <div>
              <div class="flex items-center gap-2">
                <span class="w-3 h-3 rounded-full bg-[#eab308]"></span>
                <span class="font-normal">Total Interest</span>
              </div>
              <div class="text-lg font-medium mt-1 text-[#1f2937]">
                ₹{{ emiDetails.totalInterest.toLocaleString() }}
              </div>
              <p class="text-xs text-gray-500">
                {{ interestPercentage }}% of total payment
              </p>
            </div>
          </div>
        </div>

        <!-- Loan Balance Line Chart -->
        <div class="border border-[#f3e8ff] rounded-lg p-4 bg-white">
          <h3 class="text-lg font-semibold mb-2 text-[#7e22ce]">
            Loan Balance Over Time
          </h3>
          <v-chart class="h-80" :option="lineOption" autoresize />
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, reactive, computed, watch } from "vue";
import VChart from "vue-echarts";
import * as echarts from "echarts/core";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from "echarts/components";
import { PieChart, LineChart } from "echarts/charts";
import { CanvasRenderer } from "echarts/renderers";

echarts.use([
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  PieChart,
  LineChart,
  CanvasRenderer
]);

const loanAmount = ref(500000);
const interestRate = ref(8.5);
const tenure = ref(15);

const emiDetails = reactive({
  emi: 0,
  totalAmount: 0,
  totalInterest: 0,
  principalAmount: 0,
});

const calculateEMI = () => {
  const P = loanAmount.value;
  const R = interestRate.value / 100 / 12;
  const N = tenure.value * 12;

  if (P && R && N) {
    const emi = (P * R * Math.pow(1 + R, N)) / (Math.pow(1 + R, N) - 1);
    const totalAmount = emi * N;
    const totalInterest = totalAmount - P;

    emiDetails.emi = Math.round(emi);
    emiDetails.totalAmount = Math.round(totalAmount);
    emiDetails.totalInterest = Math.round(totalInterest);
    emiDetails.principalAmount = P;
  }
};

// Watch for recalculation
watch([loanAmount, interestRate, tenure], calculateEMI, { immediate: true });

const interestPercentage = computed(() =>
  emiDetails.totalAmount > 0
    ? Math.round((emiDetails.totalInterest / emiDetails.totalAmount) * 100)
    : 0
);

const pieOption = computed(() => ({
  series: [
    {
      type: "pie",
      radius: "70%",
      center: ["50%", "50%"],
      data: [
        { value: emiDetails.principalAmount, name: "Principal", itemStyle: { color: "#3b82f6" } },
        { value: emiDetails.totalInterest, name: "Interest", itemStyle: { color: "#ef4444" } }
      ],
    //   label: { show: true }, // hide inline labels
      label: {
        show: true,
        position: "outside", // labels outside the chart
        formatter: "{b}: ₹{c}", // Example: Principal: ₹500,000
        fontSize: 12,
        fontWeight: "normal", // makes it NOT bold
        color: "#555" // softer gray instead of pure black
      },
      labelLine: {
        show: true,
        length: 20,
        length2: 10
      }
    }
  ]
}));


// Generate amortization data (yearly)
const amortizationData = computed(() => {
  const data = [];
  let remaining = loanAmount.value;
  const monthlyRate = interestRate.value / 100 / 12;
  const emi = emiDetails.emi;
  const months = tenure.value * 12;

  for (let m = 0; m <= months; m++) {
    if (m % 12 === 0) {
      data.push({
        year: m / 12,
        remaining: Math.round(remaining),
      });
    }
    // simulate repayment
    const interest = remaining * monthlyRate;
    const principalPayment = emi - interest;
    remaining = Math.max(0, remaining - principalPayment);
  }
  return data;
});

// Line Chart (Remaining Balance Over Time)
const lineOption = computed(() => ({
  tooltip: { trigger: "axis" },
  xAxis: {
    type: "category",
    data: amortizationData.value.map((d) => d.year),
    name: "Years"
  },
  yAxis: {
    type: "value",
    name: "Remaining (₹)",
    axisLabel: {
      formatter: (val) => `₹${(val / 100000).toFixed(0)}L`
    }
  },
  series: [
    {
      data: amortizationData.value.map((d) => d.remaining),
      type: "line",
      smooth: true,
      lineStyle: { width: 3, color: "#3b82f6" },
      itemStyle: { color: "#3b82f6" }
    }
  ]
}));
</script>

<style scoped>
.h-72 {
  height: 18rem;
}
.h-80 {
  height: 20rem;
}
</style>
