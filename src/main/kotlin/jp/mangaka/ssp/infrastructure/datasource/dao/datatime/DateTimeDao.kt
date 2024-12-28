package jp.mangaka.ssp.infrastructure.datasource.dao.datatime

import java.time.LocalDateTime

interface DateTimeDao {
    /**
     * @return DBの現在時刻
     */
    fun selectCurrentDateTime(): LocalDateTime
}
