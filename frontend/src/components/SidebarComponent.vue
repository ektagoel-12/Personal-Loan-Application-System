<template>
  <aside class="h-screen w-64 bg-white border-r border-gray-200 flex flex-col">
    <!-- Header -->
    <div class="px-6 py-4 flex items-center gap-2 border-b border-gray-200">
      <CreditCard class="h-5 w-5" />
      <span class="font-semibold">LoanFlow</span>
    </div>

    <!-- Menu Items -->
    <nav class="flex-1 overflow-y-auto">
      <ul class="p-2 space-y-1">
        <li v-for="item in menuItems" :key="item.key">
          <button
            @click="onSectionChange(item.key)"
            class="flex items-center w-full px-3 py-2 rounded-lg text-sm font-medium hover:bg-gray-100"
            :class="activeSection === item.key ? 'bg-gray-200 dark:bg-gray-700' : ''"
          >
            <component :is="item.icon" class="h-4 w-4 mr-2" />
            <span>{{ item.title }}</span>
          </button>
        </li>
      </ul>
    </nav>

    <!-- Footer -->
    <div class="p-2 border-t border-gray-200 dark:border-gray-700">
      <button
        @click="onSectionChange('settings')"
        class="flex items-center w-full px-3 py-2 rounded-lg text-sm font-medium hover:bg-gray-100"
        :class="activeSection === 'settings' ? 'bg-gray-200 dark:bg-gray-700' : ''"
      >
        <Settings class="h-4 w-4 mr-2" />
        <span>Settings</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  LayoutDashboard,
  FileText,
  Calculator,
  MessageSquare,
  Settings,
  Users,
  TrendingUp,
  CreditCard
} from 'lucide-vue-next'

// Dummy user (replace with Pinia or provide/inject later)
const user = ref({
  name: 'John Doe',
  role: 'admin'
})

const activeSection = ref('dashboard')
const onSectionChange = (section) => {
  activeSection.value = section
  console.log('Changed section to:', section)
}

const customerItems = [
  { title: 'Dashboard', icon: LayoutDashboard, key: 'dashboard' },
  { title: 'My Loans', icon: FileText, key: 'loans' },
  { title: 'EMI Calculator', icon: Calculator, key: 'calculator' },
  { title: 'Support', icon: MessageSquare, key: 'support' }
]

const adminItems = [
  { title: 'Admin Dashboard', icon: TrendingUp, key: 'admin-dashboard' },
  { title: 'Loan Applications', icon: FileText, key: 'admin-loans' },
  { title: 'User Management', icon: Users, key: 'users' },
  { title: 'EMI Calculator', icon: Calculator, key: 'calculator' },
  { title: 'Support Tickets', icon: MessageSquare, key: 'admin-support' }
]

const menuItems = computed(() => (user.value.role === 'admin' ? adminItems : customerItems))
</script>
