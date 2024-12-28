package jp.mangaka.ssp.application.service.spot.helper

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatio.AspectRatioStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.aspectratio.AspectRatioDao
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.currencymaster.CurrencyMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.DecorationDao
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster.DspMasterDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplate.NativeTemplateStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate.NativeTemplateDao
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElement
import jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement.NativeTemplateElementDao
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.SpotDao
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component

// TODO アプリ全体で共通利用できる関数は適切なGetWithCheckHelperを作成し切り出す。
@Component
class SpotGetWithCheckHelper(
    private val aspectRatioDao: AspectRatioDao,
    private val countryMasterDao: CountryMasterDao,
    private val currencyMasterDao: CurrencyMasterDao,
    private val decorationDao: DecorationDao,
    private val dspMasterDao: DspMasterDao,
    private val nativeTemplateDao: NativeTemplateDao,
    private val nativeTemplateElementDao: NativeTemplateElementDao,
    private val siteDao: SiteDao,
    private val sizeTypeInfoDao: SizeTypeInfoDao,
    private val spotDao: SpotDao
) {
    /**
     * @param currencyId 通貨ID
     * @return 引数の通貨IDに合致するCurrencyMaster
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getCurrencyWithCheck(currencyId: CurrencyId): CurrencyMaster = currencyMasterDao
        .selectById(currencyId)
        ?: throw CompassManagerException("通貨ID:${currencyId}に合致するエンティティ取得できませんでした。")

    /**
     * @param coAccountId CoアカウントID
     * @param decorationId デコレーション設定ID
     * @return 引数のCoアカウントID または co_account_id=0でデコレーション設定IDに合致するDecoration
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getDecorationWithCheck(coAccountId: CoAccountId, decorationId: DecorationId): Decoration = decorationDao
        .selectById(decorationId)
        ?.takeIf { it.coAccountId in listOf(CoAccountId.zero, coAccountId) }
        ?: throw CompassManagerException(
            "デコレーションID:$decorationId/CoアカウントID:0 or ${coAccountId}に合致するエンティティが取得できませんでした。"
        )

    /**
     * @param coAccountId CoアカウントID
     * @param siteId サイトID
     * @param siteStatuses サイトステータスのリスト
     * @return 引数のCoアカウントID、サイトID、サイトステータスに合致するSite
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    @Deprecated("SiteGetWithCheckHelperを作成済みなので今後はそちらを利用する。この関数は利用がなくなったら削除する。")
    fun getSiteWithCheck(coAccountId: CoAccountId, siteId: SiteId, siteStatuses: Collection<SiteStatus>): Site =
        siteDao
            .selectByIdAndStatuses(siteId, siteStatuses)
            ?.takeIf { it.coAccountId == coAccountId }
            ?: throw CompassManagerException(
                "サイトID:$siteId/CoアカウントID:$coAccountId/サイトステータス：$siteStatuses" +
                    "に合致するエンティティが取得できませんでした。"
            )

    /**
     * @param dspIds DSPIDのリスト
     * @return 引数のDSPIDに合致するDspMasterのリスト
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getDspsWithCheck(dspIds: Collection<DspId>): List<DspMaster> = dspMasterDao.selectByIds(dspIds).apply {
        val notExistIds = dspIds - this.map { it.dspId }.toSet()

        if (notExistIds.isNotEmpty()) {
            throw CompassManagerException("DSPID${notExistIds}に合致するエンティティが取得できませんでした。")
        }
    }

    /**
     * @param spotId 広告枠ID
     * @param spotStatuses 広告枠ステータスのリスト
     * @return 引数の広告枠IDに合致するSpot
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getSpotWithCheck(spotId: SpotId, spotStatuses: Collection<SpotStatus>): Spot =
        spotDao
            .selectByIdAndStatus(spotId, spotStatuses)
            ?: throw CompassManagerException(
                "広告枠ID:$spotId/広告枠ステータス：$spotStatuses\"" +
                    "に合致するエンティティが取得できませんでした。"
            )

    /**
     * @param spotId 広告枠ID
     * @param spotStatuses 広告枠ステータスのリスト
     * @param userType ユーザー種別
     * @return 引数の広告枠IDに合致するSpot
     * @throws CompassManagerException
     *   - 条件に合致するエンティティが取得できなかったとき
     *   - 広告枠へのアクセスが許可されていないユーザーだったとき
     */
    fun getSpotWithCheck(spotId: SpotId, spotStatuses: Collection<SpotStatus>, userType: UserType): Spot {
        val spot = getSpotWithCheck(spotId, spotStatuses)

        if (!userType.isMaStaff() && spot.upstreamType.isPrebidjs()) {
            throw CompassManagerException(
                "ユーザー種別：${userType}でヘッダービディング：prebidjs の広告枠を取得しようとしました。"
            )
        }

        return spot
    }

    /**
     * @param coAccountId CoアカウントID
     * @param nativeTemplateId ネイティブテンプレートID
     * @param nativeTemplateStatuses ネイティブテンプレートステータスのリスト
     * @return 引数のCoアカウントID または co_account_id=NULL でネイティブテンプレートID・ステータスに合致するNativeTemplate
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getNativeTemplateWithCheck(
        coAccountId: CoAccountId,
        nativeTemplateId: NativeTemplateId,
        nativeTemplateStatuses: Collection<NativeTemplateStatus>
    ): NativeTemplate = nativeTemplateDao
        .selectByIdAndStatues(nativeTemplateId, nativeTemplateStatuses)
        ?.takeIf { it.coAccountId == null || it.coAccountId == coAccountId }
        ?: throw CompassManagerException(
            "ネイティブテンプレートID:$nativeTemplateId/CoアカウントID: NULL or $coAccountId/" +
                "ネイティブテンプレートステータス：${nativeTemplateStatuses}に合致するエンティティが取得できませんでした。"
        )

    /**
     * @param coAccountId CoアカウントID
     * @param nativeTemplateId ネイティブテンプレートID
     * @param nativeTemplateStatuses ネイティブテンプレートステータスのリスト
     * @param platformId プラットフォームID
     * @return 通常デザインのネイティブテンプレート
     */
    fun getNativeStandardTemplateWithCheck(
        coAccountId: CoAccountId,
        nativeTemplateId: NativeTemplateId,
        nativeTemplateStatuses: Collection<NativeTemplateStatus>,
        platformId: PlatformId
    ): NativeTemplate {
        val (nativeTemplate, nativeTemplateElements) = getNativeTemplateAndElements(
            coAccountId,
            nativeTemplateId,
            nativeTemplateStatuses,
            platformId
        )

        if (nativeTemplateElements.any { it.nativeElementId.isVideo() }) {
            throw CompassManagerException(
                "ネイティブデザインに動画要素を含むネイティブテンプレート:${nativeTemplateId}は設定できません。"
            )
        }

        return nativeTemplate
    }

    /**
     * @param nativeTemplateId ネイティブテンプレートID
     * @param nativeTemplateStatuses ネイティブテンプレートステータスのリスト
     * @return 動画デザインのネイティブテンプレート
     */
    fun getNativeVideoTemplateWithCheck(
        nativeTemplateId: NativeTemplateId,
        nativeTemplateStatuses: Collection<NativeTemplateStatus>,
    ): NativeTemplate {
        val (nativeTemplate, nativeTemplateElements) = getNativeTemplateAndElements(
            null,
            nativeTemplateId,
            nativeTemplateStatuses
        )

        if (nativeTemplateElements.none { it.nativeElementId.isVideo() }) {
            throw CompassManagerException(
                "ネイティブ動画デザインに動画要素を含まないネイティブテンプレート:${nativeTemplateId}は設定できません。"
            )
        }

        return nativeTemplate
    }

    @TestOnly
    fun getNativeTemplateAndElements(
        coAccountId: CoAccountId?,
        nativeTemplateId: NativeTemplateId,
        nativeTemplateStatuses: Collection<NativeTemplateStatus>,
        // ビデオの場合はプラットフォームのチェックは行わない
        platformId: PlatformId? = null
    ): Pair<NativeTemplate, List<NativeTemplateElement>> {
        val nativeTemplate = nativeTemplateDao
            .selectByIdAndStatues(nativeTemplateId, nativeTemplateStatuses)
            ?.takeIf { nativeTemplate ->
                val isMatchPlatform = platformId?.let { nativeTemplate.platformId == it } ?: true
                nativeTemplate.coAccountId == coAccountId && isMatchPlatform
            }
            ?: throw CompassManagerException(
                "ネイティブテンプレートID:$nativeTemplateId/CoアカウントID:$coAccountId/" +
                    "ネイティブテンプレートステータス：$nativeTemplateStatuses/" +
                    platformId?.let { "プラットフォームID:$platformId" } +
                    "に合致するエンティティが取得できませんでした。"
            )
        val nativeTemplateElements = nativeTemplateElementDao.selectByNativeTemplateIds(listOf(nativeTemplateId))

        return nativeTemplate to nativeTemplateElements
    }

    fun getAspectRatiosWithCheck(
        aspectRatioIds: Collection<AspectRatioId>,
        statuses: Collection<AspectRatioStatus>
    ): List<AspectRatio> = aspectRatioDao
        .selectByAspectRatioIdsAndStatuses(aspectRatioIds, statuses)
        .also { entities ->
            val notFoundIds = aspectRatioIds - entities.map { it.aspectRatioId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "アスペクト比ID：$notFoundIds/アスペクト比ステータス：${statuses}に合致するエンティティが取得できませんでした。"
                )
            }
        }

    /**
     * @param countryIds 国IDのリスト
     * @return 引数の国IDに合致する CountryMaster のリスト
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getCountiesWithCheck(countryIds: Collection<CountryId>): List<CountryMaster> = countryMasterDao
        .selectByIds(countryIds)
        .also { entities ->
            val notFoundIds = countryIds - entities.map { it.countryId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "国ID：${notFoundIds}に合致するエンティティが取得できませんでした。"
                )
            }
        }

    /**
     * @param countryId 国ID
     * @return 引数の国IDに合致する CountryMaster
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getCountryWithCheck(countryId: CountryId): CountryMaster = getCountiesWithCheck(listOf(countryId)).first()

    /**
     * @param sizeTypeIds サイズ種別IDのリスト
     * @return 引数のサイズ種別IDに合致する SizeTypeInfo のリスト
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getSizeTypeInfosWithCheck(sizeTypeIds: Collection<SizeTypeId>): List<SizeTypeInfo> = sizeTypeInfoDao
        .selectByIds(sizeTypeIds)
        .also { entities ->
            val notFoundIds = sizeTypeIds - entities.map { it.sizeTypeId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "サイズ種別ID：${notFoundIds}に合致するエンティティが取得できませんでした。"
                )
            }
        }
}
