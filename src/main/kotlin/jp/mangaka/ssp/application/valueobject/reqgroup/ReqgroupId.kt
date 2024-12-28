package jp.mangaka.ssp.application.valueobject.reqgroup

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject
import jp.mangaka.ssp.application.valueobject.IdValueObject.Companion.assertNonNegative

data class ReqgroupId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: Int): ReqgroupId = ReqgroupId(value)
    }
}
