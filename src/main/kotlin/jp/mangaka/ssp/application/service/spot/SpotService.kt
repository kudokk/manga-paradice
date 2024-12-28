package jp.mangaka.ssp.application.service.spot

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.spot.SpotId
import jp.mangaka.ssp.infrastructure.datasource.dao.usermaster.UserMaster.UserType
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBannerEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotBasicEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotDspEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotNativeEditForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotVideoEditForm
import jp.mangaka.ssp.presentation.controller.spot.view.SpotCreateResultView

interface SpotService {
    /**
     * 広告枠の作成を行う
     *
     * @param coAccountId CoアカウントID
     * @param userType ユーザー種別
     * @param form フォーム
     * @return 新規作成された広告枠のID
     */
    fun create(coAccountId: CoAccountId, userType: UserType, form: SpotCreateForm): SpotCreateResultView

    /**
     * 広告枠の基本設定の編集を行う.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @param form フォーム
     */
    fun editBasic(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotBasicEditForm)

    /**
     * 広告枠のDSP設定の編集を行う.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @param form フォーム
     */
    fun editDsp(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotDspEditForm)

    /**
     * 広告枠のバナー設定の編集を行う.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @param form フォーム
     */
    fun editBanner(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotBannerEditForm)

    /**
     * 広告枠のネイティブ設定の編集を行う.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @param form フォーム
     */
    fun editNative(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotNativeEditForm)

    /**
     * 広告枠のビデオ設定の編集を行う.
     *
     * @param coAccountId CoアカウントID
     * @param spotId 広告枠ID
     * @param userType ユーザー種別
     * @param form フォーム
     */
    fun editVideo(coAccountId: CoAccountId, spotId: SpotId, userType: UserType, form: SpotVideoEditForm)
}
