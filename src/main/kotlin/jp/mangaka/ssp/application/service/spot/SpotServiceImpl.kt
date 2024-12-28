package jp.mangaka.ssp.application.service.spot

import jakarta.validation.Validator
import jp.mangaka.ssp.application.service.spot.helper.SizeTypeInfoPersistHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import jp.mangaka.ssp.application.service.spot.helper.SpotPersistHelper
import jp.mangaka.ssp.application.service.spot.util.SpotUtils
import jp.mangaka.ssp.application.service.spot.validation.SpotBannerEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotBasicEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotCreateValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotDspEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotNativeEditValidation
import jp.mangaka.ssp.application.service.spot.validation.SpotVideoEditValidation
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm
import jp.mangaka.ssp.presentation.controller.spot.form.CloseButtonForm.ColorForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeStandardForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm.NativeVideoForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import jp.mangaka.ssp.presentation.controller.spot.view.SpotCreateResultView
import jp.mangaka.ssp.util.exception.FormatValidationException
import jp.mangaka.ssp.util.exception.ResourceConflictException
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SpotServiceImpl(
    private val validator: Validator,
    private val relaySpotSizetypeDao: RelaySpotSizetypeDao,
    private val spotBannerDao: SpotBannerDao,
    private val spotBannerDisplayDao: SpotBannerDisplayDao,
    private val spotNativeDao: SpotNativeDao,
    private val spotNativeDisplayDao: SpotNativeDisplayDao,
    private val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao,
    private val spotVideoDao: SpotVideoDao,
    private val spotVideoDisplayDao: SpotVideoDisplayDao,
    private val spotVideoFloorCpmDao: SpotVideoFloorCpmDao,
    private val sizeTypeInfoPersistHelper: SizeTypeInfoPersistHelper,
    private val spotGetWithCheckHelper: SpotGetWithCheckHelper,
    private val spotPersistHelper: SpotPersistHelper,
    private val spotSizeTypeDeleteRuleHelper: SpotSizeTypeDeleteRuleHelper
) : SpotService {
    override fun create(coAccountId: CoAccountId, userType: UserType, form: SpotCreateForm): SpotCreateResultView {
        // 画面で指定されたIDの有効性をチェックし取得
        val site = getSite(coAccountId, form.basic)
        val aspectRatios = getAspectRatios(form.video)
        checkBasic(form.basic)
        checkDsp(form.dsps)
        checkBanner(coAccountId, form.banner)
        checkNative(coAccountId, form.native, site)

        FormatValidationException.checkErrorResult(
            validator.validate(SpotCreateValidation.of(form, userType, site))
        )

        // サイズ種別情報と広告枠は登録先のDBが異なるのでトランザクションも分かれている
        val sizeTypeIds = sizeTypeInfoPersistHelper.bulkCreate(coAccountId, site!!.platformId, form)

        val spotId = spotPersistHelper.create(coAccountId, form, site, sizeTypeIds, userType, aspectRatios)
        return SpotCreateResultView(spotId)
    }

    @TestOnly
    fun getSite(coAccountId: CoAccountId, form: BasicSettingCreateForm): Site? = form.siteId?.let {
        spotGetWithCheckHelper.getSiteWithCheck(
            coAccountId,
            it,
            listOf(SiteStatus.active, SiteStatus.requested, SiteStatus.ng)
        )
    }

    @TestOnly
    fun getAspectRatios(form: VideoSettingForm?): List<AspectRatio> = form?.details?.let { details ->
        spotGetWithCheckHelper.getAspectRatiosWithCheck(
            details.mapNotNull { it.aspectRatioId },
            listOf(AspectRatioStatus.active)
        )
    } ?: emptyList()

    @TestOnly
    fun checkBasic(form: BasicSettingCreateForm) {
        form.currencyId?.let { spotGetWithCheckHelper.getCurrencyWithCheck(it) }
    }

    @TestOnly
    fun checkDsp(forms: Collection<DspForm>) {
        if (forms.isEmpty()) return

        forms.mapNotNull { it.dspId }.let { spotGetWithCheckHelper.getDspsWithCheck(it) }
    }

    @TestOnly
    fun checkBanner(coAccountId: CoAccountId, form: BannerSettingForm?) {
        if (form == null) return

        form.decorationId?.let { spotGetWithCheckHelper.getDecorationWithCheck(coAccountId, it) }
    }

    @TestOnly
    fun checkNative(coAccountId: CoAccountId, form: NativeSettingForm?, site: Site?) {
        if (form == null || site?.platformId == null) return

        val statuses = listOf(NativeTemplate.NativeTemplateStatus.active)

        form.standard?.nativeTemplateId?.let {
            spotGetWithCheckHelper.getNativeStandardTemplateWithCheck(coAccountId, it, statuses, site.platformId)
        }
        form.video?.nativeTemplateId?.let {
            spotGetWithCheckHelper.getNativeVideoTemplateWithCheck(it, statuses)
        }
    }

    override fun editBasic(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotBasicEditForm) {
        val existingSpot = getExistingSpot(coAccountId, spotId, userType)
        val relaySpotSizetypes = relaySpotSizetypeDao.selectBySpotId(existingSpot.spot.spotId)
        val sizeTypeInfos = spotGetWithCheckHelper.getSizeTypeInfosWithCheck(relaySpotSizetypes.map { it.sizeTypeId })
        val correctedForm = correctSpotBasicEditForm(form, existingSpot, userType)

        checkConflict(existingSpot.spot, correctedForm.updateTime)

        FormatValidationException.checkErrorResult(
            SpotBasicEditValidation.of(
                correctedForm,
                existingSpot.spot,
                userType,
                existingSpot.isDisplayControl(),
                sizeTypeInfos
            ).let { validator.validate(it) }
        )

        spotPersistHelper.editBasic(spotId, correctedForm.basic)
    }

    /**
     * 社員以外のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param existingSpot 既存の広告枠情報
     * @param userType ユーザー種別
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctSpotBasicEditForm(
        form: SpotBasicEditForm,
        existingSpot: ExistingSpot,
        userType: UserType
    ): SpotBasicEditForm {
        // 社員の場合は上書きしない
        if (userType.isMaStaff()) return form

        val newBasic = form.basic.copy(pageUrl = existingSpot.spot.pageUrl)
        return form.copy(basic = newBasic)
    }

    override fun editDsp(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotDspEditForm) {
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.editableStatuses, userType)
        // 利用はしないが存在をチェック
        spotGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)

        checkConflict(spot, form.updateTime)
        checkDsp(form.dsps)

        FormatValidationException.checkErrorResult(
            validator.validate(SpotDspEditValidation.of(form, userType))
        )

        spotPersistHelper.editDsps(coAccountId, spotId, form.dsps)
    }

    override fun editBanner(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotBannerEditForm) {
        val existingSpot = getExistingSpot(coAccountId, spotId, userType)
        val relaySpotSizetypes = relaySpotSizetypeDao.selectBySpotId(spotId)
        val currentSizeTypeInfos = spotGetWithCheckHelper.getSizeTypeInfosWithCheck(
            // ネイティブ用のサイズは除外
            relaySpotSizetypes.map { it.sizeTypeId }.filterNot { it.isNative() }
        )
        val correctedForm = correctSpotBannerEditForm(form, existingSpot, userType)

        checkConflict(existingSpot.spot, correctedForm.updateTime)
        checkBanner(coAccountId, correctedForm.banner)

        FormatValidationException.checkErrorResult(
            SpotBannerEditValidation.of(
                correctedForm,
                userType,
                existingSpot.spot,
                existingSpot.site,
                existingSpot.isDisplayControl(),
                existingSpot.hasOtherThanBanner(),
                getDeleteSizeTypes(correctedForm.banner, currentSizeTypeInfos),
                spotSizeTypeDeleteRuleHelper.getRule(spotId),
                existingSpot.hasBanner()
            ).let { validator.validate(it) }
        )

        // サイズ種別情報と広告枠は登録先のDBが異なるのでトランザクションも分かれている
        val nextSizetypeIds = sizeTypeInfoPersistHelper
            .bulkCreate(coAccountId, existingSpot.site.platformId, correctedForm.banner?.sizeTypes ?: emptyList())

        spotPersistHelper.editBanner(
            spotId,
            correctedForm.banner,
            existingSpot.isDisplayControl(),
            existingSpot.hasBanner(),
            currentSizeTypeInfos.map { it.sizeTypeId },
            nextSizetypeIds
        )
    }

    /**
     * 社員以外のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param existingSpot 既存の広告枠情報
     * @param userType ユーザー種別
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctSpotBannerEditForm(
        form: SpotBannerEditForm,
        existingSpot: ExistingSpot,
        userType: UserType
    ): SpotBannerEditForm {
        // 社員・新規設定・設定削除の場合は上書きしない
        if (userType.isMaStaff() || form.banner == null || !existingSpot.hasBanner()) return form

        val newBanner = form.banner.copy(
            // 既存のバナー設定の存在は確認済みなので強制キャスト
            isScalable = existingSpot.spotBannerDisplay!!.isScalable,
            closeButton = existingSpot.spotBannerDisplay.closeButtonType?.let {
                CloseButtonForm(
                    existingSpot.spotBannerDisplay.closeButtonType,
                    existingSpot.spotBannerDisplay.closeButtonSize,
                    ColorForm.of(existingSpot.spotBannerDisplay.closeButtonLineColor!!),
                    ColorForm.of(existingSpot.spotBannerDisplay.closeButtonBgColor!!),
                    ColorForm.of(existingSpot.spotBannerDisplay.closeButtonFrameColor!!)
                )
            }
        )

        return form.copy(banner = newBanner)
    }

    @TestOnly
    fun getDeleteSizeTypes(
        bannerSettingForm: BannerSettingForm?,
        sizeTypeInfos: List<SizeTypeInfo>
    ): List<SizeTypeInfo> = if (bannerSettingForm != null) {
        val sizeTypes = bannerSettingForm.sizeTypes.map { it.width to it.height }
        sizeTypeInfos.filterNot { sizeTypes.contains(it.width to it.height) }
    } else {
        sizeTypeInfos
    }

    override fun editNative(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotNativeEditForm) {
        val existingSpot = getExistingSpot(coAccountId, spotId, userType)
        val relaySpotSizetypes = relaySpotSizetypeDao.selectBySpotId(spotId)
        val currentSizeTypeInfos = spotGetWithCheckHelper.getSizeTypeInfosWithCheck(
            // バナー用のサイズは除外
            relaySpotSizetypes.map { it.sizeTypeId }.filter { it.isNative() }
        )
        val correctedForm = correctSpotNativeEditForm(form, existingSpot, userType)

        checkConflict(existingSpot.spot, correctedForm.updateTime)
        checkNative(coAccountId, correctedForm.native, existingSpot.site)

        FormatValidationException.checkErrorResult(
            SpotNativeEditValidation.of(
                correctedForm,
                userType,
                existingSpot.spot,
                existingSpot.site,
                existingSpot.isDisplayControl(),
                existingSpot.hasOtherThanNative(),
                // サイトの platformId は更新されないので更新時に削除は発生しない想定.
                correctedForm.native?.let { emptyList() } ?: currentSizeTypeInfos,
                spotSizeTypeDeleteRuleHelper.getRule(spotId),
                existingSpot.hasNativeStandard(),
                existingSpot.hasNativeVideo()
            ).let { validator.validate(it) }
        )

        spotPersistHelper.editNative(
            spotId,
            correctedForm.native,
            existingSpot.isDisplayControl(),
            existingSpot.hasNativeStandard(),
            existingSpot.hasNativeVideo(),
            currentSizeTypeInfos.map { it.sizeTypeId },
            correctedForm.native?.let { listOf(existingSpot.getNativeSizeTypeId()) } ?: emptyList()
        )
    }

    /**
     * 社員以外のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param existingSpot 既存の広告枠情報
     * @param userType ユーザー種別
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctSpotNativeEditForm(
        form: SpotNativeEditForm,
        existingSpot: ExistingSpot,
        userType: UserType
    ): SpotNativeEditForm {
        // 社員・新規設定・設定削除の場合は上書きしない
        if (userType.isMaStaff() || form.native == null || !existingSpot.hasNative()) return form

        val newNative = form.native.copy(
            standard = correctNativeStandardForm(form.native.standard, existingSpot.spotNativeDisplay),
            video = correctNativeVideoForm(form.native.video, existingSpot.spotNativeVideoDisplay)
        )

        return form.copy(native = newNative)
    }

    /**
     * 編集のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param spotNativeDisplay 既存の広告枠情報
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctNativeStandardForm(
        form: NativeStandardForm?,
        spotNativeDisplay: SpotNativeDisplay?
    ): NativeStandardForm? {
        if (form == null || spotNativeDisplay == null) return form

        val currentCloseButton = spotNativeDisplay.closeButtonType?.let {
            CloseButtonForm(
                spotNativeDisplay.closeButtonType,
                spotNativeDisplay.closeButtonSize,
                ColorForm.of(spotNativeDisplay.closeButtonLineColor!!),
                ColorForm.of(spotNativeDisplay.closeButtonBgColor!!),
                ColorForm.of(spotNativeDisplay.closeButtonFrameColor!!)
            )
        }

        return form.copy(closeButton = currentCloseButton)
    }

    /**
     * 編集のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param spotNativeVideoDisplay 既存の広告枠情報
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctNativeVideoForm(
        form: NativeVideoForm?,
        spotNativeVideoDisplay: SpotNativeVideoDisplay?
    ): NativeVideoForm? {
        if (form == null || spotNativeVideoDisplay == null) return form

        val currentCloseButton = spotNativeVideoDisplay.closeButtonType?.let {
            CloseButtonForm(
                spotNativeVideoDisplay.closeButtonType,
                spotNativeVideoDisplay.closeButtonSize,
                ColorForm.of(spotNativeVideoDisplay.closeButtonLineColor!!),
                ColorForm.of(spotNativeVideoDisplay.closeButtonBgColor!!),
                ColorForm.of(spotNativeVideoDisplay.closeButtonFrameColor!!)
            )
        }

        return form.copy(closeButton = currentCloseButton)
    }

    override fun editVideo(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotVideoEditForm) {
        val existingSpot = getExistingSpot(coAccountId, spotId, userType)
        val currentSpotVideoFloorCpms = spotVideoFloorCpmDao.selectBySpotId(spotId)
        val correctedForm = correctSpotVideoEditForm(form, existingSpot, userType)
        val aspectRatios = getAspectRatios(correctedForm.video)

        checkConflict(existingSpot.spot, correctedForm.updateTime)

        FormatValidationException.checkErrorResult(
            SpotVideoEditValidation.of(
                correctedForm,
                userType,
                existingSpot.spot,
                existingSpot.site,
                existingSpot.isDisplayControl(),
                existingSpot.hasOtherThanVideo(),
                existingSpot.spotVideoDisplays.map { it.aspectRatioId }
            ).let { validator.validate(it) }
        )

        spotPersistHelper.editVideo(
            spotId,
            correctedForm.video,
            existingSpot.isDisplayControl(),
            existingSpot.hasVideo(),
            existingSpot.spotVideoDisplays,
            currentSpotVideoFloorCpms,
            aspectRatios
        )
    }

    /**
     * 社員以外のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param existingSpot 既存の広告枠情報
     * @param userType ユーザー種別
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctSpotVideoEditForm(
        form: SpotVideoEditForm,
        existingSpot: ExistingSpot,
        userType: UserType
    ): SpotVideoEditForm {
        // 社員・新規設定・設定削除の場合は上書きしない
        if (userType.isMaStaff() || form.video == null || !existingSpot.hasVideo()) return form

        val newVideo = form.video.copy(
            details = form.video.details.map { correctVideoDetailForm(it, existingSpot.spotVideoDisplays) }
        )

        return form.copy(video = newVideo)
    }

    /**
     * 編集のとき、引数の form の社員限定項目を既存の登録内容で上書きする.
     *
     * @param form Form
     * @param spotVideoDisplays 既存の広告枠情報
     * @return 社員限定項目を必要に応じて既存の登録内容で上書きしたForm
     */
    @TestOnly
    fun correctVideoDetailForm(
        form: VideoDetailForm,
        spotVideoDisplays: Collection<SpotVideoDisplay>
    ): VideoDetailForm {
        val currentDisplay = spotVideoDisplays.find { it.aspectRatioId == form.aspectRatioId } ?: return form

        val currentCloseButton = currentDisplay.closeButtonType?.let {
            CloseButtonForm(
                currentDisplay.closeButtonType,
                currentDisplay.closeButtonSize,
                ColorForm.of(currentDisplay.closeButtonLineColor!!),
                ColorForm.of(currentDisplay.closeButtonBgColor!!),
                ColorForm.of(currentDisplay.closeButtonFrameColor!!)
            )
        }

        return form.copy(isScalable = currentDisplay.isScalable, closeButton = currentCloseButton)
    }

    @TestOnly
    fun checkConflict(spot: Spot, updateTime: LocalDateTime) {
        if (spot.updateTime != updateTime) {
            throw ResourceConflictException("ID:${spot.spotId}の広告枠の編集がコンフリクトしました。")
        }
    }

    @TestOnly
    fun getExistingSpot(coAccountId: CoAccountId, spotId: SpotId, userType: UserType): ExistingSpot {
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.editableStatuses, userType)
        val site = spotGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
        val spotBanner = spotBannerDao.selectById(spotId)
        val spotBannerDisplay = spotBanner?.let { spotBannerDisplayDao.selectById(spotId) }
        val spotNative = spotNativeDao.selectById(spotId)
        val spotNativeDisplay = spotNative?.let { spotNativeDisplayDao.selectById(spotId) }
        val spotNativeVideoDisplay = spotNative?.let { spotNativeVideoDisplayDao.selectBySpotId(spotId) }
        val spotVideo = spotVideoDao.selectById(spotId)
        val spotVideoDisplays = spotVideo?.let { spotVideoDisplayDao.selectBySpotId(spotId) } ?: emptyList()

        SpotUtils.checkSpotBannerConsistency(spotId, spotBanner, spotBannerDisplay)
        SpotUtils.checkSpotNativeConsistency(spotId, spotNative, spotNativeDisplay, spotNativeVideoDisplay)
        SpotUtils.checkSpotVideoConsistency(spotId, spotVideo, spotVideoDisplays)

        return ExistingSpot(
            spot,
            site,
            spotBannerDisplay,
            spotNativeDisplay,
            spotNativeVideoDisplay,
            spotVideoDisplays
        )
    }

    // このサービス内のみでの利用を想定しているので外部では利用しないこと.
    data class ExistingSpot(
        val spot: Spot,
        val site: Site,
        val spotBannerDisplay: SpotBannerDisplay?,
        val spotNativeDisplay: SpotNativeDisplay?,
        val spotNativeVideoDisplay: SpotNativeVideoDisplay?,
        val spotVideoDisplays: Collection<SpotVideoDisplay>
    ) {
        fun isDisplayControl(): Boolean = SpotUtils.isDisplayControl(
            spotBannerDisplay,
            spotNativeDisplay,
            spotNativeVideoDisplay,
            spotVideoDisplays
        )

        // バナー設定があるか
        fun hasBanner(): Boolean = spotBannerDisplay != null

        // ネイティブ設定があるか
        fun hasNative(): Boolean = hasNativeStandard() || hasNativeVideo()

        // ネイティブ設定（通常デザイン）があるか
        fun hasNativeStandard(): Boolean = spotNativeDisplay != null

        // ネイティブ設定（動画デザイン）があるか
        fun hasNativeVideo(): Boolean = spotNativeVideoDisplay != null

        // ビデオ設定があるか
        fun hasVideo(): Boolean = spotVideoDisplays.isNotEmpty()

        // バナー設定以外の設定があるか
        fun hasOtherThanBanner(): Boolean = hasNative() || hasVideo()

        // ネイティブ設定以外の設定があるか
        fun hasOtherThanNative(): Boolean = hasBanner() || hasVideo()

        // ビデオ設定以外の設定があるか
        fun hasOtherThanVideo(): Boolean = hasBanner() || hasNative()

        fun getNativeSizeTypeId() = if (site.platformId.isPc()) SizeTypeId.nativePc else SizeTypeId.nativeSp
    }
}
