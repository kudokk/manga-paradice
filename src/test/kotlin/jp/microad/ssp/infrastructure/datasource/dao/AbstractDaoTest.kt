package jp.mangaka.ssp.infrastructure.datasource.dao

import jp.mangaka.ssp.application.valueobject.IdValueObjectConverterFactory
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.support.DefaultConversionService

/**
 * Daoのテストの規定クラス
 */
abstract class AbstractDaoTest {
    init {
        // 下記はWebMvcConfigで登録されていて、Daoのテストでは有効にならないので基底クラスで登録
        (DefaultConversionService.getSharedInstance() as ConverterRegistry).addConverterFactory(
            IdValueObjectConverterFactory()
        )
    }
}
