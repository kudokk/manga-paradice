package jp.mangaka.ssp.application.service.targeting.time.validation

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import jakarta.validation.Validation
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingCreateValidation
import jp.mangaka.ssp.application.service.targeting.time.validation.BasicSettingValidation.BasicSettingEditValidation
import jp.mangaka.ssp.infrastructure.datasource.dao.countrymaster.CountryMaster
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo
import jp.mangaka.ssp.infrastructure.datasource.dao.struct.StructCo.StructStatus
import jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting.TimeTargeting.TimeTargetingStatus
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingCreateForm
import jp.mangaka.ssp.presentation.controller.targeting.time.form.BasicSettingForm.BasicSettingEditForm
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.apache.commons.lang3.RandomStringUtils.random as randomString

@DisplayName("BasicSettingValidationのテスト")
private class BasicSettingValidationTest {
    val validator = Validation.buildDefaultValidatorFactory().validator!!

    @Nested
    @DisplayName("作成のテスト")
    inner class CreateTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("タイムターゲティング名のテスト")
        inner class TimeTargetingNameTest {
            @ParameterizedTest
            @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
            @DisplayName("未入力のとき")
            fun isBlank(value: String?) {
                validator.validate(sut(value)).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            @Test
            @DisplayName("文字数超過")
            fun isTooLong() {
                validator.validate(sut(randomString(256))).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [1, 255])
            @DisplayName("正常")
            fun isValid(length: Int) {
                validator.validate(sut(randomString(length))).run {
                    assertTrue(none { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            private fun sut(timeTargetingName: String?) = BasicSettingCreateValidation(timeTargetingName, "", mock())
        }

        @Nested
        @DisplayName("国のテスト")
        inner class CountryIdTest {
            val country: CountryMaster = mock()
            val sut = BasicSettingCreateValidation("", "", country)

            @Test
            @DisplayName("利用できない国のとき")
            fun isUnavailableCountry() {
                doReturn(false).whenever(country).isAvailableAtCompass()

                validator.validate(sut).run {
                    assertTrue(any { it.propertyPath.toString() == "countryId" })
                }
            }

            @Test
            @DisplayName("正常")
            fun isValid() {
                doReturn(true).whenever(country).isAvailableAtCompass()

                validator.validate(sut).run {
                    assertTrue(none { it.propertyPath.toString() == "countryId" })
                }
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("備考のテスト")
        inner class DescriptionTest {
            @ParameterizedTest
            @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
            @DisplayName("未入力のとき")
            fun isBlank(value: String?) {
                validator.validate(sut(value)).run {
                    assertTrue(none { it.propertyPath.toString() == "description" })
                }
            }

            @Test
            @DisplayName("文字数超過")
            fun isTooLong() {
                validator.validate(sut(randomString(1024))).run {
                    assertTrue(any { it.propertyPath.toString() == "description" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [1, 1023])
            @DisplayName("正常")
            fun isValid(length: Int) {
                validator.validate(sut(randomString(length))).run {
                    assertTrue(none { it.propertyPath.toString() == "description" })
                }
            }

            private fun sut(description: String?) = BasicSettingCreateValidation("", description, mock())
        }

        @Nested
        @DisplayName("ファクトリ関数のテスト")
        inner class FactoryTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val form: BasicSettingCreateForm = mock {
                    on { this.timeTargetingName } doReturn null
                    on { this.description } doReturn "desc"
                }

                validator.validate(BasicSettingCreateValidation.of(form, mock())).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }
        }
    }

    @Nested
    @DisplayName("編集のテスト")
    inner class EditTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("タイムターゲティング名のテスト")
        inner class TimeTargetingNameTest {
            @ParameterizedTest
            @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
            @DisplayName("未入力のとき")
            fun isBlank(value: String?) {
                validator.validate(sut(value)).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            @Test
            @DisplayName("文字数超過")
            fun isTooLong() {
                validator.validate(sut(randomString(256))).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [1, 255])
            @DisplayName("正常")
            fun isValid(length: Int) {
                validator.validate(sut(randomString(length))).run {
                    assertTrue(none { it.propertyPath.toString() == "timeTargetingName" })
                }
            }

            private fun sut(timeTargetingName: String?) =
                BasicSettingEditValidation(timeTargetingName, "", mock(), TimeTargetingStatus.active, emptyList())
        }

        @Nested
        @DisplayName("タイムターゲティングステータスのテスト")
        inner class TimeTargetingStatusTest {
            val activeStructs: List<StructCo> = listOf(StructStatus.active, StructStatus.stop, StructStatus.pause)
                .map { status ->
                    mock {
                        on { this.structStatus } doReturn status
                    }
                }
            val inactiveStructs: List<StructCo> = List(3) {
                mock {
                    on { this.structStatus } doReturn StructStatus.archive
                }
            }

            @Nested
            @DisplayName("変更後のステータスがアーカイブのとき")
            inner class ChangeToArchiveTest {
                @Test
                @DisplayName("アクティブなストラクトに紐づいているとき")
                fun isRelateActiveStruct() {
                    validator.validate(sut(TimeTargetingStatus.archive, inactiveStructs + activeStructs)).run {
                        assertTrue(any { it.propertyPath.toString() == "timeTargetingStatus" })
                    }
                }

                @Test
                @DisplayName("正常")
                fun isValid() {
                    validator.validate(sut(TimeTargetingStatus.archive, inactiveStructs)).run {
                        assertTrue(none { it.propertyPath.toString() == "timeTargetingStatus" })
                    }
                }
            }

            @Nested
            @DisplayName("ステータスが未変更のとき")
            inner class NotChangeToArchiveTest {
                @Test
                @DisplayName("正常")
                fun isValid() {
                    validator.validate(sut(TimeTargetingStatus.active, activeStructs + inactiveStructs)).run {
                        assertTrue(none { it.propertyPath.toString() == "timeTargetingStatus" })
                    }
                }
            }

            private fun sut(timeTargetingStatus: TimeTargetingStatus, structs: Collection<StructCo>) =
                BasicSettingEditValidation("", "", mock(), timeTargetingStatus, structs)
        }

        @Nested
        @DisplayName("国のテスト")
        inner class CountryIdTest {
            val country: CountryMaster = mock()
            val sut = BasicSettingEditValidation("", "", country, TimeTargetingStatus.active, emptyList())

            @Test
            @DisplayName("利用できない国のとき")
            fun isUnavailableCountry() {
                doReturn(false).whenever(country).isAvailableAtCompass()

                validator.validate(sut).run {
                    assertTrue(any { it.propertyPath.toString() == "countryId" })
                }
            }

            @Test
            @DisplayName("正常")
            fun isValid() {
                doReturn(true).whenever(country).isAvailableAtCompass()

                validator.validate(sut).run {
                    assertTrue(none { it.propertyPath.toString() == "countryId" })
                }
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("備考のテスト")
        inner class DescriptionTest {
            @ParameterizedTest
            @MethodSource("jp.mangaka.ssp.util.TestUtils#emptyStrings")
            @DisplayName("未入力のとき")
            fun isBlank(value: String?) {
                validator.validate(sut(value)).run {
                    assertTrue(none { it.propertyPath.toString() == "description" })
                }
            }

            @Test
            @DisplayName("文字数超過")
            fun isTooLong() {
                validator.validate(sut(randomString(1024))).run {
                    assertTrue(any { it.propertyPath.toString() == "description" })
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [1, 1023])
            @DisplayName("正常")
            fun isValid(length: Int) {
                validator.validate(sut(randomString(length))).run {
                    assertTrue(none { it.propertyPath.toString() == "description" })
                }
            }

            private fun sut(description: String?) =
                BasicSettingEditValidation("", description, mock(), TimeTargetingStatus.active, emptyList())
        }

        @Nested
        @DisplayName("ファクトリ関数のテスト")
        inner class FactoryTest {
            @Test
            @DisplayName("正常")
            fun isCorrect() {
                val form: BasicSettingEditForm = mock {
                    on { this.timeTargetingName } doReturn null
                    on { this.description } doReturn "desc"
                    on { this.timeTargetingStatus } doReturn TimeTargetingStatus.active
                }

                validator.validate(BasicSettingEditValidation.of(form, mock(), emptyList())).run {
                    assertTrue(any { it.propertyPath.toString() == "timeTargetingName" })
                }
            }
        }
    }
}
