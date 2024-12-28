package jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.jetbrains.annotations.TestOnly
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

/*
 * 管理画面では PC か SP のみ利用なのでplatform_idの絞り込みを行っている
 */
@Repository
class SizeTypeInfoDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val coreMasterJdbc: JdbcWrapper,
    @Autowired @Qualifier("CompassMasterJdbc") private val compassMasterJdbc: JdbcWrapper,
) : SizeTypeInfoDao {
    override fun selectStandards(): List<SizeTypeInfo> = coreMasterJdbc.query(
        """
            SELECT *
            FROM size_type_info
            WHERE definition_type = 'standard'
              AND platform_id IN (1, 2)
        """.trimIndent(),
        SizeTypeInfo::class
    )

    override fun selectUserDefinedsByCoAccountId(coAccountId: CoAccountId): List<SizeTypeInfo> {
        // クリエイティブと広告枠で利用があるサイズを取得
        val sizeTypeIds = listOf(
            selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId),
            selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId)
        ).flatten().distinct()

        if (sizeTypeIds.isEmpty()) return emptyList()

        return coreMasterJdbc.query(
            """
                SELECT *
                FROM size_type_info
                WHERE size_type_id IN (:sizeTypeIds)
                  AND definition_type = 'userdefined'
                  AND platform_id IN (1, 2)
            """.trimIndent(),
            CustomMapSqlParameterSource("sizeTypeIds", sizeTypeIds),
            SizeTypeInfo::class
        )
    }

    override fun selectBySpotId(spotId: SpotId): List<SizeTypeInfo> {
        // 広告枠で選択しているサイズを取得
        val sizeTypeIds = selectSizeTypeIdsBySpotId(spotId)

        return selectByIds(sizeTypeIds.map { SizeTypeId(it) })
    }

    override fun selectByIds(sizeTypeIds: Collection<SizeTypeId>): List<SizeTypeInfo> {
        if (sizeTypeIds.isEmpty()) return emptyList()

        return coreMasterJdbc.query(
            """
                SELECT *
                FROM size_type_info
                WHERE size_type_id IN (:sizeTypeIds)
                  AND platform_id IN (1, 2)
            """.trimIndent(),
            CustomMapSqlParameterSource("sizeTypeIds", sizeTypeIds),
            SizeTypeInfo::class
        )
    }

    override fun selectByPlatformId(platformId: PlatformId): List<SizeTypeInfo> = coreMasterJdbc.query(
        """
            SELECT *
            FROM size_type_info
            WHERE platform_id = :platformId
        """.trimIndent(),
        CustomMapSqlParameterSource("platformId", platformId),
        SizeTypeInfo::class
    )

    @TestOnly
    fun selectSizeTypeIdsUsingCreativeByCoAccountId(coAccountId: CoAccountId): List<Int> = coreMasterJdbc.query(
        """
            SELECT size_type_id
            FROM creative
            WHERE co_account_id = :coAccountId
        """.trimIndent(),
        CustomMapSqlParameterSource("coAccountId", coAccountId),
        Int::class
    )

    @TestOnly
    fun selectSizeTypeIdsBySpotId(spotId: SpotId): List<Int> = compassMasterJdbc.query(
        """
            SELECT size_type_id
            FROM relay_spot_sizetype 
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        Int::class
    )

    @TestOnly
    fun selectSizeTypeIdsUsingSpotByCoAccountId(coAccountId: CoAccountId): List<Int> = compassMasterJdbc.query(
        """
            SELECT size_type_id
            FROM relay_spot_sizetype rss
              INNER JOIN spot sp ON rss.spot_id = sp.spot_id
              INNER JOIN site si ON sp.site_id = si.site_id AND si.co_account_id = :coAccountId
        """.trimIndent(),
        CustomMapSqlParameterSource("coAccountId", coAccountId),
        Int::class
    )

    override fun bulkInsert(sizeTypeInfos: List<SizeTypeInfoInsert>): List<SizeTypeId> {
        if (sizeTypeInfos.isEmpty()) return emptyList()

        val queryHead = """
            INSERT INTO size_type_info (width, height, platform_id, definition_type, create_time)
            VALUES
        """.trimIndent()

        // クエリにマッピング、ついでにパラメータも初期化
        val params = CustomMapSqlParameterSource()
        val queryParams = sizeTypeInfos.mapIndexed { i, elm ->
            params
                .addValue("width$i", elm.width)
                .addValue("height$i", elm.height)
                .addValue("platform_id$i", elm.platformId)

            "(:width$i, :height$i, :platform_id$i, 'userdefined', NOW())"
        }.joinToString(separator = "\n,")

        return coreMasterJdbc
            .bulkInsertAndReturnResult("$queryHead\n$queryParams", params)
            .getIds { SizeTypeId(it.toInt()) }
    }
}
