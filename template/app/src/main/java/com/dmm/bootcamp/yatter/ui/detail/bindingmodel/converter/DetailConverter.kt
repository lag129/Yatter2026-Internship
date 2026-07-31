package com.dmm.bootcamp.yatter.ui.detail.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.ui.detail.bindingmodel.DetailBindingModel
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.converter.ImageConverter

object DetailConverter {
  fun convertToBindingModel(yweetList: List<Yweet>): List<DetailBindingModel> =
    yweetList.map { convertToBindingModel(it) }

  fun convertToBindingModel(yweet: Yweet): DetailBindingModel = DetailBindingModel(
    id = yweet.id.value,
    displayName = yweet.user.displayName ?: "",
    username = yweet.user.username.value,
    avatar = yweet.user.avatar?.toString(),
    content = yweet.content,
    attachmentImageList = ImageConverter.convertToBindingModel(yweet.attachmentImageList),
  )
}
