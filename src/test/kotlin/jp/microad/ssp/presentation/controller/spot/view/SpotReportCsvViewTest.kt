package jp.mangaka.ssp.presentation.controller.spot.view

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotReportCsvViewのテスト")
private class SpotReportCsvViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spots = listOf(
            spot(20, "spot20", 10),
            spot(21, "spot21", 10),
            spot(22, "spot22", 11)
        )
        val sites = listOf(
            site(10, "サイト10"),
            site(11, "サイト11")
        )
        val siteMap = sites.associateBy { it.siteId }
        val deliveryFormatsViews: List<DeliveryFormatsView> = listOf(mock(), mock(), mock())
        val deliveryFormatsViewsMap = spots.zip(deliveryFormatsViews).associateBy({ it.first.spotId }, { it.second })
        val summaries: List<SummaryView> = listOf(mock(), mock(), mock())
        val summaryMap = spots.zip(summaries).associateBy({ it.first.spotId }, { it.second })
        val divIds = listOf("divId20", "divId21", "divId22")
        val divIdMap = spots.zip(divIds).associateBy({ it.first.spotId }, { it.second })

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = SpotReportCsvView.of(spots, siteMap, deliveryFormatsViewsMap, summaryMap, divIdMap)

            Assertions.assertEquals(
                listOf(
                    SpotReportCsvView(
                        sites[0].siteId,
                        sites[0].siteName,
                        spots[0].spotId,
                        spots[0].spotName,
                        divIds[0],
                        deliveryFormatsViews[0],
                        summaries[0]
                    ),
                    SpotReportCsvView(
                        sites[0].siteId,
                        sites[0].siteName,
                        spots[1].spotId,
                        spots[1].spotName,
                        divIds[1],
                        deliveryFormatsViews[1],
                        summaries[1]
                    ),
                    SpotReportCsvView(
                        sites[1].siteId,
                        sites[1].siteName,
                        spots[2].spotId,
                        spots[2].spotName,
                        divIds[2],
                        deliveryFormatsViews[2],
                        summaries[2]
                    )
                ),
                actual
            )
        }
    }

    private fun site(siteId: Int, siteName: String): SiteView = mock {
        on { this.siteId } doReturn SiteId(siteId)
        on { this.siteName } doReturn siteName
    }

    private fun spot(spotId: Int, spotName: String, siteId: Int): Spot = mock {
        on { this.spotId } doReturn SpotId(spotId)
        on { this.spotName } doReturn spotName
        on { this.siteId } doReturn SiteId(siteId)
    }
}
