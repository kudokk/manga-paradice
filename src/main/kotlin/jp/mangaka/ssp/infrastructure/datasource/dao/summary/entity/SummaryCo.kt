package jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.util.exception.CompassManagerException
import java.math.BigDecimal

interface DeliveryResult {
    val impression: Long
    val click: Long
    val gross: BigDecimal
}

sealed class SummaryCo<T : SummaryCo<T>> : DeliveryResult {
    abstract fun add(other: T): T

    data class TotalSummaryCo(
        override val impression: Long,
        override val click: Long,
        override val gross: BigDecimal
    ) : SummaryCo<TotalSummaryCo>() {
        /**
         * この集計結果に別の集計結果を加算する.
         *
         * @param other 集計結果
         * @return 引数の集計結果を加算した集計結果
         */
        override fun add(other: TotalSummaryCo): TotalSummaryCo = TotalSummaryCo(
            this.impression + other.impression,
            this.click + other.click,
            this.gross.add(other.gross)
        )

        companion object {
            /**
             * 配信実績なし
             */
            val zero: TotalSummaryCo = TotalSummaryCo(0, 0, BigDecimal.ZERO.setScale(8))
        }
    }

    data class SpotSummaryCo(
        val spotId: SpotId,
        override val impression: Long,
        override val click: Long,
        override val gross: BigDecimal
    ) : SummaryCo<SpotSummaryCo>() {
        /**
         * この集計結果に別の集計結果を加算する.
         *
         * @param other 集計結果
         * @return 引数の集計結果を加算した集計結果
         * @throws CompassManagerException 異なる集計対象を加算しようとしたとき
         */
        override fun add(other: SpotSummaryCo): SpotSummaryCo {
            if (this.spotId != other.spotId) {
                throw CompassManagerException(
                    "異なる集計対象は加算できません。this=${this.spotId}, other=${other.spotId}"
                )
            }

            return SpotSummaryCo(
                this.spotId,
                this.impression + other.impression,
                this.click + other.click,
                this.gross.add(other.gross)
            )
        }
    }

    companion object {
        /**
         * 集計結果のリストをグループ化単位でグルーピングするためのキー
         */
        val groupKeySelector: (SummaryCo<*>) -> String = {
            when (it) {
                is TotalSummaryCo -> "0"
                is SpotSummaryCo -> "${it.spotId}"
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
fun <T : SummaryCo<T>> List<T>.merge(): List<T> =
    groupBy(SummaryCo.groupKeySelector)
        .map { es -> es.value.reduce { acc, t -> acc.add(t) } }
