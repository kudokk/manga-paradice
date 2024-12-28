package jp.mangaka.ssp.application.valueobject.deal

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class DealId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): DealId = DealId(value)
    }
}
