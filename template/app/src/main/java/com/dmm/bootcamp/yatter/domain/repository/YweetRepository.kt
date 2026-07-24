package com.dmm.bootcamp.yatter.domain.repository

import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.domain.model.YweetId
import java.io.File

interface YweetRepository {
  suspend fun findById(id: YweetId): Yweet?

  suspend fun findAllPublic(): List<Yweet>

  suspend fun findAllHome(): List<Yweet>

  suspend fun create(
    content: String,
    attachmentList: List<File>,
    imageIds: List<Int> = emptyList(),
  ): Yweet

  suspend fun delete(yweet: Yweet)
}
