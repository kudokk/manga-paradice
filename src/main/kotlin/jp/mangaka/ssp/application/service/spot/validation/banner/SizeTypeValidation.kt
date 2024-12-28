package jp.mangaka.ssp.application.service.spot.validation.banner

import org.hibernate.validator.constraints.Range

data class SizeTypeValidation(
    @field:Range(min = 0, max = 65535, message = "Validation.Number.Range")
    val width: Int,
    @field:Range(min = 0, max = 65535, message = "Validation.Number.Range")
    val height: Int
)
