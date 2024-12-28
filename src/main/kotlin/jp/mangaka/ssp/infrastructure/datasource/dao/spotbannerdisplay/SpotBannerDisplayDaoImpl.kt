package jp.mangaka.ssp.infrastructure.datasource.dao.spotbannerdisplay

import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotBannerDisplayDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotBannerDisplayDao {
    override fun insert(spotBannerDisplay: SpotBannerDisplayInsert) {
        jdbcWrapper.insertExecute(
            {
                it
                    .withTableName("spot_banner_display")
                    .usingGeneratedKeyColumns("is_scroll_end_control", "is_allowed_drag", "is_rounded_rectangle")
            },
            spotBannerDisplay
        )
    }

    override fun selectById(spotId: SpotId): SpotBannerDisplay? = jdbcWrapper.queryForObject(
        """
            SELECT *
            FROM spot_banner_display
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotBannerDisplay::class
    )

    override fun selectByIds(spotIds: Collection<SpotId>): List<SpotBannerDisplay> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM spot_banner_display
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            SpotBannerDisplay::class
        )
    }

    override fun update(spotBannerDisplay: SpotBannerDisplayInsert) {
        jdbcWrapper.update(
            """
                UPDATE spot_banner_display
                SET position_bottom = :positionBottom
                   ,is_scalable = :isScalable
                   ,is_display_scrolling = :isDisplayScrolling
                   ,decoration_id = :decorationId
                   ,close_button_type = :closeButtonType
                   ,close_button_size = :closeButtonSize
                   ,close_button_line_color = :closeButtonLineColor
                   ,close_button_bg_color = :closeButtonBgColor
                   ,close_button_frame_color = :closeButtonFrameColor
                WHERE spot_id = :spotId
            """.trimIndent(),
            spotBannerDisplay
        )
    }

    override fun deleteById(spotId: SpotId) {
        jdbcWrapper.update(
            """
                DELETE FROM spot_banner_display
                WHERE spot_id = :spotId
            """.trimIndent(),
            CustomMapSqlParameterSource("spotId", spotId)
        )
    }
}
