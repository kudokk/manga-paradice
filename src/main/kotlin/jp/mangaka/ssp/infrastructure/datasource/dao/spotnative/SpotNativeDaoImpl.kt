package jp.mangaka.ssp.infrastructure.datasource.dao.spotnative

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotNativeDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotNativeDao {
    override fun insert(spotNative: SpotNativeInsert) {
        jdbcWrapper.insertExecute({ it.withTableName("spot_native") }, spotNative)
    }

    override fun selectById(spotId: SpotId): SpotNative? {
        return jdbcWrapper.queryForObject(
            """
            SELECT *
            FROM spot_native
            WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId),
            SpotNative::class
        )
    }

    override fun update(spotNative: SpotNativeUpdate) {
        jdbcWrapper.update(
            """
                UPDATE spot_native
                SET native_template_id = :nativeTemplateId
                WHERE spot_id = :spotId
            """.trimIndent(),
            spotNative
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_native
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }
}
