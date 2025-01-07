package com.manga.paradice.infrastructure.datasource.dao.usermaster

import com.manga.paradice.infrastructure.datasource.JdbcWrapper
import com.manga.paradice.infrastructure.datasource.mapper.CustomMapSqlParameterSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository

@Repository
class UserMasterDaoImpl(
    @Autowired @Qualifier("CoreMasterJdbc") private val jdbcWrapper: JdbcWrapper
) : UserMasterDao {
    override fun inert(userMaster: UserMasterInsert): Int = jdbcWrapper.insertExecuteAndReturnId(
        """
            INSERT INTO user_master (
                sec_mail_address, sec_password, sec_user_name, user_type 
            ) VALUES (
                :secMailAddress, :secPassword, :secUserName, :userType
            )
        """.trimIndent(),
        userMaster
    ).toInt()

    override fun selectByMailAddress(mailAddress: String): UserMaster? =
        jdbcWrapper.queryForObject(
            """
              SELECT
                * 
              FROM user_master 
              WHERE sec_user_mail_address = :mailAddress
                AND soft_delete_flag = 'open'  
            """.trimIndent(),
            CustomMapSqlParameterSource("mailAddress", mailAddress),
            UserMaster::class
        )
}
