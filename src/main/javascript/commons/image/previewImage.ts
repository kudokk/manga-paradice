/**
 * 指定img要素に指定画像ファイルをdateURLIで表示する
 * @param element img要素
 * @param file 画像ファイル
 */
export const previewImage = (element: HTMLImageElement, file: File) => {
  const reader = new FileReader()
  reader.onload = function () {
    element.src = this.result as string
  }
  reader.readAsDataURL(file)
}