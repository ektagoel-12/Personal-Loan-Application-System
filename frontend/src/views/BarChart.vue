<template>
<div class="chart-container">
    <Bar :data="chartData" :options="chartOptions" />
  </div></template>

<script>
import { Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

export default {
  name: "BarChart",
  components: { Bar },
  props: ["data"],
  data() {
    return {
      chartData: {
        labels: this.data?.months || [],
        datasets: [
          {
            label: "Applications",
            data: this.data?.values || [],
            backgroundColor: "black",
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
        this.chartData.labels = newVal?.months || [];
        this.chartData.datasets[0].data = newVal?.values || [];
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