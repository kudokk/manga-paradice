<script setup lang="ts">
defineProps<{ text: string }>()
const emits = defineEmits<{ (e: 'click'): void }>()
</script>

<template>
  <button type="button" @click="emits('click')">{{ text }}</button>
</template>

<style lang="sass" scoped>
button
  background-color: #B88E23
  border-radius: .375em .35em
  height: 40px
  width: 130px
  font-family: "DM Sans"
  font-size: 1em
  font-weight: bold
  border: none
</style>