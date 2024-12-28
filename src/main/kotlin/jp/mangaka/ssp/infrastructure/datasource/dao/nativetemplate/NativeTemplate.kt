package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplate

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import java.time.LocalDateTime

data class NativeTemplate(
    val nativeTemplateId: NativeTemplateId,
    val coAccountId: CoAccountId?,
    val nativeTemplateName: String,
    val nativeTemplateStatus: NativeTemplateStatus,
    val platformId: PlatformId,
    val fontFamily: String,
    val bgColor: String?,
    val adAttrText: String,
    val adAttrFontColor: String,
    val adAttrPosition: AdAttrPosition,
    val ctaButtonBgColor: String,
    val ctaButtonEdgeColor: String,
    val htmlCode: String,
    val cssCode: String,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
) {
    enum class NativeTemplateStatus {
        active, archive
    }

    enum class AdAttrPosition {
        top_left, top_right, bottom_left, bottom_right
    }
}
