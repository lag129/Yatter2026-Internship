package com.dmm.bootcamp.yatter.infra.domain.repository

import android.util.Log
import com.dmm.bootcamp.yatter.domain.model.Password
import com.dmm.bootcamp.yatter.domain.model.User
import com.dmm.bootcamp.yatter.domain.model.Username
import com.dmm.bootcamp.yatter.domain.repository.UserRepository
import com.dmm.bootcamp.yatter.domain.service.GetLoginUsernameService
import com.dmm.bootcamp.yatter.infra.domain.converter.UserConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import remote.apis.UsersApi
import remote.models.LoginRequest
import retrofit2.HttpException
import java.io.File

class UserRepositoryImpl(
  private val usersApi: UsersApi,
  private val getLoginUsernameService: GetLoginUsernameService,
) : UserRepository {
  private val userCache: MutableMap<Username, User> = mutableMapOf()

  override suspend fun create(username: Username, password: Password): User =
    withContext(Dispatchers.IO) {
      val request = LoginRequest(
        username = username.value,
        password = password.value,
      )
      val response = usersApi.addUser(request)
      val body =
        response.body() ?: throw Exception("Failed to create user: HTTP ${response.code()}")
      UserConverter.convertFromApiModel(body)
    }

  override suspend fun findLoginUser(disableCache: Boolean): User? = withContext(Dispatchers.IO) {
    val username = getLoginUsernameService.execute() ?: return@withContext null
    findByUsername(username = username, disableCache = disableCache)
  }

  override suspend fun findByUsername(username: Username, disableCache: Boolean): User? =
    withContext(Dispatchers.IO) {
      if (!disableCache) {
        userCache[username]?.let {
          return@withContext it
        }
      }
      try {
        val response = usersApi.findUserByUsername(username = username.value)
        val body = response.body() ?: return@withContext null
        val user = UserConverter.convertFromApiModel(body)
        userCache[username] = user
        return@withContext user
      } catch (e: HttpException) {
        Log.d("UserRepositoryImpl", "HTTP error: ${e.code()} message:${e.message()}")
        null
      } catch (e: Exception) {
        Log.d("UserRepositoryImpl", "Error: ${e.message}")
        null
      }
    }

  override suspend fun update(
    me: User,
    newDisplayName: String?,
    newNote: String?,
    newAvatar: File?,
    newHeader: File?,
  ): User = withContext(Dispatchers.IO) {
    val response = usersApi.updateUser(
      displayName = newDisplayName,
      note = newNote,
      avatar = newAvatar?.let {
        MultipartBody.Part.createFormData(
          "avatar",
          it.name,
          newAvatar.asRequestBody("image/*".toMediaTypeOrNull())
        )
      },
    )
    val body = response.body() ?: throw Exception("Failed to update user: HTTP ${response.code()}")
    val user = UserConverter.convertFromApiModel(body)
    userCache[me.username] = user
    user
  }

  override suspend fun followings(): List<User> {
    TODO("Not yet implemented")
  }

  override suspend fun followers(): List<User> {
    TODO("Not yet implemented")
  }

  override suspend fun follow(me: User, username: Username) {
    TODO("Not yet implemented")
  }

  override suspend fun unfollow(me: User, username: Username) {
    TODO("Not yet implemented")
  }
}
