import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import VueApexCharts from "vue3-apexcharts";
import Toast,{Flip, Slide, Zoom} from "vue3-toastify"
import "vue3-toastify/dist/index.css"

const app = createApp(App)

app.use(router)
app.use(store)
app.use(VueApexCharts); 
app.use(Toast,{transition: Slide,
    transitionDuration: 100,
    autoClose : 3000
})
app.mount('#app')
