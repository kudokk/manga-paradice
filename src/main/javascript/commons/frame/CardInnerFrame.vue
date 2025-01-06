<script setup lang="ts">
defineProps<{ header: string }>()
</script>

<template>
  <div class="card-inner-frame">
    <div class="card-inner-frame-inner">
      <h2 class="card-inner-frame-header">{{ header }}</h2>
      <slot></slot>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.card-inner-frame
  display: flex
  align-items: center
  justify-content: center
  height: 100%
  position: relative
  &-inner
    color: #454240
    padding: 4.75em 3.5em
    max-width: 90vw
  &-header
    color: #5C4E3D
    font-size: 2.8rem
    margin-bottom: .5em
</style>