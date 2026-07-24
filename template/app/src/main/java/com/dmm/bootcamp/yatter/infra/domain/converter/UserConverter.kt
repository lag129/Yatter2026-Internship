package com.dmm.bootcamp.yatter.infra.domain.converter

import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.UserId
import com.dmm.bootcamp.yatter.domain.model.Username
import java.net.URL
import remote.models.User as ApiUser

object UserConverter {
  fun convertFromApiModel(apiUser: ApiUser) = User(
    id = UserId(apiUser.id.toString()),
    username = Username(apiUser.username),
    displayName = apiUser.displayName,
    note = apiUser.note,
    avatar = apiUser.avatar?.takeIf { it.isNotEmpty() }?.let { URL(it) },
    header = apiUser.header?.takeIf { it.isNotEmpty() }?.let { URL(it) },
    followingCount = apiUser.followingCount,
    followerCount = apiUser.followersCount,
  )
}
