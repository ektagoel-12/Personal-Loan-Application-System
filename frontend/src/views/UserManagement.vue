<template>
    <div class="p-4 space-y-6">
        <!-- Filters Section -->
        <div class="flex space-x-4 bg-gray-100 p-4 rounded-lg shadow-md">
            <!-- Name Filter -->
            <div class="flex flex-col w-1/3">
                <label for="nameFilter" class="font-semibold text-sm text-gray-700">Filter by Name</label>
                <input 
                    id="nameFilter" 
                    v-model="nameFilter" 
                    type="text" 
                    class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="Search by name" 
                />
            </div>

            <!-- Email Filter -->
            <div class="flex flex-col w-1/3">
                <label for="emailFilter" class="font-semibold text-sm text-gray-700">Filter by Email</label>
                <input 
                    id="emailFilter" 
                    v-model="emailFilter" 
                    type="text" 
                    class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                    placeholder="Search by email" 
                />
            </div>

            <!-- Role Filter -->
            <div class="flex flex-col w-1/3">
                <label for="roleFilter" class="font-semibold text-sm text-gray-700">Filter by Role</label>
                <select 
                    id="roleFilter" 
                    v-model="roleFilter" 
                    class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                >
                    <option value="">All Roles</option>
                    <option value="ADMIN">Admin</option>
                    <option value="USER">User</option>
                </select>
            </div>
        </div>

        <!-- Table Section -->
        <div class="overflow-x-auto">
            <table class="min-w-full table-auto border-collapse border border-gray-300 shadow-md rounded-lg">
                <thead>
                    <tr class="bg-gray-100 text-left text-sm font-semibold text-gray-700">
                        <th class="px-4 py-2 border-b">User ID</th>
                        <th class="px-4 py-2 border-b">Name</th>
                        <th class="px-4 py-2 border-b">Email</th>
                        <th class="px-4 py-2 border-b">Income</th>
                        <th class="px-4 py-2 border-b">Credit Score</th>
                        <th class="px-4 py-2 border-b">Aadhar</th>
                        <th class="px-4 py-2 border-b">Role</th>
                        <th class="px-4 py-2 border-b">Make Admin</th>
                        <th class="px-4 py-2 border-b">Remove Admin</th>
                    </tr>
                </thead>
                <tbody>
                    <tr 
                        v-for="user in filteredUsers" 
                        :key="user.id" 
                        class="border-t hover:bg-gray-50 transition-all duration-200"
                    >
                        <td class="px-4 py-2 text-sm text-gray-700">{{ user.id }}</td>
                        <td class="px-4 py-2 text-sm text-gray-700">{{ user.name }}</td>
                        <td class="px-4 py-2 text-sm text-gray-700">{{ user.email }}</td>
                        <td class="px-4 py-2 text-sm text-gray-700">{{ user.income | currency }}</td>
                        <td class="px-4 py-2 text-sm">
                            <span :class="['px-2 py-1 rounded text-white', user.creditScore >= 700 ? 'bg-green-600' : 'bg-red-500']">
                                {{ user.creditScore }}
                            </span>
                        </td>
                        <td class="px-4 py-2 text-sm text-gray-700">{{ user.aadhar }}</td>
                        <td class="px-4 py-2 text-sm text-gray-700 capitalize">{{ user.role }}</td>
                        <td class="px-4 py-2 text-center">
                            <button @click="changeToAdmin(user.id)" 
                                    class="bg-green-500 text-white p-2 rounded-lg hover:bg-green-600 transition-all duration-200">
                                Add Admin
                            </button>
                        </td>
                        <td class="px-4 py-2 text-center">
                            <button @click="changeToUser(user.id)" 
                                    class="bg-red-500 text-white p-2 rounded-lg hover:bg-red-600 transition-all duration-200">
                                Remove Admin
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script setup>
import { makeRequestWithToken } from "@/utils/requests";
import { ref, onMounted, computed } from "vue";

let users = ref([]);
let nameFilter = ref("");
let emailFilter = ref("");
let roleFilter = ref("");

// Filters applied to users
const filteredUsers = computed(() => {
    return users.value.filter(user => {
        const matchesName = user.name.toLowerCase().includes(nameFilter.value.toLowerCase());
        const matchesEmail = user.email.toLowerCase().includes(emailFilter.value.toLowerCase());
        const matchesRole = roleFilter.value ? user.role === roleFilter.value : true;
        return matchesName && matchesEmail && matchesRole;
    });
});

const changeToAdmin = async (userId) => {
    const response = await makeRequestWithToken('PATCH',`/users/${userId}`,{role:'ADMIN'});
    if (!response) {
        alert("Updating the user failed");
        return;
    }
    let response1 = await makeRequestWithToken('GET', '/users', null);
    if (!response1) {
        alert("Failed loading users");
        return;
    }
    users.value = response1.data;
};

const changeToUser = async (userId) => {
    const response = await makeRequestWithToken('PATCH', `/users/${userId}`, { role: 'USER' });
    if (!response) {
        alert("Updating the user failed");
        return;
    }
    let response1 = await makeRequestWithToken('GET', '/users', null);
    if (!response1) {
        alert("Failed loading users");
        return;
    }
    users.value = response1.data;
};

// lifecycle
onMounted(async () => {
    let response = await makeRequestWithToken('GET', '/users', null);
    if (!response) {
        alert("Failed loading users");
        return;
    }
    users.value = response.data;
});
</script>

<style scoped>
/* Optional: Additional styling */
input, select {
    transition: background-color 0.3s ease;
}

input:focus, select:focus {
    background-color: #eff6ff; /* Light blue background on focus */
}
</style>
