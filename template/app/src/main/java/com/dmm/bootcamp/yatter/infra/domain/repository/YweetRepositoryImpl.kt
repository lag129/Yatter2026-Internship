package com.dmm.bootcamp.yatter.infra.domain.repository

import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.domain.model.YweetId
import com.dmm.bootcamp.yatter.domain.repository.YweetRepository
import com.dmm.bootcamp.yatter.infra.domain.converter.YweetConverter
import java.io.File
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import remote.apis.TimelinesApi
import remote.apis.YweetsApi
import remote.models.AddYweetRequest
import remote.models.AttachmentRequest

class YweetRepositoryImpl(
  private val publicTimelinesApi: TimelinesApi,
  private val homeTimelinesApi: TimelinesApi,
  private val yweetsApi: YweetsApi,
) : YweetRepository {
  override suspend fun findById(id: YweetId): Yweet? = withContext(IO) {
    val response = yweetsApi.findYweetByID(id.value.toInt())
    val body = response.body() ?: return@withContext null
    YweetConverter.convertFromApiModel(body)
  }

  override suspend fun findAllPublic(): List<Yweet> = withContext(IO) {
    val response = publicTimelinesApi.findPublicTimelines()
    val body = response.body() ?: return@withContext emptyList()
    YweetConverter.convertFromApiModel(body)
  }

  override suspend fun findAllHome(): List<Yweet> = withContext(IO) {
    val response = homeTimelinesApi.findHomeTimelines()
    val body = response.body() ?: return@withContext emptyList()
    YweetConverter.convertFromApiModel(body)
  }

  override suspend fun create(
    content: String,
    attachmentList: List<File>,
    imageIds: List<Int>,
  ): Yweet = withContext(IO) {
    val images = imageIds.map { imageId ->
      AttachmentRequest(
        imageId = imageId,
        description = "",
      )
    }
    val request = AddYweetRequest(
      yweet = content,
      images = images,
    )
    val response = yweetsApi.addYweet(request)
    val body =
      response.body()
        ?: throw Exception(
          "Failed to create yweet: response body was null (HTTP ${response.code()})",
        )
    YweetConverter.convertFromApiModel(body)
  }

  override suspend fun delete(yweet: Yweet) = withContext(IO) {
    yweetsApi.deleteYweet(yweet.id.value.toInt())
    Unit
  }
}
