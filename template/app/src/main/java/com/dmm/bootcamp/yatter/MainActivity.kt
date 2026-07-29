package com.dmm.bootcamp.yatter

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import com.dmm.bootcamp.yatter.ui.login.LoginPage
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      YatterTheme {
        Surface {
          LoginPage(
            onLoggedIn = {},
            onNavigationRegister = {},
          )
        }
      }
    }
  }
}
