package jp.mangaka.ssp.presentation.controller.targeting.time.view

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.presentation.controller.common.view.CountrySelectElementView

data class CountriesView(val coAccountCountryId: CountryId, val countries: List<CountrySelectElementView>)
