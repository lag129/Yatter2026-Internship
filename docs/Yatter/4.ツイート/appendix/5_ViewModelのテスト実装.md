# [前の資料](./4_DI実装.md)
# ViewModelのテスト実装
ツイート画面で利用しているViewModelのテストを書いてみましょう。

`PostViewModel` の投稿成功・戻る操作は **`PostNavigationEvent` の `Flow`**（`Channel<PostNavigationEvent>` を `receiveAsFlow()` したもの）として検証します。`onClickPost` は `Context` を引数に取るため、テストでは `mockk<Context>(relaxed = true)` などで渡します。

`PostViewModel`のテストを実装します。  
今回のテストは次の観点を確認します。  

- onCreate時にユーザー情報が取得できていること
- テキスト入力するとUiStateが更新されること
- 投稿ボタン押下で投稿完了すること（`PostNavigationEvent.Posted` が送られること）
- 投稿ボタン押下でエラー発生時に成功イベントが送られないこと
- ナビゲーションの戻るボタン押下時に `PostNavigationEvent.Back` が送られること

実際にテストを書いてみて、テストコード例も載せていますので見比べながら動作を確認しましょう。

<details>
<summary>PostViewModelのテストコード例</summary>

```Kotlin
class PostViewModelSpec {
  private val getLoginUserService = mockk<GetLoginUserService>()
  private val postYweetUseCase = mockk<PostYweetUseCase>()
  private val subject = PostViewModel(
    postYweetUseCase,
    getLoginUserService,
  )

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun getMeWhenOnCreate() = runTest {
    val avatarUrl = URL("https://www.dmm.com")
    val me = User(
      id = UserId(value = "me user"),
      username = Username(value = ""),
      displayName = null,
      note = null,
      avatar = avatarUrl,
      header = URL("https://www.google.com"),
      followingCount = 0,
      followerCount = 0
    )
    coEvery {
      getLoginUserService.execute()
    } returns me

    subject.onCreate()

    assertThat(subject.uiState.value.bindingModel.avatarUrl).isEqualTo(avatarUrl.toString())
  }


  @Test
  fun changeYweetAndCanPost() = runTest {
    val newYweetText = "new"

    subject.onChangedYweetText(newYweetText)

    assertThat(subject.uiState.value.bindingModel.yweetText).isEqualTo(newYweetText)
    assertThat(subject.uiState.value.canPost).isTrue()
  }

  @Test
  fun changeYweetAndCannotPost() = runTest {
    val oldYweetText = "old"
    val newYweetText = ""

    subject.onChangedYweetText(oldYweetText)
    assertThat(subject.uiState.value.bindingModel.yweetText).isEqualTo(oldYweetText)
    assertThat(subject.uiState.value.canPost).isTrue()

    subject.onChangedYweetText(newYweetText)

    assertThat(subject.uiState.value.bindingModel.yweetText).isEqualTo(newYweetText)
    assertThat(subject.uiState.value.canPost).isFalse()
  }

  @Test
  fun postSuccess() = runTest {
    val yweet = "yweet"
    val context = mockk<Context>(relaxed = true)
    subject.onChangedYweetText(yweet)

    coEvery {
      postYweetUseCase.execute(any(), any())
    } returns PostYweetUseCaseResult.Success

    val deferred = async { subject.navigationEvent.first() }
    subject.onClickPost(context)

    coVerify {
      postYweetUseCase.execute(any(), any())
    }

    assertThat(deferred.await()).isEqualTo(PostNavigationEvent.Posted)
  }

  @Test
  fun postFailure() = runTest {
    val yweet = "yweet"
    val context = mockk<Context>(relaxed = true)
    subject.onChangedYweetText(yweet)

    coEvery {
      postYweetUseCase.execute(any(), any())
    } returns PostYweetUseCaseResult.Failure.OtherError(Exception())

    subject.onClickPost(context)

    coVerify {
      postYweetUseCase.execute(any(), any())
    }
  }

  @Test
  fun clickBack() = runTest {
    val deferred = async { subject.navigationEvent.first() }
    subject.onClickNavIcon()

    assertThat(deferred.await()).isEqualTo(PostNavigationEvent.Back)
  }
}
```

</details>

# [次の章へ](../../5.その次は/1_その次は.md)
