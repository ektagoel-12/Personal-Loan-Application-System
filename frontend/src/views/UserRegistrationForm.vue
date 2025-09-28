<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-secondary to-white p-6 font-sans">
    <div class="w-full max-w-md bg-neutral rounded-2xl shadow-lg border border-secondary/50">
      
      <!-- Header -->
      <div class="p-6 border-b border-secondary/30 text-center">
        <h2 class="text-2xl font-bold text-primary">Create Account</h2>
        <p class="text-sm text-gray-600">Join our loan management platform</p>
      </div>

      <!-- Form -->
      <div class="p-6">
        <form @submit.prevent="handleSubmit" class="space-y-5">
          
          <!-- Name -->
          <div>
            <label for="name" class="block text-sm font-medium text-textdark">Full Name</label>
            <input
              id="name"
              type="text"
              v-model="formData.name"
              placeholder="Enter your full name"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-medium text-textdark">Email</label>
            <input
              id="email"
              type="email"
              v-model="formData.email"
              placeholder="Enter your email"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium text-textdark">Password</label>
            <div class="relative">
              <input
                id="password"
                :type="showPassword ? 'text' : 'password'"
                v-model="formData.password"
                placeholder="Enter your password"
                class="w-full mt-1 px-3 py-2 border rounded-lg pr-10 focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-accent"
                @click="showPassword = !showPassword"
              >
                <component :is="showPassword ? EyeOff : Eye" class="h-4 w-4" />
              </button>
            </div>
          </div>

          <!-- Confirm Password -->
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-textdark">Confirm Password</label>
            <div class="relative">
              <input
                id="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="formData.confirmPassword"
                placeholder="Confirm your password"
                class="w-full mt-1 px-3 py-2 border rounded-lg pr-10 focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-accent"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <component :is="showConfirmPassword ? EyeOff : Eye" class="h-4 w-4" />
              </button>
            </div>
          </div>

          <!-- Income -->
          <div>
            <label for="income" class="block text-sm font-medium text-textdark">Income</label>
            <input
              id="income"
              type="number"
              v-model="formData.income"
              placeholder="Enter your annual income"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Credit Score -->
          <div>
            <label for="creditScore" class="block text-sm font-medium text-textdark">Credit Score</label>
            <input
              id="creditScore"
              type="number"
              v-model="formData.creditScore"
              placeholder="Enter your credit score"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          
          <!-- Aadhar -->
          <div>
            <label for="aadhar" class="block text-sm font-medium text-textdark">Aadhar Number</label>
            <input
              id="aadhar"
              type="text"
              v-model="formData.aadhar"
              placeholder="Enter your Aadhar number"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
            <p v-if="formErrors.aadhar" class="text-red-500 text-sm">{{ formErrors.aadhar }}</p>
          </div>

          <!-- Error alert -->
          <div v-if="error" class="p-3 rounded bg-red-100 text-red-600 text-sm">
            {{ error }}
          </div>

          <!-- Submit -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-primary hover:bg-accent text-white py-2.5 rounded-lg font-medium shadow-md transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="isLoading">Creating Account...</span>
            <span v-else>Create Account</span>
          </button>
        </form>

        <!-- Switch to Login -->
        <div class="mt-6 text-center">
          <button
            type="button"
            class="text-sm text-primary hover:text-accent"
            @click="router.push('/login-form')"
          >
            Already have an account? Sign in
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'
import router from '../router'
import { makeRequestWithoutToken } from '@/utils/requests'
import { useStore } from 'vuex'

const store = useStore()
const toast = useToast()

const formData = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  income: null,
  creditScore: null,
  aadhar: ''
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const isLoading = ref(false)

// Validation functions
const validEmail = (email) => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)
const validName = (name) => name.length >= 2
const validPassword = (password) => {
  return password.length >= 8 &&
         /[A-Z]/.test(password) &&
         /[a-z]/.test(password) &&
         /\d/.test(password) &&
         /[!@#$%^&*(),.?":{}|<>]/.test(password)
}
const validAadhar = (aadhar) => {
    return aadhar.length === 12 && (/^\d+$/.test(aadhar))
}

// Validation with toast notifications
const validateForm = () => {
  if (!validName(formData.name)) {
    toast.error('Name should be at least 2 characters long')
    return false
  }
  
  if (!validEmail(formData.email)) {
    toast.error('Please enter a valid email address')
    return false
  }
  
  if (formData.password !== formData.confirmPassword) {
    toast.error('Passwords do not match')
    return false
  }
  
  if (!validPassword(formData.password)) {
    toast.error('Password must have 8+ characters, 1 uppercase, 1 lowercase, 1 number, and 1 special character')
    return false
  }
  
  if (!formData.income || formData.income <= 0) {
    toast.error('Please enter a valid income amount')
    return false
  }
  
  if (!formData.creditScore || formData.creditScore <= 0) {
    toast.error('Please enter a valid credit score')
    return false
  }
  
  if (!validAadhar(formData.aadhar)) {
    toast.error('Please enter a valid 12-digit Aadhar number')
    return false
  }
  
  return true
}

// Submit handler
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    const response = await makeRequestWithoutToken("POST", "/auth/register", formData)

    if (!response) {
      toast.error('Registration failed. Please try again.')
      return
    }

    // Store tokens and user data
    localStorage.setItem('token', response.data["accessToken"])
    localStorage.setItem('refreshToken', response.data["refreshToken"])
    
    const userData = {
      name: response.data["name"],
      email: response.data["email"],
      role: response.data["role"],
      creditScore: response.data["creditScore"],
      income: response.data["income"],
      aadhar: response.data["aadhar"],
      id: response.data["id"]
    }

    localStorage.setItem('currUser', JSON.stringify(userData))
    store.dispatch("setCurrentUser", userData)

    // Show success toast
    toast.success(`Welcome ${userData.name}! Account created successfully.`)

    // Navigate based on role
    if (response.data["role"] === "ADMIN") {
      router.push("/admin")
    } else {
      router.push("/user-dashboard")
    }

  } catch (error) {
    console.error('Registration error:', error)
    toast.error('Registration failed. Please check your details and try again.')
  } finally {
    isLoading.value = false
  }
}
</script>