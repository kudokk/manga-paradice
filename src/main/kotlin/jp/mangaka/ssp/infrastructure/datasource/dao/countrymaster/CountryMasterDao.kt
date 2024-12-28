package jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster

import jp.mangaka.ssp.application.valueobject.country.CountryId

interface CountryMasterDao {
    /**
     * 全ての国を取得する.
     *
     * @return 全てのCountryMasterのリスト
     */
    fun selectAll(): List<CountryMaster>

    /**
     * 国IDに合致する国を取得する
     *
     * @param countryId 国ID
     * @return 引数の国IDに合致する CountryMaster
     */
    fun selectById(countryId: CountryId): CountryMaster?

    /**
     * 国IDに合致する国を取得する
     *
     * @param countryIds 国IDのリスト
     * @return 引数の国IDに合致する CountryMaster のリスト
     */
    fun selectByIds(countryIds: Collection<CountryId>): List<CountryMaster>
}
