<template>
  <header class="bg-white border-b border-[#e5e7eb] px-6 py-4 shadow-sm font-sans text-[#1f2937]">
    <div class="flex items-center justify-between">
      <!-- Left side -->
      <div class="flex items-center space-x-4">
        <h1
          class="text-xl font-bold text-[#7e22ce] cursor-pointer hover:text-[#6b21a8] transition"
          @click="router.push(user.role === 'ADMIN' ? '/admin' : '/user-dashboard')"
        >
          LoanFlow
        </h1>
        <span
          v-if="user"
          class="px-2 py-1 rounded text-xs font-medium capitalize"
          :class="user.role === 'admin'
            ? 'bg-[#7e22ce] text-white'
            : 'bg-[#f3e8ff] text-[#7e22ce]'"
        >
          {{ user.name }}
        </span>
      </div>

      <!-- Right side -->
      <div class="flex items-center space-x-4">
        <!-- Notifications -->
        <button class="h-9 w-9 flex items-center justify-center rounded-full hover:bg-[#f3e8ff] relative transition">
          <Bell class="h-4 w-4 text-[#1f2937]" />
          <span class="absolute -top-1 -right-1 h-2 w-2 bg-red-500 rounded-full"></span>
        </button>

        <!-- User Menu -->
        <div v-if="user" class="relative">
          <button
            class="h-9 w-9 flex items-center justify-center rounded-full hover:bg-[#f3e8ff] transition"
            @click="isMenuOpen = !isMenuOpen"
          >
            <div class="h-9 w-9 flex items-center justify-center rounded-full bg-[#f3e8ff] text-[#7e22ce] font-medium">
              {{ getInitials(user.name) }}
            </div>
          </button>

          <!-- Dropdown -->
          <div
            v-if="isMenuOpen"
            class="absolute right-0 mt-2 w-56 bg-white border border-[#e5e7eb] rounded-md shadow-lg z-50"
          >
            <div class="px-4 py-2">
              <p class="text-sm font-semibold text-[#1f2937]">{{ user.name }}</p>
              <p class="text-xs text-gray-500">{{ user.email }}</p>
            </div>
            <div class="border-t border-[#e5e7eb]"></div>
            <ul class="py-1">
              <li>
                <button
                  class="flex items-center px-4 py-2 w-full text-sm text-[#1f2937] hover:bg-[#f3e8ff]"
                  @click="openProfile"
                >
                  <User class="mr-2 h-4 w-4" /> Profile
                </button>
              </li>
              <li>
                <button class="flex items-center px-4 py-2 w-full text-sm text-[#1f2937] hover:bg-[#f3e8ff]">
                  <Settings class="mr-2 h-4 w-4" /> Settings
                </button>
              </li>
              <li>
                <button
                  @click="logout"
                  class="flex items-center px-4 py-2 w-full text-sm text-red-600 hover:bg-[#fef2f2]"
                >
                  <LogOut class="mr-2 h-4 w-4" /> Log out
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Profile Card -->
    <transition name="fade">
      <div
        v-if="showProfile"
        class="fixed top-16 right-6 bg-white border border-[#e5e7eb] rounded-xl shadow-xl w-80 z-50 p-6"
      >
        <button
          class="absolute top-3 right-3 text-gray-400 hover:text-[#1f2937] transition"
          @click="showProfile = false"
        >
          ✕
        </button>

        <div class="flex justify-center mb-4">
          <div class="h-20 w-20 rounded-full bg-[#f3e8ff] flex items-center justify-center text-3xl font-bold text-[#7e22ce]">
            {{ getInitials(user.name) }}
          </div>
        </div>

        <h2 class="text-center text-lg font-semibold text-[#1f2937] mb-4">Profile Details</h2>

        <div class="space-y-2 text-sm text-[#1f2937]">
          <p><span class="font-medium">Name:</span> {{ user.name }}</p>
          <p><span class="font-medium">Email:</span> {{ user.email }}</p>
          <p><span class="font-medium">Role:</span> {{ user.role }}</p>
          <p><span class="font-medium">Credit Score:</span> {{ user.creditScore }}</p>
          <p><span class="font-medium">Income:</span> ₹{{ user.income.toLocaleString() }}</p>
          <p><span class="font-medium">Aadhar:</span> {{ user.aadhar ?? 'Not Provided' }}</p>
        </div>
      </div>
    </transition>
  </header>
</template>


<script setup>
import { ref } from 'vue'
import { Bell, LogOut, Settings, User } from 'lucide-vue-next'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const user = ref(store.getters.currentUser)

const isMenuOpen = ref(false)
const showProfile = ref(false)

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currUser')
  router.push('/login-form')
}

const getInitials = (name) => {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
}

const openProfile = () => {
  isMenuOpen.value = false
  showProfile.value = true
}
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
