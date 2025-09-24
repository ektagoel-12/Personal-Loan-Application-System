<script setup>
import { useStore } from 'vuex';

const store = useStore()
console.log(store.getters.doubleCount)
</script>

<template>
  <RouterView></RouterView>
</template>

<style scoped></style>
