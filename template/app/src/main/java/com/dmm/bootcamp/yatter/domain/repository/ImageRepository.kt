package com.dmm.bootcamp.yatter.domain.repository

import java.io.File

interface ImageRepository {
  suspend fun upload(file: File): Int
}
