package com.dmm.bootcamp.yatter.domain.service

import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.Username

interface LoginService {
  suspend fun execute(username: Username, password: Password)
}
