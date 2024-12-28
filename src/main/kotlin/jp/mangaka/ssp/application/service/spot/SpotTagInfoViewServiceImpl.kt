package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.service.spot.helper.SpotGetWithCheckHelper
import java.util.Properties
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spotbanner.SpotBannerDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotnative.SpotNativeDao
import jp.mangaka.ssp.infrastructure.datasource.dao.spotvideo.SpotVideoDao
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagInfoView
import org.apache.velocity.app.Velocity
import org.springframework.stereotype.Service

@Service
class SpotTagInfoViewServiceImpl(
    private val spotGetWithCheckHelper: SpotGetWithCheckHelper,
    private val spotBannerDao: SpotBannerDao,
    private val spotNativeDao: SpotNativeDao,
    private val spotVideoDao: SpotVideoDao
) : SpotTagInfoViewService {
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
     * 入力されたspotIdで3テーブルに検索をかけ、spotTypeを生成して返す
     * バナー、ネイティブ、ビデオのうち検索がヒットしたものを返す、複数の場合は「/」で区切る
     * @param spotId 広告枠ID
     * @return spotType 広告枠種別
     */
    fun generateSpotType(spotId: SpotId): String {
        return listOfNotNull(
            spotBannerDao.selectById(spotId)?.let { "banner" },
            spotNativeDao.selectById(spotId)?.let { "native" },
            spotVideoDao.selectById(spotId)?.let { "video" }
        ).joinToString(separator = "/")
    }

    /**
     * 生成した関数をレスポンス用のオブジェクトにして返す
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 生成した広告タグをまとめたオブジェクト（spotTagInfoView）
     */
    override fun getSpotTagInfo(coAccountId: CoAccountId, spotId: SpotId): SpotTagInfoView {
        val spot = spotGetWithCheckHelper.getSpotWithCheck(spotId, SpotStatus.entries)
        val site = spotGetWithCheckHelper.getSiteWithCheck(coAccountId, spot.siteId, SiteStatus.entries)
        val spotType = generateSpotType(spotId)

        return SpotTagInfoView(
            spot.spotName,
            spotType,
            site.siteId,
            site.siteName,
            site.siteType,
            spot.spotStatus,
            spot.displayType,
            spot.descriptions
        )
    }
}
