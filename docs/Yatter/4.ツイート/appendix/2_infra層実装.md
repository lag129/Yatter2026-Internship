# [前の資料](./1_domain層実装.md)
# ツイート画面のinfra層実装
ツイート画面のinfra層実装を行います。
infra層では、domain層実装時に定義した`GetLoginUserService`と`GetLoginUsernameService`、`UserRepository`、`YweetRepository`の実装を行います。

まずは`UserRepository`の実装です。  
必要な引数と`impl`クラスの定義を`infra/domain/repository`に追加します。  

```Kotlin
class UserRepositoryImpl(
  private val usersApi: UsersApi,
  private val getLoginUsernameService: GetLoginUsernameService,
) : UserRepository {
}
```
必要なメソッドをオーバーライドします。  

```Kotlin
class UserRepositoryImpl(
  private val usersApi: UsersApi,
  private val getLoginUsernameService: GetLoginUsernameService,
) : UserRepository {
  override suspend fun findLoginUser(disableCache: Boolean): User? {
    TODO("Not yet implemented")
  }

  override suspend fun findByUsername(username: Username, disableCache: Boolean): User? {
    TODO("Not yet implemented")
  }

  override suspend fun create(username: Username, password: Password): User {
    TODO("Not yet implemented")
  }

  override suspend fun update(
    me: User,
    newDisplayName: String?,
    newNote: String?,
    newAvatar: URL?,
    newHeader: URL?
  ): User {
    TODO("Not yet implemented")
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
```

`UserRepositoryImpl`では、ユーザー名をもとにAPIからユーザー情報を取得します。  

ユーザー情報取得には自動生成された`UsersApi`の`findUserByUsername`メソッドを利用します。  

```Kotlin
// 自動生成されたUsersApiの定義（参考）
interface UsersApi {
    @GET("users/{username}")
    suspend fun findUserByUsername(
        @Path("username") username: String
    ): Response<User>
}
```

APIの定義ができたらツイート機能に必要な`findLoginUser`、`findByUsername`をまずは実装します。  
処理は次の手順を実装します。  

- getLoginUsernameServiceからログイン済みユーザー情報取得
  - ユーザー情報取得できなければ取得不可(null)
- ユーザー名からAPIを経由してアカウント情報取得
- `User`ドメインへ変換

コードは次のようになります。  

```Kotlin
  override suspend fun findLoginUser(disableCache: Boolean): User? = withContext(Dispatchers.IO) {
    val username = getLoginUsernameService.execute() ?: return@withContext null
    findByUsername(username = username, disableCache = disableCache)
  }

  override suspend fun findByUsername(
    username: Username,
    disableCache: Boolean,
  ): User? = withContext(Dispatchers.IO) {
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
```

ここで、`userCache`という変数が出てきました。
これは、過去に取得したことのあるユーザー情報をキャッシュしておくためのMapです。  
キャッシュを利用することで、APIの呼び出し回数を減らすことができ、アプリのパフォーマンス向上や通信量の削減に役立ちます。

キャッシュの実装は次のように行います。
```Kotlin
class UserRepositoryImpl(
  private val usersApi: UsersApi,
  private val getLoginUsernameService: GetLoginUsernameService,
) : UserRepository {
  private val userCache: MutableMap<Username, User> = mutableMapOf()
  ...
}
```



残りのメソッドは今回の機能では利用しないため、ひとまずTODOのままにしておきます。  
実装したメソッドには単体テストを書いてみましょう。  


<details>
<summary>UserRepositoryImplのテスト実装例</summary>

```Kotlin
class UserRepositoryImplSpec {
  private val usersApi = mockk<UsersApi>()
  private val getLoginUsernameService = mockk<GetLoginUsernameService>()
  private val subject = UserRepositoryImpl(usersApi, getLoginUsernameService)

  @Test
  fun findByUsername() = runTest {
    val username = Username("username")
    val apiUser = ApiUser(
      id = 1,
      username = "username",
      displayName = "display name",
      note = null,
      avatar = "https://www.google.com",
      header = "https://www.google.com",
      followingCount = 0,
      followersCount = 0,
      createdAt = OffsetDateTime.now(),
    )

    val expect = UserConverter.convertFromApiModel(apiUser)

    coEvery {
      usersApi.findUserByUsername(any())
    } returns Response.success(apiUser)

    val result = subject.findByUsername(username, disableCache = false)

    coVerify {
      usersApi.findUserByUsername(username.value)
    }

    assertThat(result).isEqualTo(expect)
  }

  @Test
  fun findLoginUser() = runTest {
    val username = "username"
    val apiUser = ApiUser(
      id = 1,
      username = "username",
      displayName = "display name",
      note = null,
      avatar = "https://www.google.com",
      header = "https://www.google.com",
      followingCount = 0,
      followersCount = 0,
      createdAt = OffsetDateTime.now(),
    )
    val expect = UserConverter.convertFromApiModel(apiUser)

    coEvery {
      getLoginUsernameService.execute()
    } returns Username(username)
    coEvery {
      usersApi.findUserByUsername(any())
    } returns Response.success(apiUser)

    val result = subject.findLoginUser(disableCache = false)

    coVerify {
      usersApi.findUserByUsername(username)
    }
    coVerify {
      getLoginUsernameService.execute()
    }

    assertThat(result).isEqualTo(expect)
  }
}
```

</details>

---

続いて`GetLoginUserService`の実装です。

`infra/domain/service`に`GetLoginUserServiceImpl`クラスを定義します。  
`UserRepository`を介して`User`クラスを取得するため引数に`UserRepository`も追加しておきます。  

```Kotlin
class GetLoginUserServiceImpl(
  private val userRepository: UserRepository,
) : GetLoginUserService {
  override suspend fun execute(): User? = withContext(Dispatchers.IO) {
    userRepository.findLoginUser(disableCache = false)
  }
}
```

こちらもテストを実装して動作の担保をします。  

<details>
<summary>GetLoginUserServiceImplSpecのテスト実装例</summary>

```Kotlin
class GetLoginUserServiceImplSpec {
  private val userRepository = mockk<UserRepository>()
  private val subject = GetLoginUserServiceImpl(userRepository)

  @Test
  fun getLoginUser() {
    val user = User(
      id = UserId(value = ""),
      username = Username(value = ""),
      displayName = null,
      note = null,
      avatar = URL("https://www.google.com"),
      header = URL("https://www.google.com"),
      followingCount = 0,
      followerCount = 0,
    )

    coEvery { userRepository.findLoginUser(disableCache = any()) } returns user

    val result = runBlocking { subject.execute() }

    assertThat(result).isEqualTo(user)
  }
}
```

</details>

---

次は`GetLoginUsernameService`の実装です。
`infra/domain/service`に`GetLoginUsernameServiceImpl`クラスを定義します。

```Kotlin
class GetLoginUsernameServiceImpl(
  private val loginUserPreferences: LoginUserPreferences,
) : GetLoginUsernameService {
  override fun execute(): Username? = loginUserPreferences.getUsername()?.let { Username(it) }
}
```

`?.let { }` は、`getUsername()` が null でない場合のみブロックを実行する null チェックのイディオムです。詳しくは [Kotlinのスコープ関数について](../../../tutorial/Kotlinのスコープ関数について/1_スコープ関数とは.md) を参照してください。

こちらもテストを実装して動作の担保をします。
<details>
<summary>GetLoginUsernameServiceImplSpecのテスト実装例</summary>

```Kotlin
class GetLoginUsernameServiceImplSpec {
  private val loginUserPreferences = mockk<LoginUserPreferences>()
  private val subject = GetLoginUsernameServiceImpl(loginUserPreferences)

  @Test
  fun getLoginUsername() {
    val username = "username"

    every { loginUserPreferences.getUsername() } returns username

    val result = subject.execute()

    assertThat(result).isEqualTo(Username(value = username))
  }

  @Test
  fun getLoginUsernameNull() {
    every { loginUserPreferences.getUsername() } returns null

    val result = subject.execute()

    assertThat(result).isNull()
  }
}
```
</details>

---

`YweetRepository`の実装です。  
`YweetRepositoryImpl`での投稿処理は`YweetsApi`を使って実装します。  

ツイート用APIの定義を確認するとツイート用のAPIを実行する際にはBodyが必要となっています。
`YweetsApi.addYweet`は自動生成されており、次のように定義されています。

```Kotlin
// 自動生成されたYweetsApiの定義（参考）
interface YweetsApi {
    @POST("yweets")
    suspend fun addYweet(
        @Body addYweetRequest: AddYweetRequest
    ): Response<Yweet>
}
```

`AddYweetRequest`は投稿内容と画像添付リストを保持するリクエストクラスです。

