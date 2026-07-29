package com.dmm.bootcamp.yatter.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmm.bootcamp.yatter.R
import com.dmm.bootcamp.yatter.ui.theme.YatterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTemplate(
  userName: String,
  onChangedUserName: (String) -> Unit,
  password: String,
  onChangedPassword: (String) -> Unit,
  isEnableLogin: Boolean,
  isLoading: Boolean,
  onClickLogin: () -> Unit,
  onClickRegister: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = stringResource(R.string.login_title))
        }
      )
    }
  ) { paddingValues ->

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(8.dp),
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Text(
          text = stringResource(R.string.login_username_title),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        )
        OutlinedTextField(
          singleLine = true,
          value = userName,
          onValueChange = onChangedUserName,
          placeholder = {
            Text(text = stringResource(R.string.login_username_placeholder))
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        )

        Text(
          text = stringResource(R.string.login_password_title),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          singleLine = true,
          value = password,
          onValueChange = onChangedPassword,
          placeholder = {
            Text(text = stringResource(R.string.login_password_placeholder))
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        )

        Button(
          enabled = isEnableLogin,
          onClick = onClickLogin,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(text = stringResource(R.string.login_button_text))
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
          text = stringResource(R.string.login_first_time_users_text),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth()
        )
        TextButton(
          onClick = onClickRegister,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(stringResource(R.string.login_registration_text))
        }
      }

      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun LoginTemplatePreview() {
  YatterTheme {
    Surface {
      LoginTemplate(
        userName = "username",
        onChangedUserName = {},
        password = "",
        onChangedPassword = {},
        isEnableLogin = true,
        isLoading = false,
        onClickLogin = {},
        onClickRegister = {},
      )
    }
  }
}
