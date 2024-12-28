package jp.mangaka.ssp.util

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    /**
     * @param dateTime 日時
     * @param difference 時差
     * @return 時差を考慮した日時
     */
    fun fixedDateTime(dateTime: LocalDateTime, difference: BigDecimal): LocalDateTime =
        dateTime.plus(difference.multiply((60L * 60L * 1000L).toBigDecimal()).toLong(), ChronoUnit.MILLIS)

    /**
     * @param dateTime 日時
     * @param difference 時差
     * @return 時差を考慮した日付
     */
    fun fixedDate(dateTime: LocalDateTime, difference: BigDecimal): LocalDate =
        fixedDateTime(dateTime, difference).toLocalDate()
}
