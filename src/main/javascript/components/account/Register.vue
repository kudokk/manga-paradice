<script setup lang="ts">
import FormFrame from '@/commons/frame/FormFrame.vue'
import Modal from '@/commons/modal/Modal.vue'
import { register } from '@/components/account/register'
import { useRegisterModalStore } from '@/stores/registerModal'

const registerModalStore = useRegisterModalStore()

const { form, change, submit } = register()
</script>

<template>
  <Modal @click="registerModalStore.closeRegisterModal">
    <FormFrame @click.stop="" class="register-frame">
      <h3 class="register-title">新規登録</h3>
      <hr>
      <div class="form-item">
        <h4 class="form-item-label">ユーザー名</h4>
        <div class="form-item-content">
          <input type="text" @input="change.changeUserName" />
        </div>
      </div>
      <div class="form-item">
        <h4 class="form-item-label">メールアドレス</h4>
        <div class="form-item-content">
          <input type="email" @input="change.changeMailAddress" />
        </div>
      </div>
      <div class="form-item">
        <h4 class="form-item-label">パスワード</h4>
        <div class="form-item-content">
          <input type="password" @input="change.changePassword" />
        </div>
      </div>
      <hr>
      <div class="register-button-wrapper">
        <button type="button" @click="submit">送信</button>
      </div>
    </FormFrame>
  </Modal>
</template>

<style lang="sass" scoped>
.register-frame
  font-family: "Libre Baskerville"

.register-title
  color: black
  font-size: 1.4rem

.register-button-wrapper
  display: flex
  justify-content: flex-end

.form-item
  display: flex
  margin-top: .5rem
  &-label
    color: #5C4E3D
    min-width: max(15vw, 150px)
  &-content
    margin-top: 5px
</style>