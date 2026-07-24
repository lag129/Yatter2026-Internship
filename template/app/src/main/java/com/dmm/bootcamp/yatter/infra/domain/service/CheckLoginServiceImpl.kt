package com.dmm.bootcamp.yatter.infra.domain.service

import com.dmm.bootcamp.yatter.domain.service.CheckLoginService
import com.dmm.bootcamp.yatter.infra.pref.TokenPreferences

class CheckLoginServiceImpl(private val tokenPreferences: TokenPreferences) : CheckLoginService {
  override suspend fun execute(): Boolean = tokenPreferences.getAccessToken().isNullOrEmpty().not()
}
