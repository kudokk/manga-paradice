package jp.mangaka.ssp.application.service.targeting.time

import jakarta.validation.Validator
import jp.mangaka.ssp.application.service.campaign.CampaignGetWithCheckHelper
import jp.mangaka.ssp.application.service.country.CountryGetWithCheckHelper
import jp.mangaka.ssp.application.service.struct.StructGetWithCheckHelper
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingStatusChangeValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.TimeTargetingValidation.TimeTargetingEditValidation
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCheckValue
import jp.mangaka.ssp.presentation.controller.targeting.time.view.TimeTargetingCreateResultView
import jp.mangaka.ssp.util.exception.FormatValidationException
import jp.mangaka.ssp.util.exception.ResourceConflictException
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class TimeTargetingServiceImpl(
    private val validator: Validator,
    private val structDao: StructDao,
    private val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao,
    private val campaignGetWithCheckHelper: CampaignGetWithCheckHelper,
    private val countryGetWithCheckHelper: CountryGetWithCheckHelper,
    private val structGetWithCheckHelper: StructGetWithCheckHelper,
    private val timeTargetingGetWithCheckHelper: TimeTargetingGetWithCheckHelper,
    private val timeTargetingPersistHelper: TimeTargetingPersistHelper
) : TimeTargetingService {
    override fun create(coAccountId: CoAccountId, form: TimeTargetingCreateForm): TimeTargetingCreateResultView {
        // 指定されているIDの妥当性をチェック＆後続処理で必要なエンティティの取得
        val structs = structGetWithCheckHelper.getStructsWithCheck(form.structIds, StructStatus.viewableStatuses)
        val country = countryGetWithCheckHelper.getCountryWithCheck(form.basic.countryId)
        campaignGetWithCheckHelper.getCampaignsWithCheck(
            coAccountId,
            structs.map { it.campaignId }.distinct(),
            CampaignStatus.viewableStatuses
        )

        FormatValidationException.checkErrorResult(
            validator.validate(TimeTargetingCreateValidation.of(form, country, structs))
        )

        val timeTargetingId = timeTargetingPersistHelper.create(coAccountId, form)

        return TimeTargetingCreateResultView(timeTargetingId)
    }

    override fun edit(coAccountId: CoAccountId, timeTargetingId: TimeTargetingId, form: TimeTargetingEditForm) {
        // 指定されているIDの妥当性をチェック＆後続処理で必要なエンティティの取得
        val timeTargeting = timeTargetingGetWithCheckHelper
            .getTimeTargetingWithCheck(coAccountId, timeTargetingId, listOf(TimeTargetingStatus.active))
        val structs = structGetWithCheckHelper.getStructsWithCheck(form.structIds, StructStatus.viewableStatuses)
        val currentStructs = structDao.selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
        val currentDayTypePeriods = timeTargetingDayTypePeriodDao.selectById(timeTargetingId)
        val country = countryGetWithCheckHelper.getCountryWithCheck(form.basic.countryId)

        campaignGetWithCheckHelper.getCampaignsWithCheck(
            coAccountId,
            structs.map { it.campaignId }.distinct(),
            CampaignStatus.viewableStatuses
        )

        checkConflict(form.checkValue, timeTargeting, currentDayTypePeriods)

        FormatValidationException.checkErrorResult(
            validator.validate(TimeTargetingEditValidation.of(form, timeTargeting, country, structs))
        )

        timeTargetingPersistHelper.edit(timeTargetingId, form, currentDayTypePeriods, currentStructs)
    }

    override fun editTimeTargetingStatus(
        coAccountId: CoAccountId,
        timeTargetingId: TimeTargetingId,
        form: TimeTargetingStatusChangeForm
    ) {
        // 指定されているIDの妥当性をチェック＆後続処理で必要なエンティティの取得
        val timeTargeting = timeTargetingGetWithCheckHelper
            .getTimeTargetingWithCheck(coAccountId, timeTargetingId, TimeTargetingStatus.viewableStatuses)
        val currentStructs = structDao.selectByTimeTargetingIdAndStatuses(timeTargetingId, StructStatus.entries)
        val currentDayTypePeriods = timeTargetingDayTypePeriodDao.selectById(timeTargetingId)

        checkConflict(form.checkValue, timeTargeting, currentDayTypePeriods)

        FormatValidationException.checkErrorResult(
            validator.validate(TimeTargetingStatusChangeValidation.of(form, timeTargeting, currentStructs))
        )

        timeTargetingPersistHelper.editTimeTargetingStatus(timeTargetingId, form)
    }

    /**
     * タイムターゲティングの編集のコンフリクトをチェックする.
     *
     * @param checkValue コンフリクト判断用の値
     * @param timeTargeting タイムターゲティングのエンティティ
     * @param timeTargetingDayTypePeriods 時間ターゲティング-日種別区間設定のエンティティのリスト
     */
    @TestOnly
    fun checkConflict(
        checkValue: TimeTargetingCheckValue,
        timeTargeting: TimeTargeting,
        timeTargetingDayTypePeriods: Collection<TimeTargetingDayTypePeriod>
    ) {
        if (checkValue != TimeTargetingCheckValue.of(timeTargeting, timeTargetingDayTypePeriods)) {
            throw ResourceConflictException(
                "ID：${timeTargeting.timeTargetingId}のタイムターゲティングの編集がコンフリクトしました。"
            )
        }
    }
}
