<script setup lang="ts">
defineProps<{ text: string }>()
</script>

<template>
  <span class="onsale-section">
    <span class="onsale">{{ text }}</span>
  </span>
</template>

<style lang="sass" scoped>
.onsale-section
  position: absolute
  top: -6px
  left: 10px

.onsale-section:after
  position: absolute
  content: ''
  display: block
  width: 0
  height: 0
  border-left: 50px solid transparent
  border-right: 50px solid transparent
  border-top: 6px solid #f7edd4

.onsale
  position: relative
  display: inline-block
  text-align: center
  color: #454240
  background: #f7edd4
  font-size: 14px
  line-height: 1
  padding: 12px 8px 6px
  border-top-right-radius: 8px
  text-transform: uppercase
  min-width: 100px

.onsale:before,
.onsale:after
  position: absolute
  content: ''
  display: block

.onsale:before
  background: #f7edd4
  height: 7px
  width: 6px
  left: -6px
  top: 0

.onsale:after
  background: #96a0a2
  height: 7px
  width: 8px
  border-radius: 8px 8px 0 0
  left: -8px
  top: 0
</style>