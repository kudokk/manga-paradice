package jp.mangaka.ssp.presentation.config.secutiry

import org.apache.commons.codec.digest.DigestUtils
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * ユーザパスワードエンコーダ
 */
class UserPasswordEncoder : PasswordEncoder {
    /**
     * 入力パスワードの暗号化を行う.
     *
     * @param rawPassword 入力パスワード
     * @return SHA512で暗号化したパスワード
     */
    override fun encode(rawPassword: CharSequence): String =
        DigestUtils.sha512Hex(rawPassword.toString())

    /**
     * パスワードが一致しているかどうかを判定する.
     *
     * @param rawPassword 入力パスワード
     * @param encodedPassword DBに登録されているパスワード
     * @return パスワードがDBの登録内容と一致している場合は true
     */
    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean =
        if (rawPassword.isEmpty() || encodedPassword.isEmpty()) {
            false
        } else {
            encode(rawPassword) == encodedPassword
        }
}
