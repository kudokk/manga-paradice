package jp.mangaka.ssp.application.valueobject.creative

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class CreativeId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    companion object {
        /**
         * すべてのクリエイティブを表す場合などに使用する値
         */
        val zero = CreativeId(0)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): CreativeId = CreativeId(value)
    }
}
