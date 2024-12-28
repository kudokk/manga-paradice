package jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup

import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.application.valueobject.reqgroup.ReqgroupId

data class ReqgroupDspCountryCo(
    val reqgroupId: ReqgroupId,
    val dspId: DspId,
    val countryId: CountryId
)
