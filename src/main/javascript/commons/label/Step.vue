<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{ text: string, width?: number }>()

const tempWidth = computed(() => props.width || 345)
const nextWidth = computed(() => tempWidth.value - 20)
const path = computed(() => `M ${nextWidth.value} 0
        L ${tempWidth.value} 36
        L ${nextWidth.value} 72
        L 0 72
        L 18 36
        L 0 0 Z`)

const widthString = computed(() => tempWidth.value + 'px')
</script>

<template>
  <div class="step">
    <svg class="step-content">
      <path :d=path class="themed-svg-shape-background css-0"></path>
    </svg>
    <div class="step-text">{{ text }}</div>
  </div>
</template>

<style lang="sass" scoped>
.step
  position: relative
  height: 72px
  &-content
    fill: #f7edd4ff
    stroke: #ddd3baff
    overflow: visible
    width: v-bind(widthString)
  &-text
    font-family: "Libre Baskerville"
    font-size: 1.68rem
    display: flex
    align-items: center
    justify-content: center
    position: absolute
    top: 0
    bottom: 0
    width: v-bind(widthString)
</style>
