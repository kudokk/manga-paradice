package jp.mangaka.ssp.infrastructure.datasource.dao.summary

import jp.mangaka.ssp.infrastructure.datasource.dao.summary.condition.SpotSummaryCondition
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.SpotRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo.TotalRequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.SpotSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo.TotalSummaryCo

interface SpotDailySummaryDao {
    /**
     * 引数の条件で集計したRTBの配信実績の合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果
     */
    fun selectTotalRtbSpotSummaryByCondition(condition: SpotSummaryCondition): TotalSummaryCo

    /**
     * 引数の条件で集計したRTBの配信実績の広告枠ごとの合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果のリスト
     */
    fun selectSpotRtbSpotSummaryByCondition(condition: SpotSummaryCondition): List<SpotSummaryCo>

    /**
     * 引数の条件で集計したRTBとストラクトの配信実績の合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果
     */
    fun selectTotalRtbAndStructSpotSummaryByCondition(condition: SpotSummaryCondition): TotalSummaryCo

    /**
     * 引数の条件で集計したRTBとストラクトの配信実績の広告枠ごとの合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果のリスト
     */
    fun selectSpotRtbAndStructSpotSummaryByCondition(condition: SpotSummaryCondition): List<SpotSummaryCo>

    /**
     * 引数の条件で集計したリクエスト数の合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果
     */
    fun selectTotalSpotRequestSummaryByCondition(condition: SpotSummaryCondition): TotalRequestSummaryCo

    /**
     * 引数の条件で集計したリクエスト数の広告枠ごとの合計を取得する.
     *
     * @param condition 集計条件
     * @return 集計結果のリスト
     */
    fun selectSpotRequestSummaryByCondition(condition: SpotSummaryCondition): List<SpotRequestSummaryCo>
}
