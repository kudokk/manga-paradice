import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useLoginModalStore = defineStore('loginModal', () => {
  const isOpenLoginModal = ref<boolean>(false)

  const openLoginModal = () => (isOpenLoginModal.value = true)

  const closeLoginModal = () => (isOpenLoginModal.value = false)

  return { isOpenLoginModal, openLoginModal, closeLoginModal }
})
