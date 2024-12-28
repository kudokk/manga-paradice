package jp.mangaka.ssp.infrastructure.datasource.dao.summary

import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.RequestSummaryCo
import jp.mangaka.ssp.infrastructure.datasource.dao.summary.entity.SummaryCo
import jp.mangaka.ssp.util.exception.CompassManagerException
import kotlin.reflect.KClass

object SummaryUtils {
    /**
     * 集計結果のクラスからSELECT句のグループ化対象部分を生成する
     *
     * @param resultType 集計結果のクラス
     * @return 生成したSELECT句のグループ化対象部分
     */
    fun makeSelectCondition(resultType: KClass<*>): List<String> = when (resultType) {
        SummaryCo.TotalSummaryCo::class, RequestSummaryCo.TotalRequestSummaryCo::class -> emptyList()
        SummaryCo.SpotSummaryCo::class, RequestSummaryCo.SpotRequestSummaryCo::class -> listOf("spot_id")
        else -> throw CompassManagerException("未定義のクラスです。resultType：$resultType")
    }.map { "  $it," }

    /**
     * 集計結果のクラスからGROUP BY句を生成する
     *
     * @param resultType 集計結果のクラス
     * @return 生成したGROUP BY句
     */
    fun makeGroupByCondition(resultType: KClass<*>): String? = when (resultType) {
        SummaryCo.TotalSummaryCo::class, RequestSummaryCo.TotalRequestSummaryCo::class -> null
        SummaryCo.SpotSummaryCo::class, RequestSummaryCo.SpotRequestSummaryCo::class -> "spot_id"
        else -> throw CompassManagerException("未定義のクラスです。resultType：$resultType")
    }?.let { "GROUP BY $it" }
}
