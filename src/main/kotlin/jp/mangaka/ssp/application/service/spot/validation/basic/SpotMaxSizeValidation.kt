package jp.mangaka.ssp.application.service.spot.validation.basic

import org.hibernate.validator.constraints.Range

data class SpotMaxSizeValidation(
    @field:Range(min = 1, max = 65535, message = "Validation.Number.Range")
    val width: Int?,
    @field:Range(min = 1, max = 65535, message = "Validation.Number.Range")
    val height: Int?
)
