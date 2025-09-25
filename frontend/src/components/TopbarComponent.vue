<template>
  <header class="bg-white border-b border-gray-200 px-6 py-4">
    <div class="flex items-center justify-between">
      <!-- Left side -->
      <div class="flex items-center space-x-4">
        <h1 class="text-xl cursor-pointer" @click="router.push(user.role === 'ADMIN' ? '/admin' : '/user-dashboard')" >LoanFlow</h1>
        <span
          v-if="user"
          class="px-2 py-1 rounded text-xs font-medium"
          :class="user.role === 'admin' ? 'bg-gray-900 text-white' : 'bg-gray-200 text-gray-700'"
        >
          {{user.name}}
        </span>
      </div>

      <!-- Right side -->
      <div class="flex items-center space-x-4">
        <!-- Theme Toggle -->
        <button
          class="h-9 w-9 flex items-center justify-center rounded-full hover:bg-gray-100"
          @click="toggleTheme"
        >
          <Moon v-if="theme === 'light'" class="h-4 w-4" />
          <Sun v-else class="h-4 w-4" />
        </button>

        <!-- Notifications -->
        <button
          class="h-9 w-9 flex items-center justify-center rounded-full hover:bg-gray-100 relative"
        >
          <Bell class="h-4 w-4" />
          <span class="absolute -top-1 -right-1 h-2 w-2 bg-red-500 rounded-full"></span>
        </button>

        <!-- User Menu -->
        <div v-if="user" class="relative">
          <button
            class="h-9 w-9 flex items-center justify-center rounded-full hover:bg-gray-100"
            @click="isMenuOpen = !isMenuOpen"
          >
            <div class="h-9 w-9 flex items-center justify-center rounded-full bg-gray-200">
              <span class="text-sm font-medium">
                {{ getInitials(user.name) }}
              </span>
            </div>
          </button>

          <!-- Dropdown -->
          <div
            v-if="isMenuOpen"
            class="absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-md shadow-lg z-50"
          >
            <div class="px-4 py-2">
              <p class="text-sm font-medium">{{ user.name }}</p>
              <p class="text-xs text-gray-500">{{ user.email }}</p>
            </div>
            <div class="border-t border-gray-200 dark:border-gray-700"></div>
            <ul class="py-1">
              <li>
                <button class="flex items-center px-4 py-2 w-full text-sm hover:bg-gray-100 ">
                  <User class="mr-2 h-4 w-4" /> Profile
                </button>
              </li>
              <li>
                <button class="flex items-center px-4 py-2 w-full text-sm hover:bg-gray-100 ">
                  <Settings class="mr-2 h-4 w-4" /> Settings
                </button>
              </li>
              <li>
                <button
                  @click="logout"
                  class="flex items-center px-4 py-2 w-full text-sm text-red-600 hover:bg-gray-100"
                >
                  <LogOut class="mr-2 h-4 w-4" /> Log out
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { Bell, Moon, Sun, LogOut, Settings, User } from 'lucide-vue-next'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
const store = useStore()
const router = useRouter()
const user = ref(store.getters.currentUser)

const theme = ref('light')
const isMenuOpen = ref(false)

const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  document.documentElement.classList.toggle('dark', theme.value === 'dark')
}

const logout = () => {
  console.log('Logging out...')
  localStorage.removeItem('token');
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('currUser')
  router.push("/login-form")
}

const getInitials = (name) => {
  return name
    .split(' ')
    .map(n => n[0])
    .join('')
    .toUpperCase()
}
</script>
