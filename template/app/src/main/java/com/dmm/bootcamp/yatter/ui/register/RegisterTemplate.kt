package com.dmm.bootcamp.yatter.ui.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun RegisterTemplate(
  userName: String,
  onChangedUserName: (String) -> Unit,
  password: String,
  onChangedPassword: (String) -> Unit,
  isEnableRegister: Boolean,
  isLoading: Boolean,
  onClickLogin: () -> Unit,
  onClickRegister: () -> Unit,
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = stringResource(R.string.register_title))
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.post_back_icon_button_description)
            )
          }
        }
      )
    },
  ) { paddingValues ->

    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(8.dp),
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Text(
          text = stringResource(R.string.register_username_title),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        )
        OutlinedTextField(
          singleLine = true,
          value = userName,
          onValueChange = onChangedUserName,
          placeholder = {
            Text(text = stringResource(R.string.register_username_placeholder))
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        )

        Text(
          text = stringResource(R.string.register_password_title),
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          singleLine = true,
          value = password,
          onValueChange = onChangedPassword,
          placeholder = {
            Text(text = stringResource(R.string.register_password_placeholder))
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        )

        Button(
          enabled = isEnableRegister,
          onClick = onClickRegister,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(text = stringResource(R.string.register_button_title))
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
          text = stringResource(R.string.register_login_text_button_description),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.fillMaxWidth()
        )
        TextButton(
          onClick = onClickLogin,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(text = stringResource(R.string.register_login_text_button_title))
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
private fun RegisterTemplatePreview() {
  YatterTheme {
    Surface {
      RegisterTemplate(
        userName = "username",
        onChangedUserName = {},
        password = "",
        onChangedPassword = {},
        isEnableRegister = true,
        isLoading = false,
        onClickLogin = {},
        onClickRegister = {},
        onBack = {},
      )
    }
  }
}
