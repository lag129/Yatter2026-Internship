# [前の資料](./2_infra層実装.md)
# ツイート画面のusecase層実装

ツイート画面実装に必要なUseCaseの実装を行います。  
今回必要なUseCaseはツイートを行う`PostYweetUseCase`のみですので、次のファイルを作成しましょう。  

- usecase/post/PostYweetUseCase.kt
- usecase/post/PostYweetUseCaseResult.kt
- usecase/impl/post/PostYweetUseCaseImpl.kt

## UseCaseResultの定義
まずは、UseCaseResultの定義を行います。  
今回想定するUseCaseの実行結果は次のようになります。  

- 成功
- 失敗
  - 投稿内容が空
  - ログインしていない
  - その他のエラー

これらの実行結果を`UseCaseResult`ととして定義すると次のようになります。  

```Kotlin
sealed interface PostYweetUseCaseResult {
  object Success : PostYweetUseCaseResult
  sealed interface Failure : PostYweetUseCaseResult {
    object EmptyContent : Failure
    object NotLoggedIn : Failure
    data class OtherError(val throwable: Throwable) : Failure
  }
}
```

## UseCase interfaceの定義
UseCaseResultの定義ができたら、UseCaseのinterfaceを定義します。  

```Kotlin
interface PostYweetUseCase {
  suspend fun execute(
    content: String,
    attachmentList: List<File>
  ): PostYweetUseCaseResult
}
```

## UseCaseの実装
続いてUseCaseの実装に入ります。  

`PostYweetUseCase`では次のような処理を行います。  

- 投稿内容が文字列も画像も無い時は`EmptyContent`
- 画像ファイルがあれば`ImageRepository`でアップロードして`imageId`のリストを取得
- `YweetRepository`で投稿する（テキストと画像IDリストを渡す）
- 投稿時に例外が発生しなければ`Success`
- 投稿時に`AuthenticatorException`が発生すると`NotLoggedIn`
- 投稿時に`AuthenticatorException`以外の例外が発生すると`OtherError`

実際にUseCaseの実装をしてみましょう。  

実装してみたら実装例と比較してみて問題なさそうか確認してください。  
もちろん、実装例とコードが同じである必要はなく、動作に問題なければ大丈夫です。  

<details>
<summary>PostYweetUseCaseImplの実装例</summary>

```Kotlin
class PostYweetUseCaseImpl(
  private val yweetRepository: YweetRepository,
  private val imageRepository: ImageRepository,
) : PostYweetUseCase {
  override suspend fun execute(
    content: String,
    attachmentList: List<File>
  ): PostYweetUseCaseResult {
    if (content == "" && attachmentList.isEmpty()) {
      return PostYweetUseCaseResult.Failure.EmptyContent
    }

    return try {
      val imageIds = attachmentList.map { file ->
        imageRepository.upload(file)
      }

      yweetRepository.create(
        content = content,
        attachmentList = attachmentList,
        imageIds = imageIds,
      )

      PostYweetUseCaseResult.Success
    } catch (e: AuthenticatorException) {
      PostYweetUseCaseResult.Failure.NotLoggedIn
    } catch (e: Exception) {
      PostYweetUseCaseResult.Failure.OtherError(e)
    }
  }
}
```

</details>


---

UseCaseの実装ができたら単体テストを書きます。

テスト用の`usecase/impl`に`PostYweetUseCaseImplSpec`ファイルを作成し、次の項目のテストを書いて実行してみましょう。  

- 投稿内容も空でなければ投稿に成功する
- 投稿内容が空の場合は、対応した失敗になる
- 未ログインであれば、対応した失敗になる
- 何かしらのエラーが発生したら、対応した失敗になる

テストが無事に通過すればUseCaseの実装も完了です。  

<details>
<summary>PostYweetUseCaseImplのテスト実装例</summary>

```Kotlin
class PostYweetUseCaseImplSpec {
  private val yweetRepository = mockk<YweetRepository>()
  private val imageRepository = mockk<ImageRepository>()
  private val subject = PostYweetUseCaseImpl(yweetRepository, imageRepository)

  @Test
  fun postYweetWithSuccess() = runTest {
    val content = "content"

    val yweet = Yweet(
      id = YweetId(value = ""),
      user = User(
        id = UserId(value = ""),
        username = Username(value = ""),
        displayName = null,
        note = null,
        avatar = URL("https://www.google.com"),
        header = URL("https://www.google.com"),
        followingCount = 0,
        followerCount = 0
      ),
      content = content,
      attachmentImageList = listOf(),
    )

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } returns yweet

    val result = subject.execute(
      content,
      emptyList(),
    )

    coVerify {
      yweetRepository.create(
        content,
        emptyList(),
        emptyList(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Success)
  }

  @Test
  fun postYweetWithEmptyContent() = runTest {
    val content = ""

    val result = subject.execute(
      content,
      emptyList(),
    )

    coVerify(inverse = true) {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.EmptyContent)
  }

  @Test
  fun postYweetWithNotLoggedIn() = runTest {
    val content = "content"

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } throws AuthenticatorException()

    val result = subject.execute(
      content,
      emptyList(),
    )


    coVerify {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.NotLoggedIn)
  }

  @Test
  fun postYweetWithOtherError() = runTest {
    val content = "content"
    val exception = Exception()

    coEvery {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    } throws exception

    val result = subject.execute(
      content,
      emptyList(),
    )


    coVerify {
      yweetRepository.create(
        any(),
        any(),
        any(),
      )
    }

    assertThat(result).isEqualTo(PostYweetUseCaseResult.Failure.OtherError(exception))
  }
}
```

</details>

# [次の資料](./4_DI実装.md)