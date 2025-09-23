import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('auth', () => {
  let currUser = ref(null)
  
  return { currUser }
})
