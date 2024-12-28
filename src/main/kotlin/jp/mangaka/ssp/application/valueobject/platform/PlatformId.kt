package jp.mangaka.ssp.application.valueobject.platform

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class PlatformId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        IdValueObject.assertNonNegative(value)
    }

    /**
     * @return この PlatformId のインスタンスが pc のとき true
     */
    fun isPc(): Boolean = this == pc

    companion object {
        val pc = PlatformId(1)
        val smartPhone = PlatformId(2)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): PlatformId = PlatformId(value)
    }
}
