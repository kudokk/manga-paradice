package jp.mangaka.ssp.application.service.spot.util

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import org.apache.commons.codec.binary.Hex
import org.jetbrains.annotations.TestOnly
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CryptUtils(
    @Value("\${app.constant.cipher.spot.tag.iv}")
    private val spotTagVector: String,
    @Value("\${app.constant.cipher.spot.tag.key}")
    private val spotTagKey: String,
) {
    /**
     * 広告枠IDを暗号化する
     *
     * @param spotId 広告枠ID
     * @return 暗号化された広告枠ID
     */
    fun encryptForTag(spotId: SpotId): String {
        return encrypt(spotId.value.toString(), spotTagVector, spotTagKey)
    }

    /**
     * 初期化ベクトルを利用して暗号化する
     *
     * @param plainText 暗号化対象文字列
     * @param ivStr ベクター
     * @param keyStr キー
     * @return 暗号化文字列
     */
    @TestOnly
    fun encrypt(plainText: String, ivStr: String, keyStr: String): String {
        val cipherKey: SecretKey = SecretKeySpec(keyStr.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(ivStr.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, cipherKey, ivSpec)
        val encryptionBytes = cipher.doFinal(plainText.toByteArray())
        return String(Hex.encodeHex(encryptionBytes))
    }
}
