package jp.mangaka.ssp.application.valueobject.aspectratio

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class AspectRatioId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    /**
     * @return ワイプ動画に対応するアスペクト比の場合は true
     */
    fun isWipeVideo(): Boolean = this == aspectRatio16to9

    /**
     * @return フルワイド動画に対応するアスペクト比の場合は true
     */
    fun isFullWideVideo(): Boolean = this == aspectRatio16to5 || this == aspectRatio32to5

    /**
     * @return インライン動画に対応するアスペクト比の場合は true
     */
    fun isInlineVideo(): Boolean = !(isWipeVideo() || isFullWideVideo())

    companion object {
        @JvmStatic
        val aspectRatio16to9 = AspectRatioId(1)

        @JvmStatic
        val aspectRatio16to5 = AspectRatioId(2)

        @JvmStatic
        val aspectRatio32to5 = AspectRatioId(3)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): AspectRatioId = AspectRatioId(value)
    }
}
