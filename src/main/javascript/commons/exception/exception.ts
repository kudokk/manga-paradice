const errorMessage = '異常が発生しました。捜査し直して下さい。'

/**
 * アラート表示（ユーザー用）後にconsoleにエラー出力（開発用）する
 * @param error consoleに出力するエラー文言
 * @param message アラート表示するエラー文言
 */
export const throwError = (error: string, message = errorMessage) => {
  alert(message)
  throw new Error(error)
}