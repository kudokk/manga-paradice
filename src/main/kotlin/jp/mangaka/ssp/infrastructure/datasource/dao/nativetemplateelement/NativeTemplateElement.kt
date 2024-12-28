package jp.mangaka.ssp.infrastructure.datasource.dao.nativetemplateelement

import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeElementId
import jp.mangaka.ssp.application.valueobject.nativetemplate.NativeTemplateId
import java.time.LocalDateTime

data class NativeTemplateElement(
    val nativeTemplateId: NativeTemplateId,
    val nativeElementId: NativeElementId,
    val maxLength: Int?,
    val width: Int?,
    val height: Int?,
    val fontSize: Int?,
    val fontColor: String?,
    val trimType: TrimType,
    val viewType: ViewType,
    val requiredFlag: RequiredFlag,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
) {
    enum class TrimType {
        dot, chunk
    }

    enum class ViewType {
        minify, cut
    }

    enum class RequiredFlag {
        required, optional
    }
}
