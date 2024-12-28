package jp.mangaka.ssp.presentation.controller.spot

import jp.mangaka.ssp.application.service.spot.SpotTagViewService
import jp.mangaka.ssp.application.service.spot.SpotTagInfoViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagView
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagInfoView
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SpotTagController(
    private val spotTagViewService: SpotTagViewService,
    private val spotTagInfoViewService: SpotTagInfoViewService
) {
    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 広告タグのView
     */
    @GetMapping("/api/spots/{spotId}/tag")
    fun get(@RequestParam coAccountId: CoAccountId, @PathVariable("spotId") spotId: SpotId): SpotTagView = spotTagViewService.getSpotTag(coAccountId, spotId)

    /**
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @return 広告枠情報のview
     */
    @GetMapping("/api/spots/{spotId}/tag/info")
    fun getInfo(@RequestParam coAccountId: CoAccountId, @PathVariable("spotId") spotId: SpotId): SpotTagInfoView = spotTagInfoViewService.getSpotTagInfo(coAccountId, spotId)
}
