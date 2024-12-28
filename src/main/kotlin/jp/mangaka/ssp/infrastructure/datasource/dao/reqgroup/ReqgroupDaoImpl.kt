package jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup

import jp.mangaka.ssp.application.valueobject.dsp.DspId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.dao.reqgroup.Reqgroup.ReqgroupStatus
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class ReqgroupDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : ReqgroupDao {
    override fun selectReqgroupDspCountryCosByDspIdsAndStatuses(
        dspIds: Collection<DspId>,
        statuses: Collection<ReqgroupStatus>
    ): List<ReqgroupDspCountryCo> {
        if (dspIds.isEmpty() || statuses.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT r.reqgroup_id, r.dsp_id, rrc.country_id
                FROM reqgroup r
                JOIN relay_reqgroup_country rrc
                  ON r.reqgroup_id = rrc.reqgroup_id
                WHERE r.dsp_id IN (:dspIds)
                  AND r.reqgroup_status IN (:statuses)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("dspIds", dspIds)
                .addValue("statuses", statuses),
            ReqgroupDspCountryCo::class
        )
    }
}
