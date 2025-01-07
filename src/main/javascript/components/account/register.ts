import { ref } from 'vue'
import { UserApi } from '@/api/user'
import { UserType } from '@/types/userType'

interface FormData {
  userName: string
  mailAddress: string
  password: string
}

export const register = () => {
  const form = ref<FormData>({
    userName: '',
    mailAddress: '',
    password: ''
  })

  const changeUserName = (e: Event) => {
    form.value.userName = (e.target as HTMLInputElement).value
  }

  const changeMailAddress = (e: Event) => {
    form.value.mailAddress = (e.target as HTMLInputElement).value
  }

  const changePassword = (e: Event) => {
    form.value.password = (e.target as HTMLInputElement).value
  }

  const submit = async () => {
    const createForm = {
      secUserName: form.value.userName,
      secMailAddress: form.value.mailAddress,
      secPassword: form.value.password,
      userType: UserType.CLIENT
    }
    await UserApi.postUser(createForm)
  }

  return {
    form,
    change: {
      changeUserName,
      changeMailAddress,
      changePassword,
    },
    submit
  }
}
