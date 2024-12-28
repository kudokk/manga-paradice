package jp.mangaka.ssp.application.service.targeting.time

import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView
import jp.mangaka.ssp.presentation.controller.common.view.StructSelectElementView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.CountriesView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingDetailView
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingListElementView
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TimeTargetingViewServiceImpl(
    private val campaignDao: CampaignDao,
    private val countryMasterDao: CountryMasterDao,
    private val structDao: StructDao,
    private val timeTargetingDao: TimeTargetingDao,
    private val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao,
    private val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper,
    private val countryGetWithCheckHelper: CountryGetWithCheckHelper,
    private val timeTargetingGetWithCheckHelper: TimeTargetingGetWithCheckHelper,
    @Value("\${app.constant.pagination-size.time-targetings}")
    private val timeTargetingsPageSize: Int
) : TimeTargetingViewService {
    override fun getTimeTargetingViews(coAccountId: CoAccountId, pageNo: Int): List<TimeTargetingListElementView> {
        val timeTargetings = timeTargetingDao.selectByCoAccountIdAndStatuses(
            coAccountId,
            TimeTargetingStatus.viewableStatuses,
            timeTargetingsPageSize,
            pageNo * timeTargetingsPageSize
        )

        return TimeTargetingListElementView.of(
            timeTargetings,
            timeTargetingDayTypePeriodDao.selectByIds(timeTargetings.map { it.timeTargetingId })
        )
    }

    override fun getTimeTargetingView(
        coAccountId: CoAccountId,
        timeTargetingId: TimeTargetingId
    ): TimeTargetingDetailView {
        val timeTargeting = timeTargetingGetWithCheckHelper
            .getTimeTargetingWithCheck(coAccountId, timeTargetingId, TimeTargetingStatus.viewableStatuses)
        val timeTargetingDayTypePeriods = timeTargetingDayTypePeriodDao.selectById(timeTargetingId)
        val country = countryGetWithCheckHelper.getCountryWithCheck(timeTargeting.countryId)
        val structs = structDao.selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
        val campaigns = campaignDao
            .selectByIdsAndStatuses(structs.map { it.campaignId }.distinct(), CampaignStatus.entries)

        return TimeTargetingDetailView.of(timeTargeting, timeTargetingDayTypePeriods, country, structs, campaigns)
    }

    override fun getCountriesView(coAccountId: CoAccountId): CountriesView {
        val coAccount = coAccountGetWithCheckHelper.getCoAccountWithCheck(coAccountId)
        val countries = countryMasterDao
            .selectAll()
            .filter { it.isAvailableAtCompass() }

        return CountriesView(
            coAccount.countryId,
            CountrySelectElementView.of(countries)
        )
    }

    override fun getStructViews(
        coAccountId: CoAccountId,
        timeTargetingId: TimeTargetingId?
    ): List<StructSelectElementView> {
        val campaigns = campaignDao.selectByCoAccountIdAndStatuses(coAccountId, CampaignStatus.viewableStatuses)
        // タイムターゲティングに紐づかないストラクトのみ取得
        val structs = structDao
            .selectByCampaignIdsAndStatuses(campaigns.map { it.campaignId }, StructStatus.viewableStatuses)
            // 新規作成時：タイムターゲティングが紐づかないストラクトを取得する
            // 編集時：タイムターゲティングが紐づかないストラクトと編集対象のタイムターゲティングが紐づくストラクトを取得する
            .filter { it.timeTargetingId == null || it.timeTargetingId == timeTargetingId }

        return StructSelectElementView.of(structs, campaigns)
    }
}
