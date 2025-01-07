package com.manga.paradice.application.service.user

import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster
import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMasterDao
import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMasterInsert
import com.manga.paradice.presentation.controller.user.form.UserCreateForm
import com.manga.paradice.presentation.controller.user.view.UserCreateResultView
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userMasterDao: UserMasterDao
): UserService {
    override fun create(form: UserCreateForm): UserCreateResultView {
        val userId = userMasterDao.inert(
            UserMasterInsert(
                form.secMailAddress,
                form.secPassword,
                form.secUserName,
                form.userType ?: UserMaster.UserType.client
            )
        )
        return UserCreateResultView(userId)
    }
}