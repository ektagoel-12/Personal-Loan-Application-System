<script setup>
import { useStore } from 'vuex';
const store = useStore()
console.log(store.getters.doubleCount)
</script>

<template>

   <div id="app">
    <router-view />
  </div>

</template>

<style scoped>

</style>
