package jp.mangaka.ssp.util.localfile.valueobject

import kotlin.reflect.KClass
import jp.mangaka.ssp.util.localfile.valueobject.config.CommonConfig as CommonConfigObject

sealed class LocalFileType<T : Any> {
    abstract val filePath: String
    abstract val fileType: KClass<T>

    data object CommonConfig : LocalFileType<CommonConfigObject>() {
        override val filePath: String = "values/common-config.json"
        override val fileType: KClass<CommonConfigObject> = CommonConfigObject::class
    }
}
