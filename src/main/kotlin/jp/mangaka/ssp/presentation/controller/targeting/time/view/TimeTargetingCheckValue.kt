package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import java.time.LocalDateTime

data class TimeTargetingCheckValue(
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    val updateTime: LocalDateTime,
    val count: Int
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargeting タイムターゲティングのエンティティ
         * @param timeTargetingDayTypePeriods 時間ターゲティング-日種別区間設定のエンティティのリスト
         * @return 生成した TimeTargetingCheckValue のインスタンス
         */
        fun of(
            timeTargeting: TimeTargeting,
            timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod>
        ): TimeTargetingCheckValue = TimeTargetingCheckValue(
            (timeTargetingDayTypePeriods.map { it.createTime } + timeTargeting.updateTime).max(),
            timeTargetingDayTypePeriods.size
        )
    }
}
