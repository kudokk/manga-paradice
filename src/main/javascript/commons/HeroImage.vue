<script setup lang="ts">
const props = defineProps<{
  src: string
  backColor: string
}>()
const backImage = `url(${props.src})`
</script>
<template>
  <div class="hero-image">
    <div class="hero-image-inner"></div>
    <div class="hero-image-cover"></div>
    <slot></slot>
  </div>
</template>

<style lang="sass" scoped>
.hero-image
  position: relative
  &-inner
    position: absolute
    left: calc(50% - var(--doc-padding-left, 0px) / 2 + var(--doc-padding-right, 0px) / 2)
    top: 0
    height: 100%
    width: 100vw
    overflow: hidden
    border-radius: 0.35em
    background-image: v-bind(backImage)
    background-position: center
    background-size: cover
    background-repeat: no-repeat
    transform: translateX(-50%)
  &-cover
    position: absolute
    background-color: v-bind(backColor)
    opacity: .8
    height: 100%
    width: 100vw
    left: calc(50% - var(--doc-padding-left, 0px) / 2 + var(--doc-padding-right, 0px) / 2)
    transform: translateX(-50%)
</style>
