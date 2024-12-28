package jp.mangaka.ssp.util.localfile.valueobject.config

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

/**
 * システムローカルに配備された共通設定
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CommonConfig(
    /**
     * 税率の乗数（税率が 10％ なら 1.1 が設定される）
     */
    val taxRate: BigDecimal
)
