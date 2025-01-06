package com.manga.paradice.presentation.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping




@Controller
class indexController(
) {
    /**
     * ブラウザからのリロードがあったときに404になる対策
     */
    @GetMapping(
        value = [
            "/",
            "/about",
            "/manga/{mangaId:^[0-9]+$}/detail",
            "/manga/{mangaId:^[0-9]+$}/{chapterId:^[0-9]+$}/views"
        ])
    fun urlRewrite(): String {
        return "index"
    }

    /**
     * エラーページへのアクセスは、Vueに任せる
     */
    @GetMapping(value = ["error/500", "error/404"])
    fun errorUrl() { "redirect:/" }
}