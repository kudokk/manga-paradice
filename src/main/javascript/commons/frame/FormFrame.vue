<template>
  <div class="form-frame">
    <slot></slot>
  </div>
</template>

<style lang="sass" scoped>
.form-frame
  background-color: #FFFDFA
  border: 1px solid #E5E0DF
  border-radius: 10px
  padding: 1.125em

  &:deep(hr)
    border: 1px solid #E5E0DF
    margin-top: 1rem
    margin-bottom: 1rem
</style>
