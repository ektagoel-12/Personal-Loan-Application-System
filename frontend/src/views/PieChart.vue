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
  data() {
    return {
      chartData: {
        labels: ["Approved", "Pending", "Rejected"],
        datasets: [
          {
            data: [
              this.data?.approved || 0,
              this.data?.pending || 0,
              this.data?.rejected || 0,
            ],
            backgroundColor: ["#22c55e", "#3b82f6", "#ef4444"],
          },
        ],
      },
      chartOptions: {
        responsive: true,
        maintainAspectRatio: false,
      },
    };
  },
  watch: {
    data: {
      handler(newVal) {
        this.chartData.datasets[0].data = [
          newVal?.approved || 0,
          newVal?.pending || 0,
          newVal?.rejected || 0,
        ];
      },
      deep: true,
    },
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