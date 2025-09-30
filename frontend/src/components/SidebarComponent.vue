<template>
<aside class="h-screen w-64 bg-white border-r border-gray-200 flex flex-col shadow-sm">
  <!-- Header -->
  <div class="px-6 py-4 flex items-center gap-2 border-b border-gray-200">
    <AArrowUp  class="h-5 w-5 text-purple-600" />
    <span class="font-semibold text-lg text-gray-900 hover:text-purple-600 transition cursor-pointer"
          @click="router.push('/applyLoan')">
      Apply Loan
    </span>
  </div>

  <!-- Menu Items -->
  <nav class="flex-1 overflow-y-auto">
    <ul class="p-2 space-y-1">
      <li v-for="item in menuItems" :key="item.key">
        <button
          @click="onSectionChange(item.key)"
          class="flex items-center w-full px-3 py-2 rounded-lg text-sm font-medium transition-colors"
          :class="[
            activeSection === item.key
              ? 'bg-purple-100 text-purple-700'
              : 'text-gray-700 hover:bg-gray-100',
          ]"
        >
          <component :is="item.icon" class="h-4 w-4 mr-2"
                     :class="activeSection === item.key ? 'text-purple-600' : 'text-gray-500'" />
          <span>{{ item.title }}</span>
        </button>
      </li>
    </ul>
  </nav>

  <!-- Footer -->
  <div class="p-2 border-t border-gray-200">
    <button
      @click="onSectionChange('settings')"
      class="flex items-center w-full px-3 py-2 rounded-lg text-sm font-medium transition-colors"
      :class="activeSection === 'settings'
        ? 'bg-purple-100 text-purple-700'
        : 'text-gray-700 hover:bg-gray-100'"
    >
      <Settings class="h-4 w-4 mr-2"
                :class="activeSection === 'settings' ? 'text-purple-600' : 'text-gray-500'" />
      <span>Settings</span>
    </button>
  </div>
</aside>

</template>

<script setup>
import { ref, computed } from 'vue'
import {
  FileText,
  Calculator,
  MessageSquare,
  Settings,
  Users,
  TrendingUp,
  AArrowUp
} from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const store = useStore()


const user = ref(store.getters.currentUser);

const router = useRouter();

const activeSection = ref('dashboard')
const onSectionChange = (section) => {
  activeSection.value = section
  router.push(section)
}

const customerItems = [
  { title: 'My Loans', icon: FileText, key: 'loan' },
  { title: 'EMI Calculator', icon: Calculator, key: 'calculator' },
  { title: 'Repayment Schedule', icon: Users, key: 'repayment' },
  { title: 'Your Tickets', icon: MessageSquare, key: 'user-ticket-view' }
]

const adminItems = [
  { title: 'Admin Dashboard', icon: TrendingUp, key: 'admin' },
  { title: 'Loan Applications', icon: FileText, key: 'loan' },
  { title: 'User Management', icon: Users, key: 'users' },
  { title: 'EMI Calculator', icon: Calculator, key: 'calculator' },
  { title: 'Support Tickets', icon: MessageSquare, key: 'admin-ticket-view' }
]

const menuItems = computed(() => (user.value.role === 'ADMIN' ? adminItems : customerItems))
</script>
