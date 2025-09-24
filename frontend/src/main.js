import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueApexCharts from "vue3-apexcharts";
import './assets/main.css'

const app = createApp(App)

app.use(router)
app.use(store)

app.use(VueApexCharts); 
app.component("apexchart", VueApexCharts); 

app.mount('#app')
