package jp.mangaka.ssp.presentation.common.summary

import java.time.LocalDate

sealed class SummaryRequest {
    abstract val isExpectedRevenue: Boolean
    abstract val isTaxIncluded: Boolean

    data class ListView(
        override val isExpectedRevenue: Boolean,
        override val isTaxIncluded: Boolean,
        val termType: TermType,
        val startDate: LocalDate?,
        val endDate: LocalDate?
    ) : SummaryRequest() {
        enum class TermType {
            today,
            yesterday,
            thisWeek,
            lastWeek,
            thisMonth,
            lastMonth,
            last7days,
            last30days,
            custom
        }
    }

    data class Csv(
        override val isExpectedRevenue: Boolean,
        override val isTaxIncluded: Boolean,
        val startDate: LocalDate,
        val endDate: LocalDate
    ) : SummaryRequest()
}
