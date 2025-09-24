<script setup>
import { useStore } from 'vuex';
const store = useStore()
console.log(store.getters.doubleCount)
</script>

<template>
  <router-view></router-view>
</template>

<style scoped></style>
