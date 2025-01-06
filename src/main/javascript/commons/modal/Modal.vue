<template>
  <div class="modal-overlay">
    <div class="modal-frame">
      <slot></slot>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.modal-overlay
  background-color: rgba(0,0,0,.6)
  display: flex
  align-items: center
  justify-content: center
  position: fixed
  z-index: 10
  top: 0
  height: 100%
  width: 100%

.modal-frame
  background-color: white
  box-shadow:0 0 0 0 rgba(0, 0, 0, 0.0)
  border-radius: 10px
  min-height: 10vh
  min-width: 30vw
</style>
