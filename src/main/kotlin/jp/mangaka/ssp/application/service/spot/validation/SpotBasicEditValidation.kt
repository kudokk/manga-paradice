package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jp.mangaka.ssp.application.service.spot.validation.basic.BasicSettingEditValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm

data class SpotBasicEditValidation(
    @field:Valid
    private val basic: BasicSettingEditValidation
) {
    companion object {
        /**
         * @param form 広告枠基本設定編集のForm
         * @param spot 広告枠
         * @param userType ユーザー種別
         * @param isDisplayControl 表示制御有無
         * @param sizeTypes サイズ種別のリスト
         * @return 広告枠基本設定のバリデーションオブジェクト
         */
        fun of(
            form: SpotBasicEditForm,
            spot: Spot,
            userType: UserMaster.UserType,
            isDisplayControl: Boolean,
            sizeTypes: Collection<SizeTypeInfo>
        ): SpotBasicEditValidation = SpotBasicEditValidation(
            BasicSettingEditValidation.of(
                form.basic,
                spot,
                userType,
                isDisplayControl,
                sizeTypes
            )
        )
    }
}
