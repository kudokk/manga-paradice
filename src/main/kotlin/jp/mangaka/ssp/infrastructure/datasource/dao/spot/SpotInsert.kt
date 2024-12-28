package jp.mangaka.ssp.infrastructure.datasource.dao.spot

import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.infrastructure.datasource.dao.site.Site
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DeliveryMethod
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.UpstreamType
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm

data class SpotInsert(
    val siteId: SiteId,
    val spotName: String,
    val spotStatus: SpotStatus,
    val platformId: PlatformId,
    val displayType: DisplayType,
    val upstreamType: UpstreamType,
    val deliveryMethod: DeliveryMethod,
    val width: Int?,
    val height: Int?,
    val rotationMax: Int,
    val isAmp: String,
    val proprietyDspId: ProprietyDspId,
    val descriptions: String?,
    val pageUrl: String?
) {
    // 削除予定で利用がないカラムだがnon-nullのため仮の値を登録
    val spotType: String = "banner"

    companion object {
        /**
         * @param form 広告枠作成のForm
         * @param site サイト
         * @return spotのInsertオブジェクト
         */
        fun of(form: SpotCreateForm, site: Site): SpotInsert = SpotInsert(
            site.siteId,
            form.basic.spotName!!,
            form.basic.spotStatus!!,
            site.platformId,
            form.basic.displayType!!,
            form.basic.upstreamType,
            form.basic.deliveryMethod!!,
            form.basic.spotMaxSize?.width,
            form.basic.spotMaxSize?.height,
            form.video?.rotationMax ?: Spot.rotationMaxDefaultValue,
            form.basic.isAmp.toString(),
            site.defaultProprietyDspId,
            form.basic.description,
            form.basic.pageUrl
        )
    }
}
