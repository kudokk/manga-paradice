package jp.mangaka.ssp.presentation.controller.spot.view

import jp.mangaka.ssp.application.valueobject.decoration.DecorationId
import jp.mangaka.ssp.infrastructure.datasource.dao.decoration.Decoration

data class DecorationView(
    val decorationId: DecorationId,
    val decorationName: String,
    val height: Int,
    val backgroundColor: String?,
    val text: String,
    val textColor: String
) {
    companion object {
        /**
         * @param decorations デコレーション設定のエンティティのリスト
         * @return デコレーション設定一覧のView
         */
        fun of(decorations: Collection<Decoration>): List<DecorationView> =
            decorations.map { of(it) }

        /**
         * @param decoration デコレーション設定
         * @return デコレーション設定のView
         */
        fun of(decoration: Decoration): DecorationView = DecorationView(
            decoration.decorationId,
            decoration.decorationName,
            decoration.bandHeight,
            decoration.bandBgcolor,
            decoration.bandString,
            decoration.bandFontColor
        )
    }
}
