package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativedisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotNativeDisplayDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotNativeDisplayDao {
    override fun insert(spotNativeDisplay: SpotNativeDisplayInsert) {
        jdbcWrapper.insertExecute(
            { it.withTableName("spot_native_display").usingGeneratedKeyColumns(*defaultGeneratedColumns) },
            spotNativeDisplay
        )
    }

    override fun selectById(spotId: SpotId): SpotNativeDisplay? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM spot_native_display
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotNativeDisplay::class
    )

    override fun selectByIds(spotIds: Collection<SpotId>): List<SpotNativeDisplay> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM spot_native_display
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            SpotNativeDisplay::class
        )
    }

    override fun update(spotNativeDisplay: SpotNativeDisplayInsert) {
        jdbcWrapper.update(
            """
                UPDATE spot_native_display
                SET native_template_id = :nativeTemplateId
                   ,close_button_type = :closeButtonType
                   ,close_button_type = :closeButtonType
                   ,close_button_size = :closeButtonSize
                   ,close_button_line_color = :closeButtonLineColor
                   ,close_button_bg_color = :closeButtonBgColor
                   ,close_button_frame_color = :closeButtonFrameColor
                WHERE spot_id = :spotId
            """.trimIndent(),
            spotNativeDisplay
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_native_display
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }

    companion object {
        private val defaultGeneratedColumns = arrayOf(
            "is_scalable",
            "is_scroll_end_control",
            "is_display_scrolling",
            "is_allowed_drag",
            "is_rounded_rectangle"
        )
    }
}
