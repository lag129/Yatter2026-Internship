package com.dmm.bootcamp.yatter.infra.pref

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class LoginUserPreferences(context: Context) {
  companion object {
    private const val PREF_NAME = "login_user"
    private const val KEY_USERNAME = "username"
  }

  private val sharedPreferences = context.getSharedPreferences(
    PREF_NAME,
    Context.MODE_PRIVATE,
  )

  fun getUsername(): String? = sharedPreferences.getString(
    KEY_USERNAME,
    null,
  )

  suspend fun putUsername(username: String?) = withContext(IO) {
    sharedPreferences.edit {
      putString(
        KEY_USERNAME,
        username,
      )
    }
  }

  fun clear() = sharedPreferences.edit {
    clear()
  }
}
