package jp.mangaka.ssp.application.valueobject.coaccount

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject
import jp.mangaka.ssp.application.valueobject.IdValueObject.Companion.assertNonNegative

data class CoAccountId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    /**
     * @return この CoAccountId のインスタンスが zero のとき true
     */
    fun isZero(): Boolean = this == zero

    companion object {
        /**
         * 全アカウントを表す際などに使用される特別な値
         */
        val zero = CoAccountId(0)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): CoAccountId = CoAccountId(value)
    }
}
