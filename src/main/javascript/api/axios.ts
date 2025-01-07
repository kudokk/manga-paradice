import axios, { type AxiosInstance } from 'axios'

import { ref, type Ref } from 'vue'
import { useRouter } from 'vue-router'
import { NAMES } from '@/router/name'

const headers = {
  Accept: 'application/json',
  'Content-Type': 'application/json',
}

const client: AxiosInstance = axios.create({
  baseURL: '',
  withCredentials: true,
  headers,
  responseType: 'json',
})

/**
 * axiosでのレスポンス取得時の共通処理
 * 401, 403エラーの場合はログアウト状態にした上で、現行compassのログイン画面に遷移
 * 409, 500エラーの場合はエラーページに遷移
 */
client.interceptors.response.use(
  (response) => response,
  async (error) => {
    switch (error.response?.status) {
      case 401:
      case 403:
        break
      case 409:
      case 500:
        await useRouter().push({
          name: NAMES.Error,
        })
        break
      default:
    }
    return Promise.reject(error)
  }
)

/**
 * axiosでPost通信をするときの共通処理をまとめた関数
 * @param url 通信先のエンドポイント
 * @param form Post処理に必要なパラメータ
 * @returns 通信成功時に受け取ったレスポンスデータ
 */
export const useAxiosPost = async <T = unknown[], U = unknown[], V = unknown[]>(
  url: string,
  form: T
): Promise<{ data: Ref<U | undefined>; error: Ref<V | undefined> }> => {
  const data = ref<U>()
  const error = ref<V>()

  await fetch(`/api/csrfToken`, {
    method: 'GET',
    mode: 'cors',
  }).then(async (res) => {
    await client
      .post(
        url,
        form,
        {
          headers: {
            ...headers,
            'X-CSRF-TOKEN': res.headers.get('X-Csrf-Token')
          }
        }
      )
      .then((response) => {
        data.value = response.data as U
      })
      .catch((e) => {
        if (e.response.status === 400) error.value = e.response.data as V
      })
  })

  return { data, error }
}

export interface UseAxiosGet<T> {
  data: Ref<T>
}

/**
 * axiosでGet通信をするときの共通処理をまとめた関数
 * @param url 通信先のエンドポイント
 * @param params Get処理に必要なパラメータ
 * @returns 通信成功時に受け取ったレスポンスデータ
 */
export const useAxiosGet = async <T = unknown[]>(url: string, params?: Object): Promise<UseAxiosGet<T>> => {
  const data = ref<T>()

  await client
    .get<T>(url, params == null ? undefined : { params })
    .then((res) => {
      data.value = res.data
    })
    .catch(() => {
      console.error('異常が発生しました。')
    })
  return { data: data as Ref<T> }
}
