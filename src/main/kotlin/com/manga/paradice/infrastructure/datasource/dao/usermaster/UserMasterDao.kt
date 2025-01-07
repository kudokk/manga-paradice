package com.manga.paradice.infrastructure.datasource.dao.usermaster

interface UserMasterDao {
    fun inert (userMaster: UserMasterInsert): Int

    /**
     * @param mailAddress
     * @return 引数のメールアドレスに紐づくUserMaster
     */
    fun selectByMailAddress(mailAddress: String): UserMaster?
}
