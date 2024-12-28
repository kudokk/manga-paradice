package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagView

interface SpotTagViewService {
    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 広告タグのView
     */
    fun getSpotTag(coAccountId: CoAccountId, spotId: SpotId): SpotTagView
}
