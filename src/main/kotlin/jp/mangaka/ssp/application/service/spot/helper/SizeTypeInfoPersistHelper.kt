package jp.mangaka.ssp.application.service.spot.helper

import jp.mangaka.ssp.application.valueobject.coaccount.CoAccountId
import jp.mangaka.ssp.application.valueobject.platform.PlatformId
import jp.mangaka.ssp.application.valueobject.sizetypeinfo.SizeTypeId
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoDao
import jp.mangaka.ssp.infrastructure.datasource.dao.sizetypeinfo.SizeTypeInfoInsert
import jp.mangaka.ssp.presentation.controller.spot.form.SizeTypeForm
import jp.mangaka.ssp.presentation.controller.spot.form.SpotCreateForm
import org.jetbrains.annotations.TestOnly
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SizeTypeInfoPersistHelper(private val sizeTypeInfoDao: SizeTypeInfoDao) {
    /**
     * @param coAccountId CoアカウントID
     * @param platformId プラットフォームID
     * @param form 広告枠の登録フォーム
     * @return (登録済み+新規登録した)サイズ種別情報IDのリスト（formsと同順）
     */
    @Transactional("CoreMasterTX")
    fun bulkCreate(coAccountId: CoAccountId, platformId: PlatformId, form: SpotCreateForm): List<SizeTypeId> {
        val bannerSizes = form.banner?.sizeTypes ?: emptyList()
        // ネイティブ専用受入サイズ 0x0 (size_type_id = 99 / 199) をDBから取得する.
        val nativeSizes = form.native?.let { listOf(SizeTypeForm.zero) } ?: emptyList()
        val allSizes = bannerSizes + nativeSizes

        return bulkCreate(coAccountId, platformId, allSizes)
    }

    @Transactional("CoreMasterTX")
    fun bulkCreate(
        coAccountId: CoAccountId,
        platformId: PlatformId,
        forms: List<SizeTypeForm>
    ): List<SizeTypeId> {
        if (forms.isEmpty()) return emptyList()

        // 登録済みのサイズ種別情報を取得
        val existingSizeTypeInfoMap = getExistingSizeTypeInfoMap(platformId)

        // 未登録のサイズ種別情報を登録
        val newSizeTypeInfoMap = forms
            .filter { !existingSizeTypeInfoMap.contains(it.width to it.height) }
            .let { bulkCreateNewSizeTypeInfos(platformId, it) }

        val allSizeTypeInfoMap = existingSizeTypeInfoMap + newSizeTypeInfoMap

        // 引数のフォームと同じ順序でIDのリストを返却
        return forms.map { allSizeTypeInfoMap.getValue(it.width to it.height) }
    }

    /**
     * @param platformId プラットフォームID
     * @return 既存のサイズ種別情報の横幅x縦幅とサイズ種別IDのマッピング情報
     */
    @TestOnly
    fun getExistingSizeTypeInfoMap(platformId: PlatformId): Map<Pair<Int, Int>, SizeTypeId> = sizeTypeInfoDao
        .selectByPlatformId(platformId)
        .associateBy({ it.width to it.height }, { it.sizeTypeId })

    /**
     * @param platformId プラットフォーム
     * @param forms 新規登録するサイズ種別情報の登録フォームのリスト
     * @return 既存のサイズ種別情報の横幅x縦幅とサイズ種別IDのマッピング情報
     */
    @TestOnly
    fun bulkCreateNewSizeTypeInfos(
        platformId: PlatformId,
        forms: List<SizeTypeForm>
    ): Map<Pair<Int, Int>, SizeTypeId> =
        if (forms.isNotEmpty()) {
            forms
                .map { SizeTypeInfoInsert(it.width, it.height, platformId) }
                .let { inserts ->
                    val insertedIds = sizeTypeInfoDao.bulkInsert(inserts)
                    inserts.map { it.width to it.height }.zip(insertedIds)
                }.toMap()
        } else {
            emptyMap()
        }
}
