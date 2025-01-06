<script setup lang="ts">
import FormFrame from '@/commons/frame/FormFrame.vue'
import Modal from '@/commons/modal/Modal.vue'
import { useLoginModalStore } from '@/stores/loginModal'

const loginModalStore = useLoginModalStore()
</script>

<template>
  <Modal @click="loginModalStore.closeLoginModal">
    <FormFrame @click.stop="" class="login-frame">
      <h3 class="login-title">ログイン</h3>
      <hr>
      <div class="form-item">
        <h4 class="form-item-label">ユーザー名</h4>
        <div class="form-item-content">
          <input type="text" />
        </div>
      </div>
      <div class="form-item">
        <h4 class="form-item-label">パスワード</h4>
        <div class="form-item-content">
          <input type="password" />
        </div>
      </div>
    </FormFrame>
  </Modal>
</template>

<style lang="sass" scoped>
.login-frame
  font-family: "Libre Baskerville"

.login-title
  color: black
  font-size: 1.4rem

.form-item
  display: flex
  margin-top: .5rem
  &-label
    color: #5C4E3D
    min-width: max(15vw, 150px)
  &-content
    margin-top: 5px
</style>