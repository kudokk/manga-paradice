package jp.mangaka.ssp.application.valueobject.role

import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.util.exception.CompassManagerException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("RoleIdのテスト")
private class RoleIdTest {
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("toUserTypeのテスト")
    inner class ToUserTypeTest {
        @ParameterizedTest
        @MethodSource("correctParams")
        @DisplayName("正常")
        fun isCorrect(roleId: Int, expected: UserType) {
            assertEquals(expected, RoleId(roleId).toUserType())
        }

        private fun correctParams() = listOf(
            Arguments.of(1, UserType.ma_staff),
            Arguments.of(2, UserType.agency),
            Arguments.of(3, UserType.client),
            Arguments.of(99, UserType.other)
        )

        @Test
        @DisplayName("不正なロールID")
        fun isInvalid() {
            assertThrows<CompassManagerException> { RoleId(999).toUserType() }
        }
    }

    @Nested
    @DisplayName("コンストラクタのテスト")
    inner class ConstructorTest {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, Int.MAX_VALUE])
        @DisplayName("正常")
        fun isCorrect(value: Int) {
            assertEquals(value, RoleId(value).value)
        }

        @ParameterizedTest
        @ValueSource(ints = [-1, Int.MIN_VALUE])
        @DisplayName("負数のとき")
        fun isNegative(value: Int) {
            assertThrows<IllegalArgumentException> { RoleId(value) }
        }
    }

    @Test
    @DisplayName("ofのテスト")
    fun testOf() {
        assertEquals(234, RoleId.of(234).value)
    }
}
