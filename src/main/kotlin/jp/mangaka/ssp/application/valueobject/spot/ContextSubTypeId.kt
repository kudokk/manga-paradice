package jp.mangaka.ssp.application.valueobject.spot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class ContextSubTypeId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): ContextSubTypeId = ContextSubTypeId(value)
    }
}
