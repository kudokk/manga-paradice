<script setup lang="ts">
import { ref } from 'vue'
import { NAMES } from '@/router/name'
import { useClickOutside } from '@/commons/event/useClickOutside'
import IconAccount from '@/components/icons/IconAccount.vue'
import { useRegisterModalStore } from '@/stores/registerModal'
import { useLoginModalStore } from '@/stores/loginModal'

const account = ref<HTMLElement>()
const isOpen = ref<boolean>(false)
const open = () => isOpen.value = true
const close = () => isOpen.value = false
useClickOutside(account, () => close())

const registerModalStore = useRegisterModalStore()
const loginModalStore = useLoginModalStore()
</script>
<template>
  <div class="account" ref="account">
    <!-- アイコン -->
    <button type="button" @click="open">
      <IconAccount class="account-icon" />
    </button>
    <div v-if="isOpen" class="account-dialog">
      <ul class="account-dialog-list">
        <li class="account-dialog-item account-dialog-item-has-border">
          <IconAccount class="account-dialog-icon" /><span>ユーザー名</span>
        </li>
        <li class="account-dialog-item account-dialog-item-has-border">
          <button type="button"><router-link :to="{ name: NAMES.About }">このサイトについて</router-link></button>
        </li>
        <li class="account-dialog-item">
          <button type="button">閲覧履歴</button>
        </li>
        <li class="account-dialog-item account-dialog-item-has-border">
          <button type="button"><router-link :to="{ name: NAMES.Manga.Create }">漫画投稿</router-link></button>
        </li>
        <li class="account-dialog-item">
          <button type="button" @click="registerModalStore.openRegisterModal">新規ユーザー登録</button>
        </li>
        <li class="account-dialog-item">
          <button type="button" @click="loginModalStore.openLoginModal">ログイン</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<style lang="sass" scoped>
.account
  display: flex
  position: relative

.account-icon
  width: 2.25rem

.account-dialog
  background-color: #f7edd4
  border-radius: 5px
  border: 1px solid #ddd3ba
  color: #454240
  font-size: .875rem
  position: absolute
  top: 2.5rem
  right: 0
  padding: 1rem
  z-index: 1
  &-list
    padding-left: 0
  &-item
    list-style-type: none
    display: flex
    align-items: center
    white-space: nowrap
    &-has-border
      border-bottom: 1px solid #ddd3ba
      padding-bottom: 5px
      & + .account-dialog-item
        padding-top: 5px
  &-icon
    width: 2.5rem
  & button, a
    background-color: transparent
    border: none
    color: #454240
    cursor: pointer
    font-size: .875rem
    width: 100%
    &:hover
      background-color: rgba(0,0,0,.1)
</style>