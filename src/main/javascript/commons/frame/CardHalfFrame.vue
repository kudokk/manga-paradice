<script setup lang="ts">
const props = defineProps<{ header: string, src: string, alt: string }>()

const url = `url(${props.src})`
</script>

<template>
  <div class="card-half-frame">
    <div class="card-half-frame-inner">
      <article class="card-half-frame-article">
        <h2 class="card-half-frame-header">{{ header }}</h2>
        <slot></slot>
      </article>
      <div class="card-half-frame-imageArea">
        <div class="card-half-frame-image"></div>
      </div>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.card-half-frame
  &-inner
    display: flex
    justify-content: center
  &-article
    color: #454240
    padding: 4.75em 3.5em
    width: calc(90vw - 35%)
  &-header
    color: #5C4E3D
    font-size: 2.8rem
    margin-bottom: .5em
  &-imageArea
    display: flex
    align-items: center
    width: 35%
  &-image
    background-image: v-bind(url)
    background-size: cover
    background-position: center
    border-radius: 10px
    height: 80%
    width: 100%
</style>
