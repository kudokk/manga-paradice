package jp.mangaka.ssp.infrastructure.datasource.dao.site

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteStatus

interface SiteDao {
    /**
     * @param siteId サイトID
     * @param statuses サイトステータス
     * @return 引数のサイトID・ステータスに合致するSite
     */
    fun selectByIdAndStatuses(siteId: SiteId, statuses: Collection<SiteStatus>): Site?

    /**
     * @param coAccountId CoアカウントID
     * @param statuses サイトステータス
     * @return 引数のCoアカウントID・ステータスに合致するSiteのリスト
     */
    fun selectByCoAccountIdAndStatuses(coAccountId: CoAccountId, statuses: Collection<SiteStatus>): List<Site>
}
