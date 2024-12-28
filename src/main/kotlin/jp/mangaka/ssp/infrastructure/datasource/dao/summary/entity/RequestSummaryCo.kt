package jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.util.exception.CompassManagerException

interface RequestResult {
    val request: Long
}

sealed class RequestSummaryCo<T : RequestSummaryCo<T>> : RequestResult {
    abstract fun add(other: T): T

    data class TotalRequestSummaryCo(
        override val request: Long
    ) : RequestSummaryCo<TotalRequestSummaryCo>() {
        /**
         * この集計結果に別の集計結果を加算する.
         *
         * @param other 集計結果
         * @return 引数の集計結果を加算した集計結果
         */
        override fun add(other: TotalRequestSummaryCo): TotalRequestSummaryCo = TotalRequestSummaryCo(
            this.request + other.request
        )

        companion object {
            /**
             * 配信実績なし
             */
            val zero = TotalRequestSummaryCo(0L)
        }
    }

    data class SpotRequestSummaryCo(
        val spotId: SpotId,
        override val request: Long
    ) : RequestSummaryCo<SpotRequestSummaryCo>() {
        /**
         * この集計結果に別の集計結果を加算する.
         *
         * @param other 集計結果
         * @return 引数の集計結果を加算した集計結果
         * @throws CompassManagerException 異なる集計対象を加算しようとしたとき
         */
        override fun add(other: SpotRequestSummaryCo): SpotRequestSummaryCo {
            if (this.spotId != other.spotId) {
                throw CompassManagerException(
                    "異なる集計対象は加算できません。this=${this.spotId}, other=${other.spotId}"
                )
            }

            return SpotRequestSummaryCo(
                this.spotId,
                this.request + other.request
            )
        }
    }

    companion object {
        /**
         * 集計結果のリストをグループ化単位でグルーピングするためのキー
         */
        val groupKeySelector: (RequestSummaryCo<*>) -> String = {
            when (it) {
                is TotalRequestSummaryCo -> "0"
                is SpotRequestSummaryCo -> "${it.spotId}"
            }
        }
    }
}

/**
 * 集計結果のリストをグループ化単位でマージする
 *
 * @param T 集計結果のクラス
 * @return 集計結果のリストをグループ化単位でマージしたリスト
 */
fun <T : RequestSummaryCo<T>> List<T>.merge() =
    groupBy(RequestSummaryCo.groupKeySelector)
        .map { es -> es.value.reduce { acc, t -> acc.add(t) } }
