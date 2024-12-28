package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site.SiteType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus

data class SpotTagInfoView(
    val spotName: String,
    val spotType: String,
    val siteId: SiteId,
    val siteName: String,
    val siteType: SiteType,
    val spotStatus: SpotStatus,
    val displayType: DisplayType,
    val description: String?,
)
