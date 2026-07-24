# [前の資料](./4_DI実装.md)
# ログイン画面のテスト実装
ログイン画面でしているモジュールのテストを書いてみましょう。  

`MainViewModel` は `isLoggedIn: StateFlow<Boolean?>` を検証し、`LoginViewModel` は `Channel<LoginNavigationEvent>` を `receiveAsFlow()` した `navigationEvent: Flow<LoginNavigationEvent>` を、`async { navigationEvent.first() }` などで検証します。

## LoginViewModelのテスト
`PublicTimelineViewModel`の単体テストを参考にしてみてください。  

テストする項目は次のようになります。  
- ユーザー名を変更したときにUiStateに反映されるか
- パスワードを変更したときにUiStateに反映されるか
- 入力したユーザー名とパスワードが有効な値になっているか
- ログインボタンを押してログイン成功した場合に `LoginNavigationEvent.LoggedIn` が流れるか
- ログインボタンを押してログイン失敗した場合に UseCase が呼ばれ、成功イベントが流れないか
- 登録ボタンを押したときに `LoginNavigationEvent.NavigatedToRegister` が流れるか

ヒントとして、ViewModel は `StateFlow` を公開しているため、テストでは `MainDispatcherRule` を追加して coroutine を制御します。

```Kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()
```

`event.first()` を使う場合は、`kotlinx.coroutines.async` で先に待ち受けを起動してから `onClickLogin` を呼ぶとタイムアウトしにくくなります。

<details>
<summary>LoginViewModelのテスト例</summary>

```Kotlin
class LoginViewModelSpec {
  private val loginUseCase = mockk<LoginUseCase>()
  private val subject = LoginViewModel(loginUseCase)

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun changeUsername() = runTest {
    val newUsername = "newUsername"

    subject.onChangedUsername(newUsername)

    assertThat(subject.uiState.value.loginBindingModel.username).isEqualTo(newUsername)
    assertThat(subject.uiState.value.validUsername).isTrue()
  }

  @Test
  fun changePasswordValid() = runTest {
    val newPassword = "newPassword1$"

    subject.onChangedPassword(newPassword)

    assertThat(subject.uiState.value.loginBindingModel.password).isEqualTo(newPassword)
    assertThat(subject.uiState.value.validPassword).isTrue()
  }

  @Test
  fun changePasswordInvalid() = runTest {
    val newPassword = ""

    subject.onChangedPassword(newPassword)

    assertThat(subject.uiState.value.loginBindingModel.password).isEqualTo(newPassword)
    assertThat(subject.uiState.value.validPassword).isFalse()
  }

  @Test
  fun clickLoginAndEmitLoggedIn() = runTest {
    val username = "username"
    val password = "Password1$"

    subject.onChangedUsername(username)
    subject.onChangedPassword(password)

    coEvery {
      loginUseCase.execute(any(), any())
    } returns LoginUseCaseResult.Success

    val deferred = async { subject.navigationEvent.first() }
    subject.onClickLogin()

    coVerify {
      loginUseCase.execute(Username(username), LoginPassword(password))
    }
    assertThat(deferred.await()).isEqualTo(LoginNavigationEvent.LoggedIn)
  }

  @Test
  fun clickLoginAndFailure() = runTest {
    val username = "username"
    val password = "Password1$"

    subject.onChangedUsername(username)
    subject.onChangedPassword(password)

    coEvery {
      loginUseCase.execute(any(), any())
    } returns LoginUseCaseResult.Failure.OtherError(Exception())

    subject.onClickLogin()

    coVerify {
      loginUseCase.execute(Username(username), LoginPassword(password))
    }
  }

  @Test
  fun clickRegister_emitsNavigatedToRegister() = runTest {
    val deferred = async { subject.navigationEvent.first() }
    subject.onClickRegister()

    assertThat(deferred.await()).isEqualTo(LoginNavigationEvent.NavigatedToRegister)
  }
}
```

</details>

---

## MainViewModelのテスト
`MainViewModel`のテストは次の観点で確認します。  

- ログイン済みであれば `isLoggedIn` が `true` になること
- ログイン済みでなければ `isLoggedIn` が `false` になること

<details>
<summary>MainViewModelのテスト実装例</summary>

```Kotlin
class MainViewModelSpec {
  private val checkLoginService = mockk<CheckLoginService>()
  private val subject = MainViewModel(checkLoginService)

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun isLoggedInTrueWhenTokenExists() = runTest {
    coEvery {
      checkLoginService.execute()
    } returns true

    subject.onCreate()

    assertThat(subject.isLoggedIn.value).isTrue()
  }

  @Test
  fun isLoggedInFalseWhenNoToken() = runTest {
    coEvery {
      checkLoginService.execute()
    } returns false

    subject.onCreate()

    assertThat(subject.isLoggedIn.value).isFalse()
  }
}
```

</details>

---

# [次の章へ](../4.ツイート/1_ツイート機能概要.md)
