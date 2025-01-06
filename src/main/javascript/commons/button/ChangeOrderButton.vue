<script setup lang="ts">
import IconArrowUp from '@/components/icons/IconArrowUp.vue'
import IconArrowDown from '@/components/icons/IconArrowDown.vue'
import { Direction, type DirectionType } from '@/types/direction';

const props = defineProps<{ direction: DirectionType }>()

const arrowComponent = () => {
  if (props.direction === Direction.UP) return IconArrowUp
  return IconArrowDown
}

</script>

<template>
  <button type="button" class="change-order-button">
    <component :is="arrowComponent()" />
  </button>
</template>

<style lang="sass" scoped>
.change-order-button
  background-color: #f7edd4
  border-radius: 0.35em
  border: 1px solid #ddd3ba
  font-size: 1.68rem
  font-family: "Libre Baskerville"
  display: flex
  align-items: center
  justify-content: center
  height: 2.5rem
  width: 2.5rem
</style>