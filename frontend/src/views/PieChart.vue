<template>
<div class="chart-container">
    <Pie :data="chartData" :options="chartOptions" />
  </div>
  </template>

<script>
import { Pie } from "vue-chartjs";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from "chart.js";

ChartJS.register(Title, Tooltip, Legend, ArcElement);

export default {
  name: "PieChart",
  components: { Pie },
  props: ["data"],
  computed: {
    chartData() {
      return {
        labels: ["Approved", "Pending", "Rejected"],
        datasets: [
          {
            data: [
              this.data?.approved || 0,
              this.data?.pending || 0,
              this.data?.rejected || 0,
            ],
            backgroundColor: ["#10B981", "#38BDF8", "#F87171"],
          },
        ],
      };
    },
  },
  data() {
    return {
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false,
      },
    };
  },
};
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 300px;  
  width: 100%;
}
</style>