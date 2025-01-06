import { useRouter } from 'vue-router'
import { NAMES } from '@/router/name'
import axios, { AxiosInstance, AxiosResponse } from 'axios'

interface Response {
  status: number
  data: {
    errorFields: Object
    message: string
  }
}

interface ErrorResponse {
  response: AxiosResponse
}

class Client {
  private client: AxiosInstance

  constructor() {
    this.client = axios.create({
      withCredencials: true,
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
      }
    })
  }

  private statusExeption = async (
    status: number,
    error?: ErrorResponse,
    onViewError?: (response: Response) => void
  ) => {
    switch (status) {
      case 401:
        // セッション切れの場合、リロード
        window.location.reload()
        break
      case 404:
      case 500: {
        // エラーページに遷移
        const router = useRouter()
        router.push({ name: NAMES.Error })
        break
      }
      default:
        if (error) console.error(error)
        window.alert('異常が発生しました。')
    }
  }

  async get(url: string, params: Object): Promise<Object> {
    const response = await this.client.get(url, { params })
      .catch(async (error: ErrorResponse) => {
        const status = error.response?.status ?? 0
        await this.statusExeption(status)
      })
    if (response != null) return response.data
    return {}
  }
}

