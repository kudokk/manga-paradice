package jp.mangaka.ssp.presentation.controller.spot

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.service.spot.SpotTagInfoViewService
import jp.mangaka.ssp.application.service.spot.SpotTagViewService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagInfoView
import jp.mangaka.ssp.presentation.controller.spot.view.SpotTagView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SpotTagControllerのテスト")
private class SpotTagControllerTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val spotId = SpotId(1)
    }

    val spotTagViewService: SpotTagViewService = mock()
    val spotTagInfoViewService: SpotTagInfoViewService = mock()

    val sut = SpotTagController(spotTagViewService, spotTagInfoViewService)

    @Nested
    @DisplayName("getSitesのテスト")
    inner class GetSpotTagTest {
        val spotTagView: SpotTagView = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotTagView).whenever(spotTagViewService).getSpotTag(coAccountId, spotId)

            val actual = sut.get(coAccountId, spotId)

            assertEquals(spotTagView, actual)
            verify(spotTagViewService, times(1)).getSpotTag(coAccountId, spotId)
        }
    }

    @Nested
    @DisplayName("getSpotTagInfoのテスト")
    inner class GetSpotTagInfo {
        val spotTagInfoView: SpotTagInfoView = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            doReturn(spotTagInfoView).whenever(spotTagInfoViewService).getSpotTagInfo(coAccountId, spotId)

            val actual = sut.getInfo(coAccountId, spotId)

            assertEquals(spotTagInfoView, actual)
            verify(spotTagInfoViewService, times(1)).getSpotTagInfo(coAccountId, spotId)
        }
    }
}
