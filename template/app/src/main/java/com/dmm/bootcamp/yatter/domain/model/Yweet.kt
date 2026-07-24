package com.dmm.bootcamp.yatter.domain.model

import com.dmm.bootcamp.yatter.common.ddd.Entity

class Yweet(
  id: YweetId,
  val user: User,
  val content: String,
  val attachmentImageList: List<Image>,
) : Entity<YweetId>(id)
