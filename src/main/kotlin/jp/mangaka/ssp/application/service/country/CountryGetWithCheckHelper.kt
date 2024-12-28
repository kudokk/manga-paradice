package jp.mangaka.ssp.application.service.country

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMasterDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.springframework.stereotype.Component

@Component
class CountryGetWithCheckHelper(
    private val countryMasterDao: CountryMasterDao
) {
    /**
     * 国IDに合致する国がすべて存在する場合のみ取得する.
     *
     * @param countryIds 国IDのリスト
     * @return 引数の国IDに合致する CountryMaster のリスト
     * @throws CompassManagerException 国IDに合致する国が存在しないとき
     */
    fun getCountiesWithCheck(countryIds: Collection<CountryId>): List<CountryMaster> = countryMasterDao
        .selectByIds(countryIds)
        .also { entities ->
            val notFoundIds = countryIds - entities.map { it.countryId }.toSet()

            if (notFoundIds.isNotEmpty()) {
                throw CompassManagerException(
                    "国ID：${notFoundIds}に合致するエンティティが取得できませんでした。"
                )
            }
        }

    /**
     * 国IDに合致する国が存在する場合のみ取得する.
     *
     * @param countryId 国ID
     * @return 引数の国IDに合致する CountryMaster
     * @throws CompassManagerException 国IDに合致する国が存在しないとき
     */
    fun getCountryWithCheck(countryId: CountryId): CountryMaster = countryMasterDao
        .selectById(countryId)
        ?: throw CompassManagerException("国ID：${countryId}に合致するエンティティが取得できませんでした。")
}
