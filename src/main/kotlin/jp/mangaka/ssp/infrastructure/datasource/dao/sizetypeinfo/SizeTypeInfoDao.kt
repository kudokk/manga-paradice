package jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.application.valueobject.spot.SpotId

interface SizeTypeInfoDao {
    /**
     * @return すべての標準定義のSizeTypeInfoのリスト
     */
    fun selectStandards(): List<SizeTypeInfo>

    /**
     * @param coAccountId CoアカウントID
     * @return CoアカウントIDに紐づくユーザー定義のSizeTypeInfoのリスト
     */
    fun selectUserDefinedsByCoAccountId(coAccountId: CoAccountId): List<SizeTypeInfo>

    /**
     * @param spotId スポットID
     * @return スポットIDに紐づくSizeTypeInfoのリスト
     */
    @Deprecated("relay_spot_sizetypeのDaoが定義されているのでそちらを使用する形に変更される予定")
    fun selectBySpotId(spotId: SpotId): List<SizeTypeInfo>

    /**
     * @param sizeTypeIds サイズ種別IDのリスト
     * @return 引数のサイズ種別IDに紐づく SizeTypeInfo のリスト
     */
    fun selectByIds(sizeTypeIds: Collection<SizeTypeId>): List<SizeTypeInfo>

    /**
     * @param platformId プラットフォームID
     * @return プラットフォームIDに紐づくSizeTypeInfoのリスト
     */
    fun selectByPlatformId(platformId: PlatformId): List<SizeTypeInfo>

    /**
     * @param sizeTypeInfos 登録するサイズ種別情報のリスト
     * @return 登録時に自動採番されたIDのリスト（sizeTypeInfosと同順）
     */
    fun bulkInsert(sizeTypeInfos: List<SizeTypeInfoInsert>): List<SizeTypeId>
}
