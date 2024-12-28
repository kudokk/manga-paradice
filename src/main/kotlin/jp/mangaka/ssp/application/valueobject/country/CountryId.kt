package jp.mangaka.ssp.application.valueobject.country

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject
import jp.mangaka.ssp.application.valueobject.IdValueObject.Companion.assertNonNegative

data class CountryId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        /**
         * 全ての国を表す際に使用される値
         */
        val zero = CountryId(0)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): CountryId = CountryId(value)
    }
}
