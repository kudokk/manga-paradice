package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import java.io.StringWriter
import java.util.Properties
import jp.mangaka.ssp.application.service.spot.util.CryptUtils
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagView
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Service

@Service
class SpotTagViewServiceImpl(
    private val cryptUtils: CryptUtils,
    private val spotGetWithCheckHelper: SpotGetWithCheckHelper,
    private val sizeTypeInfoDao: SizeTypeInfoDao,
) : SpotTagViewService {
    // velocityのプロパティ設定
    private val p = Properties()

    init {
        // velocityの初期設定
        p.setProperty("resource.loaders", "class")
        p.setProperty(
            "resource.loader.class.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        )
        p.setProperty("resource.default_encoding", "UTF-8")
        Velocity.init(p)
    }

    /**
     * 縦横のサイズをタグ属性で設定するための文字列を作成する(OR条件)
     * @param width
     * @param height
     * @return サイズ情報を記載した文字列
     */
    @TestOnly
    fun getSizeAttributeByOrCondition(width: Int?, height: Int?): String =
        "${width?.let { "width=\"${it}\" " } ?: ""}${height?.let { "height=\"${it}\" " } ?: ""}"

    /**
     * 縦横のサイズをstyle属性で指定するための文字列を作成する(OR条件)
     * @param width
     * @param height
     * @return サイズ情報を記載したcss文
     */
    @TestOnly
    fun getSizeStyleByOrCondition(width: Int?, height: Int?): String =
        if (width == null && height == null) "" else "style=\"${width?.let { "width:${it}px; " } ?: ""}${height?.let { "height:${it}px; " } ?: ""}\""

    /**
     * クリックマクロ向けのパラメータ一覧を取得する
     * @param isAllowedMacroUrl サイト種別
     * @return クリックマクロ向けパラメータのコレクション もしくは 空のコレクション
     */
    @TestOnly
    fun getMacroUrlParameters(isAllowedMacroUrl: Boolean): Map<String, String> = if (isAllowedMacroUrl) {
        mapOf(
            "url" to "\${COMPASS_EXT_URL}",
            "referrer" to "\${COMPASS_EXT_REF}"
        )
    } else {
        emptyMap()
    }

    /**
     * デバイス向けのパラメータを一覧を取得する
     * @param siteType サイト種別
     * @return デバイス向けパラメータのコレクション もしくは 空のコレクション
     */
    @TestOnly
    fun getDeviceParameters(siteType: SiteType): Map<String, String> = if (siteType.isApp()) {
        mapOf(
            "ifa" to "\${COMPASS_EXT_IFA}",
            "appid" to "\${COMPASS_EXT_APPID}",
            "geo" to "\${COMPASS_EXT_GEO}"
        )
    } else {
        emptyMap()
    }

    /**
     * ヘッダ共通コードを生成する
     * @return 生成したヘッダ共通コード
     */
    @TestOnly
    fun generateHeaderTag(): String {
        val sw = StringWriter()

        Velocity.getTemplate("spotTag/SpotTagHeader.vm", "UTF-8").merge(VelocityContext(), sw)
        return sw.toString()
    }

    /**
     *  汎用広告表示タグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @param width 広告枠横幅
     *  @param height 広告枠縦幅
     *  @return 生成した汎用広告表示タグ
     */
    @TestOnly
    fun generateJsWebTag(encryptedSpotId: String, isAllowedMacroUrl: Boolean, width: Int?, height: Int?): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        vc.put("isAllowedMacroUrl", isAllowedMacroUrl)
        vc.put("encryptedSpotId", encryptedSpotId)
        vc.put("widthHeightStr", getSizeStyleByOrCondition(width, height))

        Velocity.getTemplate("spotTag/SpotTagJsWeb.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  Prebid用広告表示タグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param siteType サイト種別
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @param sizeTypeInfoList 受入設定済みのサイズリスト
     *  @return 生成したPrebid用広告表示タグ
     */
    @TestOnly
    fun generatePrebidTag(
        encryptedSpotId: String,
        siteType: SiteType,
        isAllowedMacroUrl: Boolean,
        sizeTypeInfoList: List<SizeTypeInfoView>
    ): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        vc.put("encryptedSpotId", encryptedSpotId)
        vc.put("sizeTypeInfoList", sizeTypeInfoList)

        val params = mutableMapOf("spot" to encryptedSpotId)
        params += getMacroUrlParameters(isAllowedMacroUrl)
        params += getDeviceParameters(siteType)
        vc.put("params", params)

        Velocity.getTemplate("spotTag/SpotTagPrebid.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  GAM用広告表示タグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param siteType サイト種別
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @param width 広告枠横幅
     *  @param height 広告枠縦幅
     *  @return 生成したGAM用広告表示タグ
     */
    @TestOnly
    fun generateGamTag(
        encryptedSpotId: String,
        siteType: SiteType,
        isAllowedMacroUrl: Boolean,
        width: Int?,
        height: Int?
    ): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        val params = mutableMapOf("spot" to encryptedSpotId)
        params += getMacroUrlParameters(isAllowedMacroUrl)
        params += getDeviceParameters(siteType)
        // GAMタグでは広告枠サイズをパラメータとして渡す
        if (width != null) params["width"] = width.toString()
        if (height != null) params["height"] = height.toString()
        vc.put("params", params)

        Velocity.getTemplate("spotTag/SpotTagGam.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  app用広告表示タグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @return 生成したapp用広告表示タグ
     */
    @TestOnly
    fun generateJsAppTag(encryptedSpotId: String): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        vc.put("encryptedSpotId", encryptedSpotId)

        Velocity.getTemplate("spotTag/SpotTagJsApp.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  AMP用広告表示タグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @param width 広告枠横幅
     *  @param height 広告枠縦幅
     *  @return 生成したAMP用広告表示タグ
     */
    @TestOnly
    fun generateAmpTag(encryptedSpotId: String, isAllowedMacroUrl: Boolean, width: Int?, height: Int?): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        vc.put("isAllowedMacroUrl", isAllowedMacroUrl)
        vc.put("encryptedSpotId", encryptedSpotId)
        vc.put("sizeOfSpot", getSizeAttributeByOrCondition(width, height))

        Velocity.getTemplate("spotTag/SpotTagAmp.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  Next.js/Nuxt.js用の広告枠IDタグを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param width 広告枠横幅
     *  @param height 広告枠縦幅
     *  @return 生成した広告枠IDタグ
     */
    @TestOnly
    fun generateSpotIdTag(encryptedSpotId: String, width: Int?, height: Int?): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        vc.put("encryptedSpotId", encryptedSpotId)
        vc.put("sizeOfAttribute", getSizeAttributeByOrCondition(width, height))

        Velocity.getTemplate("spotTag/SpotIdTag.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  広告表示用Reactコンポーネントを生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param siteType サイト種別
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @return 生成した広告表示用Reactコンポーネント
     */
    @TestOnly
    fun generateReactComponent(
        encryptedSpotId: String,
        siteType: SiteType,
        isAllowedMacroUrl: Boolean
    ): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        val params = mutableMapOf("spot" to "spotId")
        params += getMacroUrlParameters(isAllowedMacroUrl)
        params += getDeviceParameters(siteType)
        vc.put("params", params)

        Velocity.getTemplate("spotTag/SpotTagReactComponent.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  広告表示用Vueコンポーネント（ヘッダあり）を生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param siteType サイト種別
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @return 生成した広告表示用Vueコンポーネント（ヘッダあり）
     */
    @TestOnly
    fun generateVueComponentWithHeader(
        encryptedSpotId: String,
        siteType: SiteType,
        isAllowedMacroUrl: Boolean
    ): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        val params = mutableMapOf("spot" to "props.spotId")
        params += getMacroUrlParameters(isAllowedMacroUrl)
        params += getDeviceParameters(siteType)
        vc.put("params", params)

        Velocity.getTemplate("spotTag/SpotTagVueComponentWithHeader.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     *  広告表示用Vueコンポーネント（ヘッダ無し）を生成する
     *  @param encryptedSpotId 暗号化した広告枠ID
     *  @param siteType サイト種別
     *  @param isAllowedMacroUrl 広告枠マクロ利用可否設定
     *  @return 生成した広告表示用Vueコンポーネント（ヘッダ無し）
     */
    @TestOnly
    fun generateVueComponentWithoutHeader(
        encryptedSpotId: String,
        siteType: SiteType,
        isAllowedMacroUrl: Boolean
    ): String {
        val sw = StringWriter()
        val vc = VelocityContext()

        val params = mutableMapOf("spot" to "props.spotId")
        params += getMacroUrlParameters(isAllowedMacroUrl)
        params += getDeviceParameters(siteType)
        vc.put("params", params)

        Velocity.getTemplate("spotTag/SpotTagVueComponentWithoutHeader.vm", "UTF-8").merge(vc, sw)
        return sw.toString()
    }

    /**
     * 生成した関数をレスポンス用のオブジェクトにして返す
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 生成した広告タグをまとめたオブジェクト（spotTagView）
     */
    override fun getSpotTag(coAccountId: CoAccountId, spotId: SpotId): SpotTagView {
        // 広告枠Idからデータを取得
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.entries)
        val site = spotGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
        val selectedSizeTypeInfos = SizeTypeInfoView.of(sizeTypeInfoDao.selectBySpotId(spotId))

        // 広告枠IDを暗号化
        val encryptedSpotId = cryptUtils.encryptForTag(spotId)

        // 広告枠情報をもとに必要な広告タグを生成
        val spotTag = if (spot.upstreamType.isPrebidjs()) {
            generatePrebidTag(
                encryptedSpotId,
                site.siteType,
                site.isAllowedMacroUrl,
                selectedSizeTypeInfos
            )
        } else if ((site.siteType.isWeb()) && spot.displayType.isInline()) {
            generateJsWebTag(
                encryptedSpotId,
                site.isAllowedMacroUrl,
                spot.width,
                spot.height
            )
        } else if (site.siteType.isWeb()) {
            // 表示種別が「インライン以外」の場合は広告サイズを省略する
            generateJsWebTag(encryptedSpotId, site.isAllowedMacroUrl, null, null)
        } else if (spot.deliveryMethod.isJs()) {
            generateJsAppTag(encryptedSpotId)
        } else {
            ""
        }

        return SpotTagView(
            spotId,
            spot.upstreamType,
            spot.isAmp,
            generateHeaderTag(),
            spotTag,
            if (spot.isAmp) generateAmpTag(encryptedSpotId, site.isAllowedMacroUrl, spot.width, spot.height) else "",
            generateSpotIdTag(encryptedSpotId, spot.width, spot.height),
            generateGamTag(encryptedSpotId, site.siteType, site.isAllowedMacroUrl, spot.width, spot.height),
            generateReactComponent(encryptedSpotId, site.siteType, site.isAllowedMacroUrl),
            generateVueComponentWithHeader(encryptedSpotId, site.siteType, site.isAllowedMacroUrl),
            generateVueComponentWithoutHeader(encryptedSpotId, site.siteType, site.isAllowedMacroUrl)
        )
    }
}
