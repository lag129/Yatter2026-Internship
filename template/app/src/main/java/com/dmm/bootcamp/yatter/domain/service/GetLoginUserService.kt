package com.dmm.bootcamp.yatter.domain.service

import com.dmm.bootcamp.yatter.domain.model.User

interface GetLoginUserService {
  suspend fun execute(): User?
}
