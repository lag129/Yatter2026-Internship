package com.dmm.bootcamp.yatter.domain.service

import com.dmm.bootcamp.yatter.domain.model.Username

interface GetLoginUsernameService {
  fun execute(): Username?
}
