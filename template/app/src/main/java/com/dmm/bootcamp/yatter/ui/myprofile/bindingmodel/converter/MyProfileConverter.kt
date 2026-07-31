package com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.ui.myprofile.bindingmodel.MyProfileBindingModel

object MyProfileConverter {
  fun convertToBindingModel(user: User): MyProfileBindingModel = MyProfileBindingModel(
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
