import { onMounted, onUnmounted } from 'vue'

/**
 * 指定要素にイベントリスナーを登録・解除
 * @param element 要素
 * @param event イベント名
 * @param callback イベント後の実行関数
 */
export const addEventListener = (
  element: HTMLElement | Window,
  event: keyof HTMLElementEventMap,
  callback: (e: Event) => void
) => {
  onMounted(() => element.addEventListener(event, callback))
  onUnmounted(() => element.removeEventListener(event, callback))
}
