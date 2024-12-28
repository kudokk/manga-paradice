package jp.mangaka.ssp.presentation.controller.targeting.time.view

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod.DayType
import jp.mangaka.ssp.util.exception.CompassManagerException
import java.time.LocalTime

data class DayTypePeriodDetailView(
    val dayType: DayType,
    @field:JsonFormat(pattern = "HH:mm")
    @field:JsonSerialize(using = LocalTimeSerializer::class)
    val startTime: LocalTime,
    @field:JsonFormat(pattern = "HH:mm")
    @field:JsonSerialize(using = LocalTimeSerializer::class)
    val endTime: LocalTime
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargetingDayTypePeriods 時間ターゲティング-日種別区間設定のエンティティのリスト
         * @return 生成した DayTypePeriodDetailView のインスタンスのリスト
         * @throws CompassManagerException 異なるタイムターゲティングのエンティティが渡されたとき
         */
        fun of(
            timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod>
        ): List<DayTypePeriodDetailView> = timeTargetingDayTypePeriods
            // 念のため異なるタイムターゲティングの入力は弾く.
            .takeIf { timeTargetingDayTypePeriods.distinctBy { it.timeTargetingId }.size <= 1 }
            ?.map { DayTypePeriodDetailView(it.dayType, it.startTime, it.endTime) }
            ?: throw CompassManagerException("日種別区間設定の変換は、単一タイムターゲティングでのみ可能です。")
    }
}
