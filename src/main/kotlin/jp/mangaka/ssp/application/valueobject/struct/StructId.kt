package jp.mangaka.ssp.application.valueobject.struct

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class StructId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        /**
         * すべてのストラクトを表す場合などに使用する値
         */
        val zero = StructId(0)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): StructId = StructId(value)
    }
}
