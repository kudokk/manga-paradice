package jp.mangaka.ssp.infrastructure.datasource.dao.site

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.proprietydsp.ProprietyDspId
import jp.mangaka.ssp.application.valueobject.site.SiteId
import java.time.LocalDateTime

data class Site(
    val siteId: SiteId,
    val coAccountId: CoAccountId,
    val siteName: String,
    val siteStatus: SiteStatus,
    val isAllowedMacroUrl: Boolean,
    val platformId: PlatformId,
    val siteType: SiteType,
    val defaultProprietyDspId: ProprietyDspId,
    val updateTime: LocalDateTime
) {
    enum class SiteStatus {
        active, archive, requested, ng;

        companion object {
            /** アーカイブ以外のステータスのリスト */
            val nonArchiveStatuses = listOf(active, requested, ng)
        }
    }

    enum class SiteType {
        pc_web, sp_web, i_app, a_app;

        /**
         * @return pc_web またはsp_web のとき true
         */
        fun isWeb(): Boolean = this == pc_web || this == sp_web

        /**
         * @return i_app または a_app のとき true
         */
        fun isApp(): Boolean = this == i_app || this == a_app
    }
}
