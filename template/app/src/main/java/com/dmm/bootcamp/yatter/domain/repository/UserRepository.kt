package com.dmm.bootcamp.yatter.domain.repository

import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.Username
import java.net.URL

interface UserRepository {
  suspend fun findLoginUser(disableCache: Boolean): User?

  suspend fun findByUsername(username: Username, disableCache: Boolean): User?

  suspend fun create(username: Username, password: Password): User

  suspend fun update(
    me: User,
    newDisplayName: String?,
    newNote: String?,
    newAvatar: URL?,
    newHeader: URL?,
  ): User

  suspend fun followings(): List<User>
  suspend fun followers(): List<User>

  suspend fun follow(me: User, username: Username)
  suspend fun unfollow(me: User, username: Username)
}
