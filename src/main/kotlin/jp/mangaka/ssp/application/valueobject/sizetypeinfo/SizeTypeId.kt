package jp.mangaka.ssp.application.valueobject.sizetypeinfo

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class SizeTypeId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    /**
     * @return ネイティブ要のサイズ種別IDの場合は true
     */
    fun isNative(): Boolean = this == nativePc || this == nativeSp

    companion object {
        /**
         * PCのネイティブ枠のサイズ種別ID
         */
        val nativePc = SizeTypeId(99)

        /**
         * スマートフォンのネイティブ枠のサイズ種別ID
         */
        val nativeSp = SizeTypeId(199)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): SizeTypeId = SizeTypeId(value)
    }
}
