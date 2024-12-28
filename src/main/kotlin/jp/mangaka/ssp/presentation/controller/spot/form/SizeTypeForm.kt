package jp.mangaka.ssp.presentation.controller.spot.form

// フロント側で入力制御しているためwidth,heightはnon-null
data class SizeTypeForm(val width: Int, val height: Int) {
    companion object {
        // ネイティブ専用受入サイズ
        val zero = SizeTypeForm(0, 0)
    }
}
