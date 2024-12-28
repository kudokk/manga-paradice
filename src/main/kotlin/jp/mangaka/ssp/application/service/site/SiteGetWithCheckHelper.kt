package jp.mangaka.ssp.application.service.site

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.site.SiteDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class SiteGetWithCheckHelper(
    private val siteDao: SiteDao
) {
    /**
     * @param coAccountId CoアカウントID
     * @param siteId サイトID
     * @param siteStatuses サイトステータスのリスト
     * @return 引数のCoアカウントID、サイトID、サイトステータスに合致するSite
     * @throws CompassManagerException 条件に合致するエンティティが取得できなかったとき
     */
    fun getSiteWithCheck(coAccountId: CoAccountId, siteId: SiteId, siteStatuses: Collection<SiteStatus>): Site =
        siteDao
            .selectByIdAndStatuses(siteId, siteStatuses)
            ?.takeIf { it.coAccountId == coAccountId }
            ?: throw CompassManagerException(
                "サイトID:$siteId/CoアカウントID:$coAccountId/サイトステータス：$siteStatuses" +
                    "に合致するエンティティが取得できませんでした。"
            )
}
