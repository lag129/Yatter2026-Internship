package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.service.GetLoginUsernameService
import com.dmm.bootcamp.yatter.infra.pref.LoginUserPreferences

class GetLoginUsernameServiceImpl(private val loginUserPreferences: LoginUserPreferences) :
  GetLoginUsernameService {
  override fun execute(): Username? = loginUserPreferences.getUsername()?.let { Username(it) }
}
