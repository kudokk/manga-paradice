package jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster

import jp.mangaka.ssp.application.valueobject.dsp.DspId

interface DspMasterDao {
    /**
     * @return 利用可能な全てのDspMasterのリスト
     */
    fun selectAll(): List<DspMaster>

    /**
     * @param dspIds DSPIDのリスト
     * @return 引数のDSPIDに合致するDspMasterのリスト
     */
    fun selectByIds(dspIds: Collection<DspId>): List<DspMaster>
}
