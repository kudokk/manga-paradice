package jp.mangaka.ssp.application.service.targeting.time

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.compassstruct.CompassStructDao
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargetingUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriod
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDao
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodDelete
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargetingdaytypeperiod.TimeTargetingDayTypePeriodInsert
import jp.mangaka.ssp.presentation.controller.targeting.time.form.DayTypePeriodsSettingForm.DayTypePeriodDetailForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingEditForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.TimeTargetingStatusChangeForm
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TimeTargetingPersistHelper(
    private val compassStructDao: CompassStructDao,
    private val timeTargetingDao: TimeTargetingDao,
    private val timeTargetingDayTypePeriodDao: TimeTargetingDayTypePeriodDao
) {
    /**
     * タイムターゲティングを新規登録する.
     *
     * @param coAccountId CoアカウントID
     * @param form 登録内容のフォーム
     * @return 登録したタイムターゲティングのID
     */
    @Transactional("CompassMasterTX")
    fun create(coAccountId: CoAccountId, form: TimeTargetingCreateForm): TimeTargetingId {
        val timeTargetingId = timeTargetingDao.insert(TimeTargetingInsert.of(coAccountId, form))

        timeTargetingDayTypePeriodDao.inserts(
            TimeTargetingDayTypePeriodInsert.of(timeTargetingId, form.dayTypePeriods.dayTypePeriodDetails)
        )

        compassStructDao.updateTimeTargetingId(form.structIds, timeTargetingId)

        return timeTargetingId
    }

    /**
     * タイムターゲティングを更新する
     *
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容のフォーム
     * @param currentDayTypePeriods 現在の時間ターゲティング-日種別区間設定
     * @param currentStructs 現在紐づいているストラクトIDのリスト
     */
    @Transactional("CompassMasterTX")
    fun edit(
        timeTargetingId: TimeTargetingId,
        form: TimeTargetingEditForm,
        currentDayTypePeriods: Collection<TimeTargetingDayTypePeriod>,
        currentStructs: Collection<StructCo>
    ) {
        timeTargetingDao.update(TimeTargetingUpdate.of(timeTargetingId, form))

        editTimeTargetingDayTypePeriods(
            timeTargetingId,
            form.dayTypePeriods.dayTypePeriodDetails,
            currentDayTypePeriods
        )

        editCompassStructs(timeTargetingId, form.structIds, currentStructs)
    }

    /**
     * タイムターゲティングステータスを更新する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param form 更新内容のフォーム
     */
    @Transactional("CompassMasterTX")
    fun editTimeTargetingStatus(timeTargetingId: TimeTargetingId, form: TimeTargetingStatusChangeForm) {
        timeTargetingDao.updateStatus(timeTargetingId, form.timeTargetingStatus)
    }

    /**
     * 時間ターゲティング-日種別区間設定を更新する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param forms 更新内容のフォームのリスト
     * @param currentDayTypePeriods 現在の時間ターゲティング-日種別区間設定
     */
    @TestOnly
    fun editTimeTargetingDayTypePeriods(
        timeTargetingId: TimeTargetingId,
        forms: Collection<DayTypePeriodDetailForm>,
        currentDayTypePeriods: Collection<TimeTargetingDayTypePeriod>
    ) {
        // フォームに含まれていないレコードを削除
        timeTargetingDayTypePeriodDao.deletes(
            currentDayTypePeriods
                .filter { entity -> forms.none { isSame(it, entity) } }
                .let { TimeTargetingDayTypePeriodDelete.of(it) }
        )

        // フォームに新規追加された設定を登録
        timeTargetingDayTypePeriodDao.inserts(
            forms
                .filter { form -> currentDayTypePeriods.none { isSame(form, it) } }
                .let { TimeTargetingDayTypePeriodInsert.of(timeTargetingId, it) }
        )
    }

    /**
     * コンパスストラクトを更新する.
     *
     * @param timeTargetingId タイムターゲティングID
     * @param nextStructIds 更新後に紐づくストラクトIDのリスト
     * @param currentStructs 現在紐づいているストラクトIDのリスト
     */
    @TestOnly
    fun editCompassStructs(
        timeTargetingId: TimeTargetingId,
        nextStructIds: Collection<StructId>,
        currentStructs: Collection<StructCo>
    ) {
        val currentStructIds = currentStructs.map { it.structId }

        // フォームに含まれていないストラクトの紐づけを解除
        compassStructDao.updateTimeTargetingId(currentStructIds - nextStructIds.toSet(), null)
        // フォームに新規追加されたストラクトの紐づけを登録
        compassStructDao.updateTimeTargetingId(nextStructIds - currentStructIds.toSet(), timeTargetingId)
    }

    // 特定のタイムターゲティングでの利用を想定しているので、IDはチェックしない
    private fun isSame(form: DayTypePeriodDetailForm, entity: TimeTargetingDayTypePeriod) =
        form.dayType == entity.dayType && form.startTime == entity.startTime && form.endTime == entity.endTime
}
