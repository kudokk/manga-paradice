package jp.mangaka.ssp.presentation.controller.spot.view.detail

data class CloseButtonView(
    val displayPosition: Int,
    val displaySize: Int?,
    val lineColor: ColorView?,
    val backgroundColor: ColorView?,
    val frameColor: ColorView?
) {
    data class ColorView(val red: Int, val green: Int, val blue: Int, val opacity: Double) {
        companion object {
            private val whiteSpaceRegex = """[\s　]""".toRegex()
            private val rgbaRegex = """.*\((\d+),(\d+),(\d+),(\d+\.\d+)\)""".toRegex()

            /**
             * @param color 色
             * @return 色のView
             */
            fun of(color: String): ColorView {
                val groups = rgbaRegex
                    .find(color.replace(whiteSpaceRegex, ""))!!
                    .groups
                    .map { it!!.value }

                return ColorView(groups[1].toInt(), groups[2].toInt(), groups[3].toInt(), groups[4].toDouble())
            }
        }
    }

    companion object {
        /**
         * @param displayPosition 表示位置
         * @param displaySize 表示サイズ
         * @param lineColor 本体配色
         * @param backgroundColor 背景配色
         * @param frameColor ボーダー配色
         * @return 閉じるボタンのView
         */
        fun of(
            displayPosition: Int,
            displaySize: Int?,
            lineColor: String?,
            backgroundColor: String?,
            frameColor: String?
        ): CloseButtonView = CloseButtonView(
            displayPosition,
            displaySize,
            lineColor?.let { ColorView.of(it) },
            backgroundColor?.let { ColorView.of(it) },
            frameColor?.let { ColorView.of(it) }
        )
    }
}
