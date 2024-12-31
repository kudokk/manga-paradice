package com.manga.paradice

import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class App {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            runApplication<App>(*args)
        }
    }
}