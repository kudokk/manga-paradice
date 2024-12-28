package jp.mangaka.ssp.infrastructure.datasource.dao.compassstruct

import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId

interface CompassStructDao {
    /**
     * ストラクトのタイムターゲティング紐づけを更新する.
     *
     * @param structIds ストラクトID
     * @param timeTargetingId タイムターゲティングID
     */
    fun updateTimeTargetingId(structIds: Collection<StructId>, timeTargetingId: TimeTargetingId?)
}
