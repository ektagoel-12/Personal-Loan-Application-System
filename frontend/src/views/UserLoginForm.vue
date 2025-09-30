<template>
  <div class="min-h-screen flex items-center justify-center bg-secondary p-4 font-sans">
    <div class="w-full max-w-md bg-neutral rounded-xl shadow-lg overflow-hidden">
      <!-- Header -->
      <div class="px-6 py-4 text-center space-y-2">
        <h2 class="text-2xl font-bold text-primary">Welcome Back</h2>
        <p class="text-sm text-gray-500">Sign in to your account</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="px-6 py-4 space-y-4">
        <!-- Email -->
        <div class="space-y-1">
          <label for="email" class="block text-sm font-medium text-textdark">Email</label>
          <input
            id="email"
            type="email"
            v-model="formData.email"
            placeholder="Enter your email"
            class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <!-- Password -->
        <div class="space-y-1 relative">
          <label for="password" class="block text-sm font-medium text-textdark">Password</label>
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="formData.password"
            placeholder="Enter your password"
            class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-primary pr-10"
          />
          <button
            type="button"
            class="absolute right-3 top-1/2 text-gray-500 hover:text-accent"
            @click="showPassword = !showPassword"
          >
            <component :is="showPassword ? EyeOff : Eye" class="h-4 w-4" />
          </button>
        </div>

        <!-- Submit -->
        <button
          type="submit"
          class="w-full bg-primary text-white py-2 px-4 rounded-md hover:bg-accent transition disabled:opacity-50"
        >
          Sign In
        </button>
      </form>

    
      <!-- Switch to Register -->
      <div class="px-6 py-4 text-center">
        <button
          type="button"
          @click="router.push('/registration-form')"
          class="text-sm text-primary hover:text-accent"
        >
          Don't have an account? Sign up
        </button>
      </div>
    </div>
  </div>
</template>


<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import router from '../router'
import { makeRequestWithoutToken } from '@/utils/requests'
import { useStore } from 'vuex'
import { useToast } from 'vue-toastification'

const store = useStore()
const toast = useToast()

// Reactive form data
let formData = reactive({
  email: '',
  password: ''
})


// Password toggle
const showPassword = ref(false)

// Validation functions
const validEmail = (email) => {
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return emailRegex.test(email)
}
const validPassword = (password) => password.length >= 2

// Handle form submission
const handleSubmit = async () => {
  if (!validEmail(formData.email)) {
    toast.error('Invalid email')
    return
  } 
  if (!validPassword(formData.password)) {
    toast.error('Password should be at least 2 characters')
    return
  } 

  const response = await makeRequestWithoutToken("POST","/auth/login",formData);

  if(!response){
    toast.error('Inavlid username/password')
    return;
  }
  localStorage.setItem('token',response.data["accessToken"]);
  localStorage.setItem('refreshToken',response.data["refreshToken"]);

   localStorage.setItem('currUser',JSON.stringify({
      id : response.data["id"],
      name : response.data["name"],
      email : response.data["email"],
      role : response.data["role"],
      creditScore : response.data["creditScore"],
      income : response.data["income"],
      aadhar:response.data["aadhar"],
      id: response.data["id"]
    }))
    
    store.dispatch("setCurrentUser",{
      name : response.data["name"],
      email : response.data["email"],
      role : response.data["role"],
      creditScore : response.data["creditScore"],
      aadhar:response.data["aadhar"],
      income : response.data["income"],
      id: response.data["id"]
  })
  
  store.dispatch('getAllLoans')
  if(response.data["role"] == "ADMIN"){
    router.push("/admin")
  }
  else{
    router.push("/user-dashboard")
  }

}
</script>