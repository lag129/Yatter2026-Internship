package com.dmm.bootcamp.yatter.infra.domain.converter

import com.dmm.bootcamp.yatter.domain.model.Image
import com.dmm.bootcamp.yatter.domain.model.ImageId
import remote.models.Attachment

object ImageConverter {
  fun convertFromApiModel(attachmentList: List<Attachment>): List<Image> =
    attachmentList.map { convertFromApiModel(it) }

  private fun convertFromApiModel(attachment: Attachment): Image = Image(
    id = ImageId(value = attachment.id.toString()),
    type = attachment.type,
    url = attachment.url,
    description = attachment.description,
  )
}
