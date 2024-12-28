package jp.mangaka.ssp.infrastructure.datasource.dao.spotnativevideodisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotNativeVideoDisplayDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotNativeVideoDisplayDao {
    override fun insert(spotNativeVideoDisplay: SpotNativeVideoDisplayInsert) {
        jdbcWrapper.insertExecute(
            { it.withTableName("spot_native_video_display").usingGeneratedKeyColumns(*defaultGeneratedColumns) },
            spotNativeVideoDisplay
        )
    }

    override fun selectBySpotId(spotId: SpotId): SpotNativeVideoDisplay? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM spot_native_video_display
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotNativeVideoDisplay::class
    )

    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<SpotNativeVideoDisplay> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM spot_native_video_display
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            SpotNativeVideoDisplay::class
        )
    }

    override fun update(spotNativeVideoDisplay: SpotNativeVideoDisplayInsert) {
        jdbcWrapper.update(
            """
                UPDATE spot_native_video_display
                SET native_template_id = :nativeTemplateId
                   ,player_width = :playerWidth
                   ,player_height = :playerHeight
                   ,width = :width
                   ,height = :height
                   ,position_bottom = :positionBottom
                   ,is_scalable = :isScalable
                   ,close_button_type = :closeButtonType
                   ,close_button_size = :closeButtonSize
                   ,close_button_line_color = :closeButtonLineColor
                   ,close_button_bg_color = :closeButtonBgColor
                   ,close_button_frame_color = :closeButtonFrameColor
                WHERE spot_id = :spotId
            """.trimIndent(),
            spotNativeVideoDisplay
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_native_video_display
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }

    companion object {
        private val defaultGeneratedColumns = arrayOf(
            "is_scroll_end_control",
            "is_display_scrolling",
            "is_allowed_drag",
            "is_rounded_rectangle"
        )
    }
}
