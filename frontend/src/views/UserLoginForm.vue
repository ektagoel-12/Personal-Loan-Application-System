<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
    <div class="w-full max-w-md bg-white rounded-xl shadow-lg overflow-hidden">
      <div class="px-6 py-4 space-y-2 text-center">
        <h2 class="text-2xl font-bold">Welcome Back</h2>
        <p class="text-sm text-gray-500">Sign in to your account</p>
      </div>

      <form @submit.prevent="handleSubmit" class="px-6 py-4 space-y-4">
        <!-- Email Field -->
        <div class="space-y-1">
          <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
          <input
            id="email"
            type="email"
            v-model="formData.email"
            placeholder="Enter your email"
            class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-400"
          />
          <p v-if="errors.email" class="text-red-600 text-xs">{{ errors.email }}</p>
        </div>

        <!-- Password Field -->
        <div class="space-y-1 relative">
          <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="formData.password"
            placeholder="Enter your password"
            class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-400 pr-10"
          />
          <button
            type="button"
            class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
            @click="showPassword = !showPassword"
          >
            <component :is="showPassword ? EyeOff : Eye" class="h-4 w-4" />
          </button>
          <p v-if="errors.password" class="text-red-600 text-xs">{{ errors.password }}</p>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          class="w-full bg-black text-white py-2 px-4 rounded-md hover:bg-indigo-700 transition disabled:opacity-50"
        >
          Sign In
        </button>
      </form>

      <!-- Demo credentials -->
      <div class="px-6 py-4 text-center text-xs text-gray-500 space-y-1">
        <p>Demo credentials:</p>
        <div>User: user@example.com</div>
        <div>Admin: admin@example.com</div>
        <div>Password: password123</div>
      </div>

      <!-- Switch to Register -->
      <div class="px-6 py-4 text-center">
        <button
          type="button"
          @click="router.push('/registration-form')"
          class="text-sm text-blue-600 hover:text-blue-500"
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

const store = useStore()

// Reactive form data
let formData = reactive({
  email: '',
  password: ''
})

// Reactive error messages
let errors = reactive({
  email: null,
  password: null
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
    errors.email = 'Invalid email'
    return
  } else {
    errors.email = null
  }

  if (!validPassword(formData.password)) {
    errors.password = 'Password should be at least 2 characters'
    return
  } else {
    errors.password = null
  }

  const response = await makeRequestWithoutToken("POST","/auth/login",formData);

  if(!response)return;

  localStorage.setItem('token',response.data["accessToken"]);
  localStorage.setItem('refreshToken',response.data["refreshToken"]);

   localStorage.setItem('currUser',JSON.stringify({
      name : response.data["name"],
      email : response.data["email"],
      role : response.data["role"],
      creditScore : response.data["creditScore"],
      income : response.data["income"],
  }))

  store.dispatch("setCurrentUser",{
      name : response.data["name"],
      email : response.data["email"],
      role : response.data["role"],
      creditScore : response.data["creditScore"],
      income : response.data["income"],
  })
  
  if(response.data["role"] == "ADMIN"){
    router.push("/admin")
  }
  else{
    router.push("/user-dashboard")
  }

}
</script>