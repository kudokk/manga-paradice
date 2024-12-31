package com.manga.paradice.presentation.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping




@Controller
@RequestMapping(value = ["/login"])
class LoginController(
    private val handlerMapping: RequestMappingHandlerMapping,
    private val request: HttpServletRequest
) {

    @GetMapping
    fun loginTest(): String {
        System.out.println("======= url context path")
        System.out.println(request.contextPath)
        return "login2"
    }

    @GetMapping("/hello")
    fun hello(): String {
        return "hello/index.html"
    }

    @GetMapping("/login2")
    fun getApiLogin(): String {
        return "templates/index"
    }

    @GetMapping("/hello2")
    fun getApiHello(): String {
        return "templates/index.html"
    }

    @GetMapping("/map")
    fun getMap(
        model: Model
    ): String {
        System.out.println(model)
        System.out.println("======= url mapping")
        this.handlerMapping.getHandlerMethods().let {
            for ((key, value) in it) {
                System.out.println("======= " + key.methodsCondition + " " + key.paramsCondition + " " + key.directPaths + " :: " + value)
            }
        }
        return "login2.html"
    }
}