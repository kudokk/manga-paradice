package jp.mangaka.ssp.application.service.spot.validation.basic

import jakarta.validation.Valid
import jakarta.validation.constraints.AssertFalse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.ObjectUtils.anyNull
import org.hibernate.validator.constraints.URL
import org.jetbrains.annotations.TestOnly

data class BasicSettingCreateValidation @TestOnly constructor(
    @field:NotBlank(message = "Validation.Input")
    @field:Size(max = 85, message = "Validation.Text.Range")
    private val spotName: String?,
    @field:NotNull(message = "Validation.Input")
    private val siteId: SiteId?,
    @field:NotNull(message = "Validation.Input")
    private val spotStatus: SpotStatus?,
    private val upstreamType: UpstreamType,
    private val currencyId: CurrencyId?,
    @field:NotNull(message = "Validation.Input")
    private val deliveryMethod: DeliveryMethod?,
    @field:NotNull(message = "Validation.Input")
    private val displayType: DisplayType?,
    @field:Valid
    private val spotMaxSize: SpotMaxSizeValidation?,
    @field:Size(max = 85, message = "Validation.Text.Range")
    private val description: String?,
    @field:URL(message = "Validation.Url.Format")
    @field:Size(max = 1024, message = "Validation.Text.Range")
    private val pageUrl: String?
) {
    @AssertFalse(message = "Validation.Input")
    fun getCurrencyId() = !upstreamType.isNone() && currencyId == null

    companion object {
        /**
         * @param form フォーム
         * @param userType ユーザー種別
         * @param site サイト
         * @return 基本設定のバリデーションオブジェクト
         * @throws CompassManagerException 仕様上設定できない内容が入力されているとき
         */
        fun of(form: BasicSettingCreateForm, userType: UserType, site: Site?): BasicSettingCreateValidation {
            // 仕様上あり得ない設定がある場合はパラメーター改ざんのためシステムエラー
            checkMaStaffOnly(form, userType)
            checkSpotStatus(form)
            checkCurrencyId(form)
            checkDeliveryMethod(form, site)
            checkDisplayType(form)
            checkIsDisplayControl(form)
            checkIsAmp(form, site)
            checkSpotMaxSize(form)

            return BasicSettingCreateValidation(
                form.spotName,
                form.siteId,
                form.spotStatus,
                form.upstreamType,
                form.currencyId,
                form.deliveryMethod,
                form.displayType,
                form.spotMaxSize?.let { SpotMaxSizeValidation(it.width, it.height) },
                form.description,
                form.pageUrl
            )
        }

        @TestOnly
        fun checkMaStaffOnly(form: BasicSettingCreateForm, userType: UserType) {
            if (userType.isMaStaff()) return
            if (!form.upstreamType.isNone() || ObjectUtils.anyNotNull(form.currencyId, form.pageUrl)) {
                throw CompassManagerException("マイクロアド社員以外では設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkSpotStatus(form: BasicSettingCreateForm) {
            if (form.spotStatus == null) return
            if (form.spotStatus !in listOf(SpotStatus.active, SpotStatus.standby)) {
                throw CompassManagerException("アクティブ・スタンバイ以外は設定できません。")
            }
        }

        @TestOnly
        fun checkCurrencyId(form: BasicSettingCreateForm) {
            if (form.currencyId == null) return

            if (form.upstreamType.isNone()) {
                throw CompassManagerException("ヘッダービディング通貨を設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkDeliveryMethod(form: BasicSettingCreateForm, site: Site?) {
            if (anyNull(form.deliveryMethod, site?.siteType)) return

            if (!form.upstreamType.isNone() && !form.deliveryMethod!!.isJs()) {
                throw CompassManagerException("広告配信方法にJS以外を設定できない条件が入力されています。")
            }

            if (!site!!.siteType.isApp() && form.deliveryMethod!!.isSdk()) {
                throw CompassManagerException("広告配信方法にSDKを設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkDisplayType(form: BasicSettingCreateForm) {
            if (anyNull(form.deliveryMethod, form.displayType)) return

            if (form.deliveryMethod!!.isSdk() && form.displayType!!.isOverlay()) {
                throw CompassManagerException("表示種別にオーバーレイ表示を設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkIsDisplayControl(form: BasicSettingCreateForm) {
            if (anyNull(form.deliveryMethod, form.displayType)) return

            if (form.isDisplayControl) {
                if (!form.upstreamType.isNone() ||
                    (form.deliveryMethod!!.isJs() && !form.displayType!!.isOverlay()) ||
                    (form.deliveryMethod.isSdk() && !form.displayType!!.isInterstitial())
                ) {
                    throw CompassManagerException("表示制御をオンにできない条件が入力されています。")
                }
            } else {
                if (form.deliveryMethod!!.isSdk() && form.displayType!!.isInterstitial()) {
                    throw CompassManagerException("表示制御をオフにできない条件が入力されています。")
                }
            }
        }

        @TestOnly
        fun checkIsAmp(form: BasicSettingCreateForm, site: Site?) {
            if (!form.isAmp || anyNull(site, form.deliveryMethod)) return

            if (!form.upstreamType.isNone() ||
                site!!.platformId.isPc() ||
                !form.deliveryMethod!!.isJs() ||
                !(form.displayType!!.isInline() || (form.displayType.isOverlay() && !form.isDisplayControl))
            ) {
                throw CompassManagerException("AMP対応を設定できない条件が入力されています。")
            }
        }

        @TestOnly
        fun checkSpotMaxSize(form: BasicSettingCreateForm) {
            if (anyNull(form.spotMaxSize, form.deliveryMethod, form.displayType)) return

            if (!form.upstreamType.isNone() ||
                !form.deliveryMethod!!.isJs() ||
                !(form.displayType!!.isInline() || (form.displayType.isOverlay() && !form.isDisplayControl))
            ) {
                throw CompassManagerException("固定表示（横ｘ縦）を設定できない条件が入力されています。")
            }
        }
    }
}
