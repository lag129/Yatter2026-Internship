package com.dmm.bootcamp.yatter.domain.service

interface CheckLoginService {
  suspend fun execute(): Boolean
}
