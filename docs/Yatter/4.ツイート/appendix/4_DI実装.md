# [前の資料](./3_usecase層実装.md)
# ツイート画面のDI実装
ツイート画面のDI実装をして、これまで実装した各クラスのつなぎ込みを行い、ツイート画面を動作させるようにします。

配線の書き方の復習は [Koinを使ったDI](../../../tutorial/DIについて/3_Koinを使ったDI.md) を参照してください。

ツイート画面用に各モジュールへ追加する対象は次になります。

- `GetLoginUserService`
- `UserRepository`
- `PostYweetUseCase`
- `PostViewModel`

また、infra層実装で`YweetRepositoryImpl`のコンストラクタに`homeTimelinesApi`と`yweetsApi`を追加したため、`InfraModule`と`DomainImplModule`の`YweetRepository`定義も更新します。  

`InfraModule`では、ホームタイムライン用（認証あり）の`TimelinesApi`と`YweetsApi`を追加します。  
既存のパブリック用`TimelinesApi`には`named("public")`の修飾子を付けて区別します。

```Kotlin
// InfraModule（変更箇所のみ抜粋）
internal val infraModule = module {
  ...
  // パブリックタイムライン用（認証不要）— named修飾子を追加
  single<TimelinesApi>(named("public")) {
    ApiClient().createService(TimelinesApi::class.java)
  }
  // ホームタイムライン用（認証あり）— 新規追加
  single<TimelinesApi>(named("home")) {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(TimelinesApi::class.java)
  }
  // ツイート投稿用（認証あり）— 新規追加
  single<YweetsApi> {
    ApiClient().apply {
      addAuthorization("Auth", get<TokenInterceptor>())
    }.createService(YweetsApi::class.java)
  }
  ...
}
```

`TimelinesApi`に`named()`を使うことで、同じ型でも異なるインスタンスを区別して注入できます。  
`DomainImplModule`の`YweetRepository`定義も`named()`に合わせて更新します。

コード中の `ApiClient().apply { ... }` は、`apply` スコープ関数を使ってオブジェクトを生成しながら設定を行うイディオムです。`apply` は設定後にレシーバー自身を返すため、`.createService(...)` をそのままチェーンで呼べます。詳しくは [Kotlinのスコープ関数について](../../../tutorial/Kotlinのスコープ関数について/1_スコープ関数とは.md) を参照してください。  

それぞれ適切なモジュールにインスタンス化方法を定義します。  

```Kotlin
// DomainImplModule
internal val domainImplModule = module {
  single<YweetRepository> {
    YweetRepositoryImpl(
      get(named("public")),
      get(named("home")),
      get(),
    )
  }
  single<UserRepository> { UserRepositoryImpl(get(), get()) }
  ...

  factory<GetLoginUserService> { GetLoginUserServiceImpl(get()) }
  factory<GetLoginUsernameService> { GetLoginUsernameServiceImpl(get()) }
}

// UseCaseModule
internal val useCaseModule = module {
  factory<PostYweetUseCase> { PostYweetUseCaseImpl(get(), get()) }
}

// ViewModelModule
internal val viewModelModule = module {
  viewModel { PostViewModel(get(), get()) }
//  viewModel { RegisterViewModel(get()) }
}
```

これでツイート画面実装は完了になります。  

次に`PostViewModel`のテストを実装します。

# [次の資料](./5_ViewModelのテスト実装.md)
