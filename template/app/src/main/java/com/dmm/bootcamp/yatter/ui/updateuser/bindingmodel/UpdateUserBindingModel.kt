package com.dmm.bootcamp.yatter.ui.updateuser.bindingmodel

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class UpdateUserBindingModel(
  val displayName: String,
  val avatarUri: Uri?,
  val note: String
)
