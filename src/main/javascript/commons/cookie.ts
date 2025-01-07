/**
 * クッキー情報
 * @property key クッキー名
 * @property value 値
 */
interface CookieValue {
  key: string
  value: string
}

/**
 * クッキー情報すべてを配列で取得する
 * @returns クッキー情報配列
 */
export const getCookies = (): CookieValue[] => {
  const cookies = document.cookie
  const array = cookies.split(';')

  return array.map((cookie) => {
    const splits = cookie.split('=')
    return { key: splits[0], value: splits[1] }
  })
}

/**
 * 指定クッキー名のクッキー値を取得する
 * @param key 指定クッキー名
 * @returns 指定クッキー名のクッキー値
 */
export const getCookie = (key: string) => {
  const cookies = getCookies()
  return cookies.find((cookie) => cookie.key === key)?.value
}
