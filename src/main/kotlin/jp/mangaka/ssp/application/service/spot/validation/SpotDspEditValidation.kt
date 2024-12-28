package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jp.mangaka.ssp.application.service.spot.validation.dsp.DspValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class SpotDspEditValidation(
    @field:Valid
    private val dsps: List<DspValidation>
) {
    companion object {
        /**
         * @param form 広告枠DSP設定編集のForm
         * @param userType ユーザー種別
         * @return 広告枠DSP設定編集のValidationオブジェクト
         */
        fun of(form: SpotDspEditForm, userType: UserMaster.UserType): SpotDspEditValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(userType)

            return SpotDspEditValidation(
                form.dsps.map { DspValidation(it.dspId, it.bidAdjust, it.floorCpm) }
            )
        }

        @TestOnly
        fun checkMaStaffOnly(userType: UserMaster.UserType) {
            if (!userType.isMaStaff()) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }
    }
}
