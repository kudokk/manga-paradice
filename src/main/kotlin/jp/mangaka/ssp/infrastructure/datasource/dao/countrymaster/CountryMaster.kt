package jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster

import jp.mangaka.ssp.application.valueobject.country.CountryId
import java.math.BigDecimal

data class CountryMaster(
    val countryId: CountryId,
    val name: String,
    val nameEn: String,
    val nameKr: String,
    val timeDifference: BigDecimal,
    val compassFlag: Int
) {
    /**
     * COMPASSで利用可能かどうかを判定する.
     *
     * @return 利用可能な場合は true
     */
    fun isAvailableAtCompass(): Boolean = compassFlag == 1
}
