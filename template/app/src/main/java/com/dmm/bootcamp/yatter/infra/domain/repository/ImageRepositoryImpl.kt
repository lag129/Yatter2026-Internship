package com.dmm.bootcamp.yatter.infra.domain.repository

import com.dmm.bootcamp.yatter.domain.repository.ImageRepository
import java.io.File
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import remote.apis.ImagesApi

class ImageRepositoryImpl(private val imagesApi: ImagesApi) : ImageRepository {
  override suspend fun upload(file: File): Int = withContext(IO) {
    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
    val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
    val response = imagesApi.addImages(part)
    val body =
      response.body()
        ?: throw Exception(
          "Failed to upload image: response body was null (HTTP ${response.code()})",
        )
    body.imageId
  }
}
