package jp.mangaka.ssp.infrastructure.datasource.dao.spotvideodisplay

import jp.mangaka.ssp.application.valueobject.aspectratio.AspectRatioId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.JdbcWrapper
import jp.mangaka.ssp.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class SpotVideoDisplayDaoImpl(
    @Autowired @Qualifier("CompassMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : SpotVideoDisplayDao {
    override fun inserts(spotVideoDisplays: Collection<SpotVideoDisplayInsert>) {
        if (spotVideoDisplays.isEmpty()) return

        jdbcWrapper.insertExecuteBatch({ it.withTableName("spot_video_display") }, spotVideoDisplays)
    }

    override fun selectBySpotId(spotId: SpotId): List<SpotVideoDisplay> = jdbcWrapper.query(
        """
            SELECT *
            FROM spot_video_display
            WHERE spot_id = :spotId
        """.trimIndent(),
        CustomMapSqlParameterSource("spotId", spotId),
        SpotVideoDisplay::class
    )

    override fun selectBySpotIds(spotIds: Collection<SpotId>): List<SpotVideoDisplay> {
        if (spotIds.isEmpty()) return emptyList()

        return jdbcWrapper.query(
            """
                SELECT *
                FROM spot_video_display
                WHERE spot_id IN (:spotIds)
            """.trimIndent(),
            CustomMapSqlParameterSource("spotIds", spotIds),
            SpotVideoDisplay::class
        )
    }

    override fun updates(spotVideoDisplays: Collection<SpotVideoDisplayInsert>) {
        if (spotVideoDisplays.isEmpty()) return

        jdbcWrapper.bachUpdate(
            """
                UPDATE spot_video_display
                SET width = :width
                   ,height = :height
                   ,position_top = :positionTop
                   ,position_bottom = :positionBottom
                   ,position_left = :positionLeft
                   ,position_right = :positionRight
                   ,is_scalable = :isScalable
                   ,is_allowed_drag = :isAllowedDrag
                   ,is_rounded_rectangle = :isRoundedRectangle
                   ,close_button_type = :closeButtonType
                   ,close_button_size = :closeButtonSize
                   ,close_button_line_color = :closeButtonLineColor
                   ,close_button_bg_color = :closeButtonBgColor
                   ,close_button_frame_color = :closeButtonFrameColor
                   ,pr_label_type = :prLabelType
                WHERE spot_id = :spotId
                  AND aspect_ratio_id = :aspectRatioId
            """.trimIndent(),
            spotVideoDisplays
        )
    }

    override fun deleteBySpotIdAndAspectRatioIds(spotId: SpotId, aspectRatioIds: Collection<AspectRatioId>) {
        if (aspectRatioIds.isEmpty()) return

        jdbcWrapper.update(
            """
                DELETE FROM spot_video_display
                WHERE spot_id = :spotId
                  AND aspect_ratio_id IN (:aspectRatioIds)
            """.trimIndent(),
            CustomMapSqlParameterSource()
                .addValue("spotId", spotId)
                .addValue("aspectRatioIds", aspectRatioIds)
        )
    }
}
