package jp.mangaka.ssp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CompassManagerApplication

fun main(args: Array<String>) {
    runApplication<CompassManagerApplication>(*args)
}
