package jp.mangaka.ssp.application.service.spot.helper

import jp.mangaka.ssp.application.service.coaccount.CoAccountGetWithCheckHelper
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.relaydefaultcoaccountdsp.RelayDefaultCoAccountDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotdsp.RelaySpotDspInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.relayspotsizetype.RelaySpotSizetypeInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay.SpotBannerDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay.SpotNativeDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay.SpotNativeVideoDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotupstreamcurrency.SpotUpstreamCurrencyInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplay
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay.SpotVideoDisplayInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpm
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmInsert
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideofloorcpm.SpotVideoFloorCpmUpdate
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BannerSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.DspForm
import jp.mangaka.ssp.presentation.controller.spot.form.NativeSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm
import jp.mangaka.ssp.presentation.controller.spot.form.VideoSettingForm.VideoDetailForm
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SpotPersistHelper(
    private val relayDefaultCoAccountDspDao: RelayDefaultCoAccountDspDao,
    private val relaySpotDspDao: RelaySpotDspDao,
    private val relaySpotSizetypeDao: RelaySpotSizetypeDao,
    private val spotDao: SpotDao,
    private val spotBannerDao: SpotBannerDao,
    private val spotBannerDisplayDao: SpotBannerDisplayDao,
    private val spotNativeDao: SpotNativeDao,
    private val spotNativeDisplayDao: SpotNativeDisplayDao,
    private val spotNativeVideoDisplayDao: SpotNativeVideoDisplayDao,
    private val spotUpstreamCurrencyDao: SpotUpstreamCurrencyDao,
    private val spotVideoDao: SpotVideoDao,
    private val spotVideoDisplayDao: SpotVideoDisplayDao,
    private val spotVideoFloorCpmDao: SpotVideoFloorCpmDao,
    private val coAccountGetWithCheckHelper: CoAccountGetWithCheckHelper
) {
    /**
     * 広告枠の新規作成を行う
     *
     * @param coAccountId CoアカウントID
     * @param form フォーム
     * @param site サイト
     * @param sizeTypeIds サイズ種別IDのリスト
     * @param userType ユーザー種別
     * @param aspectRatios アスペクト比のリスト
     * @return 新規作成された広告枠ID
     */
    @Transactional("CompassMasterTX")
    fun create(
        coAccountId: CoAccountId,
        form: SpotCreateForm,
        site: Site,
        sizeTypeIds: Collection<SizeTypeId>,
        userType: UserType,
        aspectRatios: Collection<AspectRatio>
    ): SpotId {
        val spotId = spotDao.insert(SpotInsert.of(form, site))

        // 基本設定の登録
        insertUpstreamCurrencyIfNeeded(coAccountId, spotId, form.basic)
        relaySpotSizetypeDao.bulkInsert(sizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) })
        // DSP設定の登録
        insertDsps(coAccountId, spotId, form.dsps, userType)
        // バナー設定の登録
        insertBannerIfNeeded(spotId, form)
        // ネイティブ設定の登録
        insertNativeIfNeeded(spotId, form)
        // ビデオ設定の登録
        insertVideoIfNeeded(spotId, form, aspectRatios)

        return spotId
    }

    @TestOnly
    fun insertDsps(coAccountId: CoAccountId, spotId: SpotId, forms: List<DspForm>, userType: UserType) {
        val defaultDsps = relayDefaultCoAccountDspDao.selectByCoAccountId(coAccountId)

        if (userType.isMaStaff()) {
            // マイクロアド社員は画面からの入力内容で登録
            relaySpotDspDao.bulkInsert(RelaySpotDspInsert.of(spotId, forms, defaultDsps))
        } else {
            // マイクロアド社員でなければデフォルトの内容で登録
            relaySpotDspDao.bulkInsert(RelaySpotDspInsert.of(spotId, defaultDsps))
        }
    }

    /**
     * ヘッダービディング通貨の設定があり かつ 対象Coアカウントに設定されている通貨と異なるとき、関連テーブルの登録を行う
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param form 登録フォーム
     */
    @TestOnly
    fun insertUpstreamCurrencyIfNeeded(coAccountId: CoAccountId, spotId: SpotId, form: BasicSettingCreateForm) {
        val coAccount = coAccountGetWithCheckHelper.getCoAccountWithCheck(coAccountId)

        if (!form.upstreamType.isPrebidjs() || form.currencyId == coAccount.currencyId) return

        // currencyIdはServiceで検証済みのため強制キャスト
        spotUpstreamCurrencyDao.insert(SpotUpstreamCurrencyInsert(spotId, form.currencyId!!))
    }

    /**
     * バナー設定があるとき、関連テーブルの登録を行う
     *
     * @param spotId 広告枠ID
     * @param form 登録フォーム
     */
    @TestOnly
    fun insertBannerIfNeeded(spotId: SpotId, form: SpotCreateForm) {
        if (form.banner == null) return

        spotBannerDao.insert(SpotBannerInsert(spotId))
        spotBannerDisplayDao.insert(SpotBannerDisplayInsert.of(spotId, form))
    }

    /**
     * ネイティブ設定があるとき、関連テーブルの登録を行う
     *
     * @param spotId 広告枠ID
     * @param form 登録フォーム
     */
    @TestOnly
    fun insertNativeIfNeeded(spotId: SpotId, form: SpotCreateForm) {
        if (form.native == null) return

        spotNativeDao.insert(SpotNativeInsert.of(spotId, form.native))

        form.native.standard?.let {
            spotNativeDisplayDao.insert(SpotNativeDisplayInsert.of(spotId, it))
        }
        form.native.video?.let {
            spotNativeVideoDisplayDao.insert(SpotNativeVideoDisplayInsert.of(spotId, it, form.basic.isDisplayControl))
        }
    }

    @TestOnly
    fun insertVideoIfNeeded(spotId: SpotId, form: SpotCreateForm, aspectRatios: Collection<AspectRatio>) {
        if (form.video == null) return

        spotVideoDao.insert(SpotVideoInsert.of(spotId, form.video))
        spotVideoDisplayDao.inserts(SpotVideoDisplayInsert.of(spotId, form.video, aspectRatios))
        spotVideoFloorCpmDao.inserts(SpotVideoFloorCpmInsert.of(spotId, form.video))
    }

    /**
     * 広告枠の更新を行う.
     *
     * @param spotId 広告枠ID
     * @param form フォーム
     */
    @Transactional("CompassMasterTX")
    fun editBasic(spotId: SpotId, form: BasicSettingEditForm) {
        spotDao.update(
            SpotUpdate(
                spotId,
                form.spotName!!,
                form.spotMaxSize?.width,
                form.spotMaxSize?.height,
                form.description,
                form.pageUrl
            )
        )
    }

    /**
     * NOT: DSP設定の編集を行う
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param forms DSP設定Formのリスト
     */
    @Transactional("CompassMasterTX")
    fun editDsps(coAccountId: CoAccountId, spotId: SpotId, forms: List<DspForm>) {
        val existingDspIds = relaySpotDspDao.selectBySpotId(spotId).map { it.dspId }
        val defaultDsps = relayDefaultCoAccountDspDao.selectByCoAccountId(coAccountId)
        val inserts = RelaySpotDspInsert.of(spotId, forms, defaultDsps)

        relaySpotDspDao.bulkInsert(inserts.filter { !existingDspIds.contains(it.dspId) })
        relaySpotDspDao.bulkUpdate(inserts.filter { existingDspIds.contains(it.dspId) })
        relaySpotDspDao.deleteBySpotIdAndDspIds(spotId, existingDspIds - inserts.map { it.dspId }.toSet())
    }

    /**
     * @param spotId 広告枠ID
     * @param form フォーム
     * @param isDisplayControl 表示制御有無
     * @param isExistingBanner 既存の広告枠にバナーが設定されているか
     * @param currentSizeTypeIds 変更前に広告枠に紐づいているサイズ種別IDのリスト
     * @param nextSizeTypeIds 変更後に広告枠に紐づくサイズ種別IDのリスト
     */
    @Transactional("CompassMasterTX")
    fun editBanner(
        spotId: SpotId,
        form: BannerSettingForm?,
        isDisplayControl: Boolean,
        isExistingBanner: Boolean,
        currentSizeTypeIds: Collection<SizeTypeId>,
        nextSizeTypeIds: List<SizeTypeId>
    ) {
        // ネイティブ用のサイズはサービス側でフィルタリングされている想定
        relaySpotSizetypeDao.deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)

        if (form == null) {
            // 削除
            spotBannerDisplayDao.deleteById(spotId)
            spotBannerDao.deleteById(spotId)
        } else if (isExistingBanner) {
            // 更新
            spotBannerDisplayDao.update(SpotBannerDisplayInsert.of(spotId, form, isDisplayControl))
            relaySpotSizetypeDao.bulkInsert(nextSizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) })
        } else {
            // 新規
            spotBannerDao.insert(SpotBannerInsert(spotId))
            spotBannerDisplayDao.insert(SpotBannerDisplayInsert.of(spotId, form, isDisplayControl))
            relaySpotSizetypeDao.bulkInsert(nextSizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) })
        }
    }

    /**
     * @param spotId 広告枠ID
     * @param form フォーム
     * @param isDisplayControl 表示制御有無
     * @param isExistingNativeStandard 既存の広告枠にネイティブ通常デザインが設定されているか
     * @param isExistingNativeVideo 既存の広告枠にネイティブ動画デザインが設定されているか
     * @param currentSizeTypeIds 変更前に広告枠に紐づいているサイズ種別IDのリスト
     * @param nextSizeTypeIds 変更後に広告枠に紐づくサイズ種別IDのリスト
     */
    @Transactional("CompassMasterTX")
    fun editNative(
        spotId: SpotId,
        form: NativeSettingForm?,
        isDisplayControl: Boolean,
        isExistingNativeStandard: Boolean,
        isExistingNativeVideo: Boolean,
        currentSizeTypeIds: Collection<SizeTypeId>,
        nextSizeTypeIds: List<SizeTypeId>
    ) {
        // バナー用のサイズはサービス側でフィルタリングされている想定
        relaySpotSizetypeDao.deleteBySpotIdAndSizeTypeIds(spotId, currentSizeTypeIds)

        if (form == null) {
            // 削除
            spotNativeDisplayDao.deleteById(spotId)
            spotNativeVideoDisplayDao.deleteById(spotId)
            spotNativeDao.deleteById(spotId)
        } else if (isExistingNativeStandard || isExistingNativeVideo) {
            // 更新
            spotNativeDao.update(SpotNativeUpdate.of(spotId, form))
            relaySpotSizetypeDao.bulkInsert(nextSizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) })

            when {
                form.standard == null -> spotNativeDisplayDao.deleteById(spotId)
                isExistingNativeStandard -> spotNativeDisplayDao.update(
                    SpotNativeDisplayInsert.of(spotId, form.standard)
                )
                else -> spotNativeDisplayDao.insert(
                    SpotNativeDisplayInsert.of(spotId, form.standard)
                )
            }

            when {
                form.video == null -> spotNativeVideoDisplayDao.deleteById(spotId)
                isExistingNativeVideo -> spotNativeVideoDisplayDao.update(
                    SpotNativeVideoDisplayInsert.of(spotId, form.video, isDisplayControl)
                )
                else -> spotNativeVideoDisplayDao.insert(
                    SpotNativeVideoDisplayInsert.of(spotId, form.video, isDisplayControl)
                )
            }
        } else {
            // 新規
            spotNativeDao.insert(SpotNativeInsert.of(spotId, form))
            form.standard?.let { spotNativeDisplayDao.insert(SpotNativeDisplayInsert.of(spotId, it)) }
            form.video?.let {
                spotNativeVideoDisplayDao.insert(SpotNativeVideoDisplayInsert.of(spotId, it, isDisplayControl))
            }
            relaySpotSizetypeDao.bulkInsert(nextSizeTypeIds.map { RelaySpotSizetypeInsert(spotId, it) })
        }
    }

    /**
     * @param spotId 広告枠ID
     * @param form フォーム
     * @param isDisplayControl 表示制御有無
     * @param isExistingVideo 既存の広告枠にビデオが設定されているか
     * @param currentSpotVideoDisplays 現在のビデオ詳細設定のリスト
     * @param currentSpotVideoFloorCpms 現在のビデオ固定単価設定のリスト
     * @param aspectRatios アスペクト比のリスト
     */
    @Transactional("CompassMasterTX")
    fun editVideo(
        spotId: SpotId,
        form: VideoSettingForm?,
        isDisplayControl: Boolean,
        isExistingVideo: Boolean,
        currentSpotVideoDisplays: Collection<SpotVideoDisplay>,
        currentSpotVideoFloorCpms: Collection<SpotVideoFloorCpm>,
        aspectRatios: Collection<AspectRatio>
    ) {
        if (form == null) {
            // 削除
            spotDao.updateRotationMaxById(spotId, Spot.rotationMaxDefaultValue)
            spotVideoDisplayDao.deleteBySpotIdAndAspectRatioIds(
                spotId,
                currentSpotVideoDisplays.map { it.aspectRatioId }
            )
            spotVideoFloorCpmDao.deleteBySpotIdAndAspectRatioIds(
                spotId,
                currentSpotVideoFloorCpms.map { it.aspectRatioId }
            )
            spotVideoDao.deleteById(spotId)
        } else if (isExistingVideo) {
            // 更新
            // バリデーション後に呼び出される想定なので強制キャスト
            spotDao.updateRotationMaxById(spotId, form.rotationMax!!)
            spotVideoDao.update(SpotVideoUpdate.of(spotId, form))
            editSpotVideoDisplay(spotId, form.details, form.prLabelType, currentSpotVideoDisplays, aspectRatios)
            editSpotVideoFloorCpm(spotId, form.details, currentSpotVideoFloorCpms)
        } else {
            // 新規
            // バリデーション後に呼び出される想定なので強制キャスト
            spotDao.updateRotationMaxById(spotId, form.rotationMax!!)
            spotVideoDao.insert(SpotVideoInsert.of(spotId, form))
            spotVideoDisplayDao.inserts(SpotVideoDisplayInsert.of(spotId, form, aspectRatios))
            spotVideoFloorCpmDao.inserts(SpotVideoFloorCpmInsert.of(spotId, form))
        }
    }

    @TestOnly
    fun editSpotVideoDisplay(
        spotId: SpotId,
        forms: Collection<VideoDetailForm>,
        prLabelType: Int?,
        currentSpotVideoDisplays: Collection<SpotVideoDisplay>,
        aspectRatios: Collection<AspectRatio>
    ) {
        val existingAspectRatioIdsForDisplay = currentSpotVideoDisplays.map { it.aspectRatioId }
        spotVideoDisplayDao.inserts(
            forms
                .filterNot { existingAspectRatioIdsForDisplay.contains(it.aspectRatioId) }
                .let { SpotVideoDisplayInsert.of(spotId, it, prLabelType, aspectRatios) }
        )
        spotVideoDisplayDao.updates(
            forms
                .filter { existingAspectRatioIdsForDisplay.contains(it.aspectRatioId) }
                .let { SpotVideoDisplayInsert.of(spotId, it, prLabelType, aspectRatios) }
        )
        spotVideoDisplayDao.deleteBySpotIdAndAspectRatioIds(
            spotId,
            existingAspectRatioIdsForDisplay - forms.mapNotNull { it.aspectRatioId }.toSet()
        )
    }

    @TestOnly
    fun editSpotVideoFloorCpm(
        spotId: SpotId,
        forms: Collection<VideoDetailForm>,
        currentSpotVideoFloorCpms: Collection<SpotVideoFloorCpm>
    ) {
        val inserts = forms
            .filterNot { currentSpotVideoFloorCpms.isExist(it) }
            .let { SpotVideoFloorCpmInsert.of(spotId, it) }
        val updates = forms
            .filter { currentSpotVideoFloorCpms.isExist(it) }
            .let { SpotVideoFloorCpmUpdate.of(spotId, it) }

        spotVideoFloorCpmDao.inserts(inserts)
        spotVideoFloorCpmDao.updates(updates)
        // アスペクト比には複数期間のレコードが存在するので、登録・更新したアスペクト比以外のみ削除
        spotVideoFloorCpmDao.deleteBySpotIdAndAspectRatioIds(
            spotId,
            currentSpotVideoFloorCpms.map { it.aspectRatioId } -
                inserts.map { it.aspectRatioId }.toSet() -
                updates.map { it.aspectRatioId }.toSet()
        )
    }

    fun Collection<SpotVideoFloorCpm>.isExist(form: VideoDetailForm): Boolean =
        any { it.aspectRatioId == form.aspectRatioId && it.startDate == form.floorCpmStartDate }
}
