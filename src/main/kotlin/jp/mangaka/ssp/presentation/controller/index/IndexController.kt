package jp.mangaka.ssp.presentation.controller.index

import jp.mangaka.ssp.presentation.config.secutiry.AccountUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController {
    @GetMapping(value = ["/", "/index"])
    fun index(
        @AuthenticationPrincipal userDetail: AccountUserDetails,
        model: Model
    ): String {
        model.addAttribute("userId", userDetail.user.userId)
        model.addAttribute("mailAddress", userDetail.user.secUserMailAddress)
        return "index"
    }
}
