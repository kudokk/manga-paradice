package jp.mangaka.ssp.presentation.controller.spot.form

import jp.mangaka.ssp.application.valueobject.currency.CurrencyId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot

data class BasicSettingCreateForm(
    val siteId: SiteId?,
    val spotName: String?,
    val spotStatus: Spot.SpotStatus?,
    val upstreamType: Spot.UpstreamType,
    val currencyId: CurrencyId?,
    val deliveryMethod: Spot.DeliveryMethod?,
    val displayType: Spot.DisplayType?,
    val isDisplayControl: Boolean,
    val isAmp: Boolean,
    val spotMaxSize: SpotMaxSizeForm?,
    val description: String?,
    val pageUrl: String?
)
