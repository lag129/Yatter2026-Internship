package com.dmm.bootcamp.yatter.ui.profile.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.ui.profile.bindingmodel.ProfileBindingModel

object ProfileConverter {
  fun convertToBindingModel(user: User): ProfileBindingModel = ProfileBindingModel(
    id = user.id.value,
    username = user.username.value,
    displayName = user.displayName ?: "",
    note = user.note ?: "",
    avatar = user.avatar?.toString(),
    header = user.header?.toString(),
    followingCount = user.followingCount,
    followerCount = user.followerCount
  )
}
