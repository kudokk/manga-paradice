package jp.mangaka.ssp.presentation.controller.spot.view.list

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.site.SiteId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.DisplayType
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot.SpotStatus
import jp.mangaka.ssp.presentation.common.summary.SummaryView
import jp.mangaka.ssp.presentation.controller.spot.view.DeliveryFormatsView
import jp.mangaka.ssp.presentation.controller.spot.view.SiteView
import jp.mangaka.ssp.presentation.controller.spot.view.SizeTypeInfoView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("SpotListElementViewのテスト")
private class SpotListElementViewTest {
    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val spots = listOf(
            spot(1, 10, "spot1", SpotStatus.active, DisplayType.inline, "2024-01-01T00:00:00"),
            spot(2, 10, "spot2", SpotStatus.standby, DisplayType.overlay, "2024-01-02T00:00:00"),
            spot(3, 11, "spot3", SpotStatus.archive, DisplayType.interstitial, "2024-01-03T00:00:00")
        )
        val siteViews = listOf(10, 11).map { SiteId(it) to mock<SiteView>() }
        val sizeTypeInfoViews = listOf(20, 21, 22).map { SizeTypeId(it) to mock<SizeTypeInfoView>() }
        val deliveryFormatsViews = listOf(1, 2, 3).map { SpotId(it) to mock<DeliveryFormatsView>() }
        val summaryViews = listOf(1, 2, 3).map { SpotId(it) to mock<SummaryView>() }
        val spotSizeTypesMap = mapOf(
            SpotId(1) to listOf(20, 21, 22).map { SizeTypeId(it) },
            SpotId(2) to listOf(20, 22).map { SizeTypeId(it) },
            SpotId(3) to listOf(21, 22).map { SizeTypeId(it) }
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = SpotListElementView.of(
                spots,
                spotSizeTypesMap,
                siteViews.toMap(),
                sizeTypeInfoViews.toMap(),
                deliveryFormatsViews.toMap(),
                summaryViews.toMap()
            )

            assertEquals(
                listOf(
                    SpotListElementView(
                        SpotId(1),
                        "spot1",
                        SpotStatus.active,
                        siteViews[0].second,
                        DisplayType.inline,
                        listOf(0, 1, 2).map { sizeTypeInfoViews[it].second },
                        deliveryFormatsViews[0].second,
                        summaryViews[0].second,
                        LocalDateTime.parse("2024-01-01T00:00:00")
                    ),
                    SpotListElementView(
                        SpotId(2),
                        "spot2",
                        SpotStatus.standby,
                        siteViews[0].second,
                        DisplayType.overlay,
                        listOf(0, 2).map { sizeTypeInfoViews[it].second },
                        deliveryFormatsViews[1].second,
                        summaryViews[1].second,
                        LocalDateTime.parse("2024-01-02T00:00:00")
                    ),
                    SpotListElementView(
                        SpotId(3),
                        "spot3",
                        SpotStatus.archive,
                        siteViews[1].second,
                        DisplayType.interstitial,
                        listOf(1, 2).map { sizeTypeInfoViews[it].second },
                        deliveryFormatsViews[2].second,
                        summaryViews[2].second,
                        LocalDateTime.parse("2024-01-03T00:00:00")
                    )
                ),
                actual
            )
        }
    }

    private fun spot(
        spotId: Int,
        siteId: Int,
        spotName: String,
        spotStatus: SpotStatus,
        displayType: DisplayType,
        updateTime: String
    ): Spot = mock {
        on { this.spotId } doReturn SpotId(spotId)
        on { this.siteId } doReturn SiteId(siteId)
        on { this.spotName } doReturn spotName
        on { this.spotStatus } doReturn spotStatus
        on { this.displayType } doReturn displayType
        on { this.updateTime } doReturn LocalDateTime.parse(updateTime)
    }
}
