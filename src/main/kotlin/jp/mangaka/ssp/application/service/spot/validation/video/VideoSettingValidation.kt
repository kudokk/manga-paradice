package jp.mangaka.ssp.application.service.spot.validation.video

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.hibernate.validator.constraints.Range
import org.jetbrains.annotations.TestOnly

data class VideoSettingValidation(
    @field:NotNull(message = "Validation.Input")
    @field:Range(min = 1, max = 100, message = "Validation.Number.Range")
    private val rotationMax: Int?,
    @field:NotEmpty(message = "Validation.VideoDetails.NotEmpty")
    @field:Valid
    val details: List<VideoDetailValidation>
) {
    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @param displayType 表示種別
         * @param isDisplayControl 表示制御フラグ
         * @param existingAspectRatioIds 既存で設定されているアスペクト比のID
         * @return ビデオ設定のバリデーションオブジェクト
         */
        fun of(
            form: VideoSettingForm,
            userType: UserType,
            site: Site?,
            displayType: Spot.DisplayType?,
            isDisplayControl: Boolean,
            existingAspectRatioIds: Collection<AspectRatioId> = emptyList()
        ): VideoSettingValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkNotDuplicate(form)
            checkPrLabelType(form)

            return VideoSettingValidation(
                form.rotationMax,
                form.details.map {
                    VideoDetailValidation.of(
                        it,
                        userType,
                        site,
                        displayType,
                        isDisplayControl,
                        existingAspectRatioIds.contains(it.aspectRatioId)
                    )
                }
            )
        }

        @TestOnly
        fun checkNotDuplicate(form: VideoSettingForm) {
            val aspectRatioIds = form.details.mapNotNull { it.aspectRatioId }

            if (aspectRatioIds.size != aspectRatioIds.distinct().size) {
                throw CompassManagerException("詳細設定に重複するアスペクト比が設定されています。")
            }
        }

        @TestOnly
        fun checkPrLabelType(form: VideoSettingForm) {
            if (form.prLabelType == null) return

            if (form.prLabelType !in 1..6) {
                throw CompassManagerException("PRラベル表示に設定できない値が入力されています。")
            }
        }
    }
}
