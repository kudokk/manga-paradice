package com.manga.paradice.infrastructure.datasource.dao.usermaster

interface UserMasterDao {
    /**
     * @param mailAddress
     * @return 引数のメールアドレスに紐づくUserMaster
     */
    fun selectByMailAddress(mailAddress: String): UserMaster?
}
