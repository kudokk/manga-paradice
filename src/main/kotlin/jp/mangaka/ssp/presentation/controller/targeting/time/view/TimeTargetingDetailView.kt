package jp.mangaka.ssp.presentation.controller.targeting.time.view

import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView

data class TimeTargetingDetailView(
    val basic: BasicSettingView,
    val dayTypePeriods: DayTypePeriodsSettingView,
    val structs: List<StructSelectElementView>,
    val checkValue: TimeTargetingCheckValue
) {
    data class BasicSettingView(
        val timeTargetingId: TimeTargetingId,
        val timeTargetingName: String,
        val timeTargetingStatus: TimeTargetingStatus,
        val description: String?,
        val country: CountrySelectElementView
    )

    data class DayTypePeriodsSettingView(
        val isActiveHoliday: Boolean,
        val dayTypePeriodDetails: List<DayTypePeriodDetailView>
    )

    companion object {
        /**
         * ファクトリ関数
         *
         * @param timeTargeting タイムターゲティングのエンティティ
         * @param timeTargetingDayTypePeriods 時間ターゲティング-日種別区間設定のエンティティのリスト
         * @param country 国のエンティティ
         * @param structs ストラクトのエンティティのリスト
         * @param campaigns キャンペーンのエンティティ
         * @return 生成した TimeTargetingDetailView のインスタンス
         */
        fun of(
            timeTargeting: TimeTargeting,
            timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod>,
            country: CountryMaster,
            structs: Collection<StructCo>,
            campaigns: Collection<CampaignCo>
        ): TimeTargetingDetailView = TimeTargetingDetailView(
            BasicSettingView(
                timeTargeting.timeTargetingId,
                timeTargeting.timeTargetingName,
                timeTargeting.timeTargetingStatus,
                timeTargeting.description,
                CountrySelectElementView.of(country)
            ),
            DayTypePeriodsSettingView(
                timeTargeting.isActiveHoliday,
                DayTypePeriodDetailView.of(timeTargetingDayTypePeriods)
            ),
            StructSelectElementView.of(structs, campaigns),
            TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods)
        )
    }
}
