import { useAxiosPost } from "@/api/axios"
import type { post } from '@/types/api/user'


export namespace UserApi {
  export const postUser = async (form: post.Request) => {
    await useAxiosPost('/api/user', form)
  }
}
