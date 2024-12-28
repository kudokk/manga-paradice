package jp.mangaka.ssp.application.valueobject.nativetemplate

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import jp.mangaka.ssp.application.valueobject.IdValueObject

data class NativeElementId(@JsonValue override val value: Int) : IdValueObject<Int>() {
    init {
        assertNonNegative(value)
    }

    /**
     * @return 動画要素の場合は true
     */
    fun isVideo(): Boolean = value == video.value

    companion object {
        /** タイトル */
        val title = NativeElementId(1)

        /** 説明文 */
        val descriptions = NativeElementId(2)

        /** CTAボタン */
        val ctaButton = NativeElementId(3)

        /** 広告主名 */
        val advertiser = NativeElementId(4)

        /** 画像 */
        val image = NativeElementId(5)

        /** ロゴ */
        val logo = NativeElementId(7)

        /** レーティング */
        val rating = NativeElementId(8)

        /** プライス */
        val price = NativeElementId(9)

        /** 広告主ドメイン */
        val domain = NativeElementId(11)

        /** ネイティブ動画要素のID */
        val video = NativeElementId(12)

        @JvmStatic
        @JsonCreator
        fun of(value: Int): NativeElementId = NativeElementId(value)
    }
}
