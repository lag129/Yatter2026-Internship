package com.dmm.bootcamp.yatter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmm.bootcamp.yatter.domain.service.CheckLoginService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
  private val checkLoginService: CheckLoginService
) : ViewModel() {

  private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
  val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

  fun onCreate() {
    viewModelScope.launch {
      _isLoggedIn.value = checkLoginService.execute()
    }
  }
}
