<script setup lang="ts">
import FormFrame from '@/commons/frame/FormFrame.vue'
import Modal from '@/commons/modal/Modal.vue'
import { useRegisterModalStore } from '@/stores/registerModal'

const registerModalStore = useRegisterModalStore()
</script>

<template>
  <Modal @click="registerModalStore.closeRegisterModal">
    <FormFrame @click.stop="" class="refister-frame">
      <h3 class="refister-title">新規登録</h3>
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
.refister-frame
  font-family: "Libre Baskerville"

.refister-title
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