package jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus

interface ReqgroupDao {
    /**
     * @param dspIds DSPIDのリスト
     * @param statuses リクエストグループステータスのリスト
     * @return 引数のDSPIDに紐づく国のリスト
     */
    fun selectReqgroupDspCountryCosByDspIdsAndStatuses(
        dspIds: Collection<DspId>,
        statuses: Collection<ReqgroupStatus>
    ): List<ReqgroupDspCountryCo>
}
