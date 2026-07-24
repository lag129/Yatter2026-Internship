package com.dmm.bootcamp.yatter.infra.domain.converter

import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.domain.model.YweetId
import remote.models.Yweet as ApiYweet

object YweetConverter {
  fun convertFromApiModel(apiYweetList: List<ApiYweet>): List<Yweet> =
    apiYweetList.map { convertFromApiModel(it) }

  fun convertFromApiModel(apiYweet: ApiYweet): Yweet = Yweet(
    id = YweetId(apiYweet.id.toString()),
    user = UserConverter.convertFromApiModel(apiYweet.user),
    content = apiYweet.content,
    attachmentImageList = ImageConverter.convertFromApiModel(apiYweet.imageAttachments),
  )
}
