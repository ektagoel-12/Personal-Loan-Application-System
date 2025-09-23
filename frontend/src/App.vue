<script setup>
import { useStore } from 'vuex';
const store = useStore()
console.log(store.getters.doubleCount)
</script>

<template>
  <p class="text-red-800">Hello</p>
</template>

<style scoped>

</style>
