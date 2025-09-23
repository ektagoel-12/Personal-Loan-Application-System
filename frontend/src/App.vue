<script setup>
import { useStore } from 'vuex';
const store = useStore()
console.log(store.getters.doubleCount)
</script>

<template>
  <h1>Hi</h1>
</template>

<style scoped>

</style>
