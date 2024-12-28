package jp.mangaka.ssp.presentation.controller.spot.form

data class CloseButtonForm(
    val displayPosition: Int?,
    val displaySize: Int?,
    val lineColor: ColorForm?,
    val backgroundColor: ColorForm?,
    val frameColor: ColorForm?
) {
    data class ColorForm(val red: Int?, val green: Int?, val blue: Int?, val opacity: Double) {
        /**
         * @return rgbaの文字列
         */
        // 永続化時に呼び出される想定なので強制キャストしてnullが設定されることを抑制
        fun rgba(): String = "rgba(${red!!},${green!!},${blue!!},$opacity)"

        companion object {
            private val whiteSpaceRegex = """[\s　]""".toRegex()
            private val rgbaRegex = """.*\((\d+),(\d+),(\d+),(\d+\.\d+)\)""".toRegex()

            /**
             * @param color 色
             * @return 色のForm
             */
            fun of(color: String): ColorForm {
                val groups = rgbaRegex
                    .find(color.replace(whiteSpaceRegex, ""))!!
                    .groups
                    .map { it!!.value }

                return ColorForm(
                    groups[1].toInt(),
                    groups[2].toInt(),
                    groups[3].toInt(),
                    groups[4].toDouble()
                )
            }
        }
    }
}
