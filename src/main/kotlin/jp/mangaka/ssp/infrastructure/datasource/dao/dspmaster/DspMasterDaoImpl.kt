package jp.mangaka.ssp.infrastructure.datasource.dao.dspmaster

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Repository

// reqgroup_status=activeなreqgroupに含まれているDSPのみが利用可能
@Repository
class DspMasterDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : DspMasterDao {
    override fun selectAll(): List<DspMaster> = jdbcWrapper.query(
        """
            SELECT *
            FROM dsp_master
            WHERE dsp_id IN (
              SELECT DISTINCT dsp_id
              FROM reqgroup
              WHERE reqgroup_status = 'active'
            )
        """.trimIndent(),
        DspMaster::class
    )

    override fun selectByIds(dspIds: Collection<DspId>): List<DspMaster> {
        if (dspIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM dsp_master
                WHERE dsp_id IN (
                  SELECT DISTINCT dsp_id
                  FROM reqgroup
                  WHERE reqgroup_status = 'active'
                    AND dsp_id IN (:dspIds)
                )
            """.trimIndent(),
            MapSqlParameterSource("dspIds", dspIds.map { it.value }),
            DspMaster::class
        )
    }
}
