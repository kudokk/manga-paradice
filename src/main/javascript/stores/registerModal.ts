import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useRegisterModalStore = defineStore('registerModal', () => {
  const isOpenRegisterModal = ref<boolean>(false)

  const openRegisterModal = () => (isOpenRegisterModal.value = true)

  const closeRegisterModal = () => (isOpenRegisterModal.value = false)

  return { isOpenRegisterModal, openRegisterModal, closeRegisterModal }
})
