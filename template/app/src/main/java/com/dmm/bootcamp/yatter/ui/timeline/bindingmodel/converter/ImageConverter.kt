package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.Image
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.ImageBindingModel

object ImageConverter {
  fun convertToBindingModel(imageList: List<Image>): List<ImageBindingModel> =
    imageList.map { convertToBindingModel(it) }

  private fun convertToBindingModel(image: Image): ImageBindingModel = ImageBindingModel(
    id = image.id.value,
    type = image.type,
    url = image.url,
    description = image.description,
  )
}
