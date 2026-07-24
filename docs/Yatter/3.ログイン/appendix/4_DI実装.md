# [前の資料](./3_usecase層実装.md)
# ログイン画面のDI実装
ログイン画面のDI実装を行います。

基本的には [パブリックタイムラインのDI実装](../../2.パブリックタイムライン/appendix/3_DI実装.md) と同様です。Koinの `single` / `factory` / `viewModel` / `get()`の説明は [Koinを使ったDI](../../../tutorial/DIについて/3_Koinを使ったDI.md) にまとめています。

ログイン画面では `UseCase`を実装したため、`di` パッケージ内の `UseCaseModule`を確認します。template にはすでに定義済みのため、UseCaseは呼び出しごとに新しいインスタンスでよい `factory` で登録されていることを確認してください。

```Kotlin
val useCaseModule = module {
  // factory<FooUseCase> { FooUseCaseImpl() }
}
```

作成したDIモジュールはApplicationファイルの`modules`に追加します。  

```Kotlin
modules(
  modules = listOf(
    domainImplModule,
    infraModule,
    useCaseModule,
    viewModelModule,
  )
)
```

UseCase用のDIモジュールの用意ができたところでログイン画面実装時に追加したクラスを各層のDIモジュールに定義していきます。  

`DomainImplModule`に`LoginService`と`CheckLoginService`、`InfraModule`に`LoginUserPreferences`と`TokenPreferences`、`UseCaseModule`に`LoginUseCase`、`ViewModelModule`に`LoginViewModel`を定義してみましょう。  

```Kotlin
// DomainImplModule

internal val domainImplModule = module {
  ...
  factory<LoginService> { LoginServiceImpl(get(), get()) }
  factory<CheckLoginService> { CheckLoginServiceImpl(get()) }
  ...
}

// InfraModule

internal val infraModule = module {
  ...
  single { LoginUserPreferences(get()) }
  single { TokenPreferences(get()) }
  single<AuthApi> { ApiClient().createService(AuthApi::class.java) }
  ...
}

// UseCaseModule

internal val useCaseModule = module {
  ...
  factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
  ...
}

// ViewModelModule

internal val viewModelModule = module {
  ...
  viewModel { LoginViewModel(get()) }
  viewModel { MainViewModel(get()) }
  ...
}

```

これでDI層の実装も完了です。  

# [次の資料](./5_ViewModelのテスト実装.md)
