import type { Ref } from "vue"
import { addEventListener } from "@/commons/event/addEventListener"

/**
 * 指定要素外クリック時に指定関数を実行
 * @param element 要素
 * @param clickOutsideFunc 領域外クリック時の実行関数
 */
export const useClickOutside = (
  element: Ref<HTMLElement | undefined>,
  clickOutsideFunc: Function
) => {
  const func = (e: Event) => {
    if (e.target != null && !element.value?.contains(e.target as Node)) {
      clickOutsideFunc()
    }
  }
  addEventListener(window, 'click', func)
}