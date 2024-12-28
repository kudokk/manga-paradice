package jp.mangaka.ssp.application.service.spot.validation

import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.spot.validation.basic.BasicSettingEditValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfo
import jp.mangaka.ssp.infrastructure.datasource.dao.spot.Spot
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.BasicSettingEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.mockk.verify as verifyK

@DisplayName("SpotBasicEditValidationのテスト")
private class SpotBasicEditValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator

    @Nested
    @DisplayName("基本設定のテスト")
    inner class BasicTest {
        @Test
        @DisplayName("@Validが反応しているか")
        fun isValid() {
            val basic = BasicSettingEditValidation(null, null, null, null, emptyList())

            validator.validate(SpotBasicEditValidation(basic)).run {
                assertTrue(any { it.propertyPath.toString().startsWith("basic.") })
            }
        }
    }

    @Nested
    @DisplayName("ファクトリ関数のテスト")
    inner class FactoryTest {
        val basicForm: BasicSettingEditForm = mock()
        val form = SpotBasicEditForm(basicForm, mock())
        val spot: Spot = mock()
        val sizeTypes: List<SizeTypeInfo> = mock()
        val basicValidation: BasicSettingEditValidation = mock()

        @BeforeEach
        fun beforeEach() {
            mockkObject(BasicSettingEditValidation)
            every { BasicSettingEditValidation.of(any(), any(), any(), any(), any()) } returns basicValidation
        }

        @AfterEach
        fun afterEach() {
            unmockkAll()
        }

        @Test
        @DisplayName("正常")
        fun isCorrect() {
            val actual = SpotBasicEditValidation.of(form, spot, UserType.ma_staff, true, sizeTypes)

            assertEquals(SpotBasicEditValidation(basicValidation), actual)

            verifyK { BasicSettingEditValidation.of(basicForm, spot, UserType.ma_staff, true, sizeTypes) }
        }
    }
}
