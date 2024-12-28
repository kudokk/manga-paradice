package jp.mangaka.ssp.application.service.campaign

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import jp.mangaka.ssp.application.valueobject.campaign.CampaignId
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignCo.CampaignStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.campaign.CampaignDao
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("CampaignGetWithCheckHelperのテスト")
private class CampaignGetWithCheckHelperTest {
    companion object {
        val coAccountId = CoAccountId(1)
    }

    val campaignDao: CampaignDao = mock()

    val sut = spy(CampaignGetWithCheckHelper(campaignDao))

    @Nested
    @DisplayName("getCampaignsWithCheckのテスト")
    inner class GetCampaignsWithCheckTest {
        val statuses = listOf(CampaignStatus.active, CampaignStatus.stop)

        @Test
        @DisplayName("すべて条件に合致するとき")
        fun isCorrect() {
            val campaignIds = listOf(1, 2, 3, 4).map { CampaignId(it) }
            val campaigns = listOf(1, 2, 3, 4).map { campaign(it, coAccountId.value) }
            doReturn(campaigns).whenever(campaignDao).selectByIdsAndStatuses(any(), any())

            val actual = sut.getCampaignsWithCheck(coAccountId, campaignIds, statuses)

            assertEquals(campaigns, actual)
            verify(campaignDao, times(1)).selectByIdsAndStatuses(campaignIds, statuses)
        }

        @Test
        @DisplayName("ID・ステータスに合致しないストラクトが含まれているとき")
        fun isNotExistAll() {
            val campaignIds = listOf(1, 2, 3, 4, 5, 6).map { CampaignId(it) }
            val campaigns = listOf(1, 2, 3, 4).map { campaign(it, coAccountId.value) }
            doReturn(campaigns).whenever(campaignDao).selectByIdsAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getCampaignsWithCheck(coAccountId, campaignIds, statuses) }

            verify(campaignDao, times(1)).selectByIdsAndStatuses(campaignIds, statuses)
        }

        @Test
        @DisplayName("Coアカウントに紐づかないストラクトが含まれているとき")
        fun isNotRelateCoAccount() {
            val campaignIds = listOf(1, 2, 3, 4, 5).map { CampaignId(it) }
            val campaigns = listOf(1, 2, 3, 4).map { campaign(it, coAccountId.value) } + campaign(5, 2)
            doReturn(campaigns).whenever(campaignDao).selectByIdsAndStatuses(any(), any())

            assertThrows<CompassManagerException> { sut.getCampaignsWithCheck(coAccountId, campaignIds, statuses) }

            verify(campaignDao, times(1)).selectByIdsAndStatuses(campaignIds, statuses)
        }
    }

    private fun campaign(campaignId: Int, coAccountId: Int): CampaignCo = mock {
        on { this.campaignId } doReturn CampaignId(campaignId)
        on { this.coAccountId } doReturn CoAccountId(coAccountId)
    }
}
