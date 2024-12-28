package jp.mangaka.ssp.application.service.spot.validation

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jp.mangaka.ssp.application.service.spot.validation.video.VideoSettingValidation
import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly

data class SpotVideoEditValidation(
    @field:Valid
    private val video: VideoSettingValidation?,
    private val hasOtherFormat: Boolean
) {
    @AssertTrue(message = "Validation.Spot.Format.NotEmpty")
    fun isFormats(): Boolean = video != null || hasOtherFormat

    companion object {
        /**
         * ファクトリ関数
         *
         * @param form フォーム
         * @param userType ユーザー種別
         * @param spot 広告枠
         * @param site サイト
         * @param isDisplayControl 表示制御フラグ
         * @param hasOtherFormat ビデオ以外のフォーマットが設定されているか
         * @param existingAspectRatioIds 既存で設定されているアスペクト比のID
         * @return 生成したバリデーションオブジェクト
         */
        fun of(
            form: SpotVideoEditForm,
            userType: UserType,
            spot: Spot,
            site: Site,
            isDisplayControl: Boolean,
            hasOtherFormat: Boolean,
            existingAspectRatioIds: Collection<AspectRatioId>
        ): SpotVideoEditValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkAllowVideo(form, spot)

            return SpotVideoEditValidation(
                form.video?.let {
                    VideoSettingValidation.of(
                        it,
                        userType,
                        site,
                        spot.displayType,
                        isDisplayControl,
                        existingAspectRatioIds
                    )
                },
                hasOtherFormat
            )
        }

        @TestOnly
        fun checkAllowVideo(form: SpotVideoEditForm, spot: Spot) {
            if (form.video == null) return

            if (!spot.isAllowVideo()) {
                throw CompassManagerException("ビデオを設定できない条件が入力されています。")
            }
        }
    }
}
