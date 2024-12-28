package jp.mangaka.ssp.infrastructure.datasource.dao.timetargeting

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.country.CountryId
import jp.mangaka.ssp.application.valueobject.targeting.time.TimeTargetingId
import jp.mangaka.ssp.util.exception.CompassManagerException
import java.time.LocalDateTime

data class TimeTargeting(
    val timeTargetingId: TimeTargetingId,
    val coAccountId: CoAccountId,
    val timeTargetingName: String,
    val timeTargetingStatus: TimeTargetingStatus,
    private val country_id: CountryId?,
    val isActiveHoliday: Boolean,
    val description: String?,
    val updateTime: LocalDateTime
) {
    // 定義上は nullable だが、アプリ上では null を想定していないので弾く
    val countryId: CountryId = country_id
        ?: throw CompassManagerException("タイムターゲティング:${timeTargetingId}のcountry_idにnullが設定されています。")

    enum class TimeTargetingStatus {
        active, archive, deleted;

        /**
         * 指定のステータスへの変更が許可されているかどうかを判定します.
         *
         * @param next 遷移先のステータス
         * @return 変更が許可されている場合は true
         */
        fun isAllowedChange(next: TimeTargetingStatus): Boolean {
            // 未変更
            if (this == next) return true

            return when (this) {
                // アーカイブに変更可
                active -> next == archive
                // アクティブ・削除に変更可
                archive -> next == active || next == deleted
                // 変更不可
                deleted -> false
            }
        }

        /**
         * 指定のステータスへの変更が許可されていることをチェックします.
         *
         * @param next 遷移先のステータス
         * @throws CompassManagerException 許可されていないステータス遷移の場合
         */
        fun checkAllowedChange(next: TimeTargetingStatus) {
            if (isAllowedChange(next)) return

            throw CompassManagerException("タイムターゲティングステータスを $this から $next に変更することはできません。")
        }

        companion object {
            /** 閲覧可能なステータスのリスト */
            val viewableStatuses = listOf(active, archive)
        }
    }
}
