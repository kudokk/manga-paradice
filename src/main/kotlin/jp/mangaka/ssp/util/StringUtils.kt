package jp.mangaka.ssp.util

import org.apache.commons.text.StringEscapeUtils

object StringUtils {
    /**
     * @param html HTML文字列
     * @return エスケープしたHTML文字列
     */
    fun escapeHtml(html: String): String = StringEscapeUtils
        .escapeHtml4(html)
        .replace("'".toRegex(), "&#39;")
}
