package jp.mangaka.ssp.presentation.controller.common.view

import com.nhaarman.mockito_kotlin.mock
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.struct.StructId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.PureadsType
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("RelayStructListElementViewのテスト")
private class StructSelectElementViewTest {
    companion object {
        val structId1 = StructId(1)
        val structId2 = StructId(2)
        val structId3 = StructId(3)
        val campaignId1 = CampaignId(1)
        val campaignId2 = CampaignId(2)
        val timeTargetingId1 = TimeTargetingId(1)
        val timeTargetingId2 = TimeTargetingId(2)
        val dateTime1 = LocalDateTime.of(2024, 1, 1, 0, 0, 0)
        val dateTime2 = LocalDateTime.of(2024, 1, 2, 0, 0, 0)
        val dateTime3 = LocalDateTime.of(2024, 1, 3, 0, 0, 0)
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val structs = listOf(
            StructCo(structId1, "struct1", campaignId1, StructStatus.active, timeTargetingId1, dateTime1, dateTime2, 0),
            StructCo(structId2, "struct2", campaignId2, StructStatus.stop, timeTargetingId1, dateTime2, dateTime3, 0),
            StructCo(structId3, "struct3", campaignId1, StructStatus.archive, timeTargetingId2, dateTime1, dateTime3, 0)
        )
        val campaigns = listOf(
            CampaignCo(campaignId1, mock(), "", mock(), PureadsType.bid),
            CampaignCo(campaignId2, mock(), "", mock(), PureadsType.commit)
        )

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = StructSelectElementView.of(structs, campaigns)

            assertEquals(
                listOf(
                    StructSelectElementView(
                        structId1,
                        "struct1",
                        StructStatus.active,
                        PureadsType.bid,
                        dateTime1,
                        dateTime2
                    ),
                    StructSelectElementView(
                        structId2,
                        "struct2",
                        StructStatus.stop,
                        PureadsType.commit,
                        dateTime2,
                        dateTime3
                    ),
                    StructSelectElementView(
                        structId3,
                        "struct3",
                        StructStatus.archive,
                        PureadsType.bid,
                        dateTime1,
                        dateTime3
                    )
                ),
                actual
            )
        }
    }
}
