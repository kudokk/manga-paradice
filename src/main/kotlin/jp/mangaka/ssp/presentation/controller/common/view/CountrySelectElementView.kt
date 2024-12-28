package jp.mangaka.ssp.presentation.controller.common.view

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster

// 国選択の要素を表すView
data class CountrySelectElementView(
    val countryId: CountryId,
    val countryNameJa: String,
    val countryNameEn: String,
    val countryNameKr: String
) {
    companion object {
        /**
         * ファクトリ関数
         *
         * @param country 国のエンティティ
         * @return 生成した CountrySelectElementView のインスタンス
         */
        fun of(country: CountryMaster): CountrySelectElementView =
            CountrySelectElementView(country.countryId, country.name, country.nameEn, country.nameKr)

        /**
         * ファクトリ関数
         *
         * @param countries 国のエンティティのリスト
         * @return 生成した CountrySelectElementView のインスタンスのリスト
         */
        fun of(countries: Collection<CountryMaster>): List<CountrySelectElementView> =
            countries.map { of(it) }
    }
}
