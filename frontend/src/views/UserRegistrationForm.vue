<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
    <div class="w-full max-w-md bg-white rounded-2xl shadow-md">

      <div class="p-6 border-b">
        <h2 class="text-2xl font-semibold text-center">Create Account</h2>
        <p class="text-sm text-gray-500 dark:text-gray-400 text-center">
          Join our loan management platform
        </p>
      </div>

      <div class="p-6">
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- Name -->
          <div>
            <label for="name" class="block text-sm font-medium">Full Name</label>
            <input
              id="name"
              type="text"
              v-model="formData.name"
              placeholder="Enter your full name"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-indigo-300"
            />
            <p v-if="formErrors.name" class="text-red-500 text-sm">{{ formErrors.name }}</p>
          </div>

          <!-- Email -->
          <div>
            <label for="email" class="block text-sm font-medium">Email</label>
            <input
              id="email"
              type="email"
              v-model="formData.email"
              placeholder="Enter your email"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-indigo-300"
            />
            <p v-if="formErrors.email" class="text-red-500 text-sm">{{ formErrors.email }}</p>
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium">Password</label>
            <div class="relative">
              <input
                id="password"
                :type="showPassword ? 'text' : 'password'"
                v-model="formData.password"
                placeholder="Enter your password"
                class="w-full mt-1 px-3 py-2 border rounded-lg pr-10 focus:outline-none focus:ring focus:ring-indigo-300"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                @click="showPassword = !showPassword"
              >
                <span v-if="showPassword"><component :is="EyeOff" class="h-4 w-4" /></span>
                <span v-else><component :is="Eye" class="h-4 w-4" /></span>
              </button>
            </div>

            <p v-if="formErrors.password" class="text-red-500 text-sm">{{ formErrors.password }}</p>
          </div>

          <!-- Confirm Password -->
          <div>
            <label for="confirmPassword" class="block text-sm font-medium">Confirm Password</label>
            <div class="relative">
              <input
                id="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="formData.confirmPassword"
                placeholder="Confirm your password"
                class="w-full mt-1 px-3 py-2 border rounded-lg pr-10 focus:outline-none focus:ring focus:ring-indigo-300"
              />
              <button
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <span v-if="showConfirmPassword"><component :is="EyeOff" class="h-4 w-4" /></span>
                <span v-else><component :is="Eye" class="h-4 w-4" /></span>
              </button>
            </div>
          </div>

          <!-- Income -->
          <div>
            <label for="income" class="block text-sm font-medium">Income</label>
            <input
              id="income"
              type="number"
              v-model="formData.income"
              placeholder="Enter your annual income"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-indigo-300"
            />
            <p v-if="formErrors.income" class="text-red-500 text-sm">{{ formErrors.income }}</p>
          </div>

          <!-- Credit Score -->
          <div>
            <label for="creditScore" class="block text-sm font-medium">Credit Score</label>
            <input
              id="creditScore"
              type="number"
              v-model="formData.creditScore"
              placeholder="Enter your credit score"
              class="w-full mt-1 px-3 py-2 border rounded-lg focus:outline-none focus:ring focus:ring-indigo-300"
            />
            <p v-if="formErrors.creditScore" class="text-red-500 text-sm">{{ formErrors.creditScore }}</p>
          </div>

          <!-- Error alert -->
          <div v-if="error" class="p-3 rounded bg-red-100 text-red-600 text-sm">
            {{ error }}
          </div>

          <!-- Submit -->
          <button
            type="submit"
            class="w-full bg-black hover:bg-indigo-500 text-white py-2 rounded-lg flex items-center justify-center"
          >
            Create Account
          </button>
        </form>

        <div class="mt-6 text-center">
          <button
            type="button"
            class="text-sm text-blue-600 hover:text-blue-500 dark:text-blue-400"
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
import router from '../router'

const formData = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  income: null,
  creditScore: null
})

const formErrors = reactive({
  name: null,
  email: null,
  password: null,
  income: null,
  creditScore: null
})

const error = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// Validation
const validEmail = (email) => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)

const validName = (name) => name.length >= 2

const validPassword = (password) => {
  return password.length >= 8 &&
         /[A-Z]/.test(password) &&
         /[a-z]/.test(password) &&
         /\d/.test(password) &&
         /[!@#$%^&*(),.?":{}|<>]/.test(password)
}


// Submit
const handleSubmit = () => {
  error.value = ''

  if (!validName(formData.name)) {
    formErrors.name = 'Name should be at least 2 letters'
    return
  }
  if (!validEmail(formData.email)) {
    formErrors.email = 'Not a valid email'
    return
  }
  if (formData.password !== formData.confirmPassword) {
    error.value = 'Passwords do not match'
    return
  }
  if (!validPassword(formData.password)) {
    formErrors.password = 'Password must have 8+ chars, 1 upper, 1 lower, 1 number, 1 special'
    return
  }
  if (!formData.income || formData.income <= 0) {
    formErrors.income = 'Please enter valid income'
    return
  }
  if (!formData.creditScore || formData.creditScore <= 0) {
    formErrors.creditScore = 'Please enter valid credit score'
    return
  }

  console.log('Submitted:', { ...formData })
}
</script>
