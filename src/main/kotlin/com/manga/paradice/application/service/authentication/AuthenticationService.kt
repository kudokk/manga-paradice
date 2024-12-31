package com.manga.paradice.application.service.authentication

import jakarta.servlet.http.HttpSession
import com.manga.paradice.infrastructure.datasource.dao.usermaster.UserMaster

interface AuthenticationService {
    fun addSessionCoAccountInfoList(user: UserMaster, session: HttpSession)
}