```Kotlin
// 自動生成されたAddYweetRequest（参考）
data class AddYweetRequest(
  val yweet: String,
  val images: List<AttachmentRequest>,
)

data class AttachmentRequest(
  val imageId: Int,
  val description: String,
)
```

APIの定義が確認できたらRepositoryの実装に戻ります。  
既に`YweetRepositoryImpl`クラスは定義されているため、クラスを開きます。  

ツイート投稿には`YweetsApi`が、ホームタイムラインには認証付きの`TimelinesApi`が必要です。  
パブリックタイムライン章で追加した`timelinesApi`を`publicTimelinesApi`にリネームし、`homeTimelinesApi`と`yweetsApi`を追加します。

```Kotlin
class YweetRepositoryImpl(
  private val publicTimelinesApi: TimelinesApi,
  private val homeTimelinesApi: TimelinesApi,
  private val yweetsApi: YweetsApi,
) : YweetRepository {
  ...
}
```

また、`findAllPublic`内で使っていた`timelinesApi`の参照も`publicTimelinesApi`に合わせて更新します。

`create`メソッドにも`imageIds`パラメータを追加します。  
`YweetRepository`インターフェースを確認し、`create`メソッドが定義されていない場合は次のメソッドを追加します。  
すでに定義されている場合は`imageIds: List<Int>`パラメータを追加してください。

`create`メソッドでは次の処理を実施します。  

- APIを通じて投稿
- 投稿完了したらYweetを返す

```Kotlin
override suspend fun create(
  content: String,
  attachmentList: List<File>,
  imageIds: List<Int>,
): Yweet = withContext(IO) {
  val images = imageIds.map { imageId ->
    AttachmentRequest(
      imageId = imageId,
      description = "",
    )
  }
  val request = AddYweetRequest(
    yweet = content,
    images = images,
  )
  val response = yweetsApi.addYweet(request)
  val body = response.body() ?: throw Exception("Failed to create yweet: response body was null (HTTP ${response.code()})")
  YweetConverter.convertFromApiModel(body)
}
```

なお、認証ヘッダーの付与は`TokenInterceptor`が自動的に行うため、Repositoryの実装では意識する必要はありません。  
`TokenInterceptor`はDIで`YweetsApi`に設定されています。

`create`メソッドが実装できたら単体テストを実施しましょう。  
次に示す項目のテストを書いてみてください。  

- テキストのみのツイートに成功する
- 画像IDを指定したツイートに成功する
- APIがnullを返した場合に例外が発生する

<details>
<summary>YweetRepositoryImpl#createのテスト実装例</summary>

```Kotlin
class YweetRepositoryImplSpec {
  private val publicTimelinesApi = mockk<TimelinesApi>()
  private val homeTimelinesApi = mockk<TimelinesApi>()
  private val yweetsApi = mockk<YweetsApi>()
  private val subject = YweetRepositoryImpl(publicTimelinesApi, homeTimelinesApi, yweetsApi)

  @Test
  fun postYweetWhenLoggedIn() = runTest {
    val loginUsername = "token"
    val content = "content"

    val apiYweet = ApiYweet(
      id = 1,
      user = ApiUser(
        id = 1,
        username = loginUsername,
        createdAt = OffsetDateTime.now(),
        followersCount = 0,
        followingCount = 0,
        displayName = "",
        note = null,
        avatar = "https://www.google.com",
        header = "https://www.google.com",
      ),
      content = content,
      createdAt = OffsetDateTime.now(),
      imageAttachments = emptyList(),
    )

    coEvery {
      yweetsApi.addYweet(any())
    } returns Response.success(apiYweet)

    val expect = YweetConverter.convertFromApiModel(apiYweet)

    val result = subject.create(
      content,
      emptyList(),
      emptyList(),
    )

    assertThat(result).isEqualTo(expect)

    coVerify {
      yweetsApi.addYweet(
        AddYweetRequest(
          yweet = content,
          images = emptyList()
        )
      )
    }
  }

  @Test
  fun postYweetWhenApiReturnsNull() = runTest {
    val content = "content"

    coEvery {
      yweetsApi.addYweet(any())
    } returns Response.success(null)

    var error: Throwable? = null
    var result: Yweet? = null

    try {
      result = subject.create(
        content,
        emptyList(),
        emptyList(),
      )
    } catch (e: Exception) {
      error = e
    }

    assertThat(result).isNull()
    assertThat(error).isNotNull()
  }
}
```

</details>

---

テストが通ることまで確認できたらinfra層の実装は完了です。  

# [次の資料](./3_usecase層実装.md)
