package jp.mangaka.ssp.presentation.controller

import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.codec.net.URLCodec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

/**
 * レスポンスヘッダーにCSVの情報を設定する.
 *
 * @param csvFileName CSVファイル名
 * @param locale ロケール
 */
fun HttpServletResponse.setCsvHeader(csvFileName: String, locale: Locale) {
    this.characterEncoding = if (locale == Locale.JAPANESE) "SHIFT_JIS" else "UTF-8"
    this.contentType = "text/csv"

    // ファイル名に日本語を入力すると化けるため、RFC6266形式にしている
    this.setHeader(
        "Content-Disposition",
        "attachment;filename*=utf-8''${URLCodec("UTF-8").encode(csvFileName)}"
    )
}

/**
 * CSVファイル名を生成する.
 *
 * @param prefix CSVファイル名のプレフィックス
 * @return CSVファイル名
 */
fun createFilename(prefix: String): String = "${prefix}${LocalDateTime.now().format(dateTimeFormatter)}"
