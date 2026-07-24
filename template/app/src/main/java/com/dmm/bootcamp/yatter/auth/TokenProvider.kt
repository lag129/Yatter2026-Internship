package com.dmm.bootcamp.yatter.auth

interface TokenProvider {
  suspend fun provide(): String
}
