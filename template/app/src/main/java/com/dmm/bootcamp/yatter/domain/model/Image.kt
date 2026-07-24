package com.dmm.bootcamp.yatter.domain.model

import com.dmm.bootcamp.yatter.common.ddd.Entity

class Image(id: ImageId, val type: String, val url: String, val description: String?) :
  Entity<ImageId>(id)
