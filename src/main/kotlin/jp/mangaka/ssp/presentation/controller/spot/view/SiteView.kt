package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType

data class SiteView(val siteId: SiteId, val siteName: String, val siteType: SiteType, val platformId: PlatformId) {
    companion object {
        /**
         * @param sites サイトのエンティティのリスト
         * @return サイト一覧のView
         */
        fun of(sites: Collection<Site>): List<SiteView> =
            sites.map { SiteView(it.siteId, it.siteName, it.siteType, it.platformId) }
    }
}
