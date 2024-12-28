package jp.mangaka.ssp.presentation.controller.spot

import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.service.spot.SpotService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.SessionUtils
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.view.SpotCreateResultView
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SpotPersistController(
    private val spotService: SpotService,
    private val session: HttpSession
) {
    @PostMapping("/api/spots")
    fun create(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @RequestBody form: SpotCreateForm
    ): SpotCreateResultView = spotService.create(coAccountId, SessionUtils.getUserType(coAccountId, session), form)

    @PostMapping("/api/spots/{spotId}/basic")
    fun editBasic(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
        @RequestBody form: SpotBasicEditForm
    ) {
        spotService.editBasic(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session), form)
    }

    @PostMapping("/api/spots/{spotId}/dsps")
    fun editDsps(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
        @RequestBody form: SpotDspEditForm
    ) {
        spotService.editDsp(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session), form)
    }

    @PostMapping("/api/spots/{spotId}/banner")
    fun editBanner(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
        @RequestBody form: SpotBannerEditForm
    ) {
        spotService.editBanner(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session), form)
    }

    @PostMapping("/api/spots/{spotId}/native")
    fun editNative(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
        @RequestBody form: SpotNativeEditForm
    ) {
        spotService.editNative(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session), form)
    }

    @PostMapping("/api/spots/{spotId}/video")
    fun editNative(
        @RequestParam("coAccountId") coAccountId: CoAccountId,
        @PathVariable spotId: SpotId,
        @RequestBody form: SpotVideoEditForm
    ) {
        spotService.editVideo(coAccountId, spotId, SessionUtils.getUserType(coAccountId, session), form)
    }
}
