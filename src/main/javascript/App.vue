<script setup lang="ts">
import { RouterView } from 'vue-router'
import Header from '@/components/header/Header.vue'
import Login from '@/components/account/Login.vue'
import Register from '@/components/account/Register.vue'
import { useLoginModalStore } from '@/stores/loginModal'
import { useRegisterModalStore } from '@/stores/registerModal'

const loginModalStore = useLoginModalStore()
const registerModalStore = useRegisterModalStore()

</script>

<template>
  <div class="body">
    <Header />
    <RouterView />
    <Login v-if="loginModalStore.isOpenLoginModal" />
    <Register v-if="registerModalStore.isOpenRegisterModal" />
  </div>
</template>

<style lang="sass" scoped>
.body
  background-image: url('@/assets/image/sample_mangaka_back.jpg')
</style>
