package jp.mangaka.ssp.presentation.controller.spot

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.servlet.http.HttpSession
import jp.mangaka.ssp.application.service.spot.SpotService
import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.SessionUtils
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.view.SpotCreateResultView
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import io.mockk.verify as verifyK

@DisplayName("SpotControllerのテスト")
private class SpotPersistControllerTest {
    companion object {
        val coAccountId = CoAccountId(1)
        val spotId = SpotId(1)
    }

    val spotService: SpotService = mock()
    val session: HttpSession = mock()

    val sut = spy(SpotPersistController(spotService, session))

    @BeforeEach
    fun beforeEach() {
        mockkObject(SessionUtils)
        every { SessionUtils.getUserType(any(), any()) } returns UserType.ma_staff
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("createのテスト")
    inner class CreateTest {
        val form: SpotCreateForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val spotCreateResultView = SpotCreateResultView(SpotId(1))
            doReturn(spotCreateResultView).whenever(spotService).create(any(), any(), any())

            sut.create(coAccountId, form)

            verify(spotService, times(1)).create(coAccountId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Nested
    @DisplayName("editBasicのテスト")
    inner class EditBasicTest {
        val form: SpotBasicEditForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editBasic(coAccountId, spotId, form)

            verify(spotService, times(1)).editBasic(coAccountId, spotId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Nested
    @DisplayName("editDspsのテスト")
    inner class EditDspsTest {
        val form: SpotDspEditForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editDsps(coAccountId, spotId, form)

            verify(spotService, times(1)).editDsp(coAccountId, spotId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Nested
    @DisplayName("editBannerのテスト")
    inner class EditBannerTest {
        val form: SpotBannerEditForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editBanner(coAccountId, spotId, form)

            verify(spotService, times(1)).editBanner(coAccountId, spotId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Nested
    @DisplayName("editNativeのテスト")
    inner class EditNativeTest {
        val form: SpotNativeEditForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editNative(coAccountId, spotId, form)

            verify(spotService, times(1)).editNative(coAccountId, spotId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }

    @Nested
    @DisplayName("editVideoのテスト")
    inner class EditVideoTest {
        val form: SpotVideoEditForm = mock()

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            sut.editNative(coAccountId, spotId, form)

            verify(spotService, times(1)).editVideo(coAccountId, spotId, UserType.ma_staff, form)
            verifyK { SessionUtils.getUserType(coAccountId, session) }
        }
    }
}
