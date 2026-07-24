# [前の資料：AndroidにおけるDI](./2_AndroidにおけるDI.md)
# Koinを使ったDI

[Koin](https://insert-koin.io/) は Kotlin向けの軽量な DI（依存性注入）フレームワークです。**アノテーションやコード生成に頼らず**、DSLで「どの型をどう作るか」を宣言します。Android公式が推す [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)（Daggerベース）とは別系統ですが、学習コストが比較的低く、本プロジェクトでも採用されています。

[概要編](./1_概要.md) の一般論や、[AndroidにおけるDI](./2_AndroidにおけるDI.md) で触れた「`Application`で初期化」「`ViewModel`へ注入」の文脈のうえで読むと理解しやすいです。

この章では、**本リポジトリの実装**に沿ってKoinの基本と読み方を説明します。

---

## 依存関係の宣言（Gradle）

`app/build.gradle.kts`の `dependencies`で、Koinの BOM（Bill of Materials）と各モジュールを読み込んでいます。

- `koin-core` … コアの DSLとコンテナ
- `koin-android` … `Application`連携、`androidContext`など
- `koin-androidx-compose` … Composeから `ViewModel`を取得するための拡張

バージョンは BOMで揃えられるため、個別のバージョン番号を各 artifactに書かなくてよい構成になっています。

---

## アプリ起動時：Koinの開始

`YatterApplication`の`onCreate`で`startKoin { ... }`を呼び出し、Android用のロガー・`Context`・**モジュール一覧**を登録しています。

```15:31:app/src/main/java/com/dmm/bootcamp/yatter/YatterApplication.kt
class YatterApplication : Application(), ImageLoaderFactory {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidLogger()
      // Reference Android context
      androidContext(this@YatterApplication)
      modules(
        modules = listOf(
          domainImplModule,
          infraModule,
          useCaseModule,
          viewModelModule,
        )
      )
    }
  }
```

- `androidContext(...)`により、モジュール内の`get()`で`Context`が必要な定義に`Application`が注入されます。
- `modules(...)`に渡した順序よりも、**各定義が参照する型**に応じてKoinが依存を解決します。

---

## モジュールに分割する理由

本プロジェクトでは、責務ごとにKoinの `module { }`をファイル分割しています。

| モジュール | 役割のイメージ |
|-----------|----------------|
| `infraModule` | APIクライアント、Preferences、インターセプターなどインフラ寄り |
| `domainImplModule` | ドメインの`Repository` / `Service`の**実装**をインターフェースに紐づける |
| `useCaseModule` | ユースケース実装とインターフェースの紐づけ |
| `viewModelModule` | 各`ViewModel`のファクトリ（画面ごとの依存） |

レイヤー構造の背景は [アプリ アーキテクチャ ガイドについて](../../appendix/16-Androidアプリアーキテクチャガイドについて.md) を参照してください。

---

## `single`と`factory`

Koinの定義でよく使う生存期間は次のとおりです。

### `single { ... }`

アプリ（Koinコンテナ）の寿命の間、**実体は 1 つ**（シングルトン）。APIクライアントや`Repository`の実装など、重い初期化や共有したいものに向きます。

`InfraModule`の例（一部）：

```kotlin
single<AuthApi> { ApiClient().createService(AuthApi::class.java) }
```

### `factory { ... }`

**呼び出すたびに新しいインスタンス**。ユースケースのように、呼び出しごとに独立した状態を持たせたい場合に使われます。

`UseCaseModule`の例：

```kotlin
factory<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
```

`get()`は「このモジュール（および他モジュール）で登録済みの依存を取ってきて渡す」という意味です。ネストした`get()`により、**コンストラクタに相当する配線**を DSLで書いています。

---

## 名前（Qualifier）で同じ型を複数登録する

同じ型（例：`TimelinesApi`）でも、用途が違えば別インスタンスが必要です。Koinでは`named("識別子")`で修飾子を付けます。

この例では、2つの`TimelinesApi`を別名で登録しています。

```kotlin
single<TimelinesApi>(named("public")) { ApiClient().createService(TimelinesApi::class.java) }
single<TimelinesApi>(named("home")) {
  ApiClient().apply {
    addAuthorization("Auth", get<TokenInterceptor>())
  }.createService(TimelinesApi::class.java)
}
```

受け取り側では`get(named("public"))`のように指定します。この例では、`YweetRepositoryImpl`に2つの`TimelinesApi`を別引数として渡しています。

```kotlin
single<YweetRepository> {
  YweetRepositoryImpl(
    get(named("public")),
    get(named("home")),
    get(),
    get(),
  )
}
```

---

## ViewModelの登録：`viewModel { }`

`ViewModel`は Androidのライフサイクルに紐づくため、Koinでは`viewModel { }`DSLで登録します（内部で適切なスコープで生成されます）。

```16:27:app/src/main/java/com/dmm/bootcamp/yatter/di/ViewModelModule.kt
internal val viewModelModule = module {
  viewModel { MainViewModel(get()) }
  viewModel { PublicTimelineViewModel(get()) }
  viewModel { TimelineViewModel(get()) }
  viewModel { PostViewModel(get(), get()) }
  viewModel { RegisterViewModel(get()) }
  viewModel { LoginViewModel(get()) }
  viewModel { (yweetId: String) -> YweetDetailViewModel(yweetId, get(), get(), get()) }
  viewModel { (username: String) -> ProfileViewModel(username, get(), get()) }
  viewModel { EditProfileViewModel(get(), get()) }
  viewModel { SettingsViewModel(get()) }
}
```

- `PostViewModel(get(), get())`のように、コンストラクタ引数の数だけ`get()`を並べます。順序は **コンストラクタの引数順** に対応します（型で解決されるため、同じ型が続く場合は注意が必要です）。
- 画面固有のIDなどを渡したい場合は、`viewModel { (id: String) -> ... }`のように**パラメータ付き定義**にします。

---

## ComposeからViewModelを取得する：`koinViewModel`

Composeの`@Composable`からは、`koin-androidx-compose`が提供する`koinViewModel()`で`ViewModel`を取得します。

```kotlin
@Composable
fun LoginPage(loginViewModel: LoginViewModel = koinViewModel()) {
  // ...
}
```

### パラメータ付き ViewModel：`parametersOf`

詳細画面の`ViewModel`のように、ナビゲーション引数（記事や詳細対象の`id`など）をコンストラクタで受け取る場合は、`parametersOf`でKoinに値を渡します。引数ごとに別の`ViewModel`が欲しいときは、次の例のとおり **`key`も指定する**と安全です。

```kotlin
@Composable
fun YweetDetailPage(
  yweetId: String,
  yweetDetailViewModel: YweetDetailViewModel = koinViewModel(key = yweetId) {
    parametersOf(yweetId)
  },
) {
```

`parametersOf`に渡した値は、モジュール側の`viewModel { (yweetId: String) -> ... }`の引数に順番で渡されます。

### （つまずきポイント）`key`を付けないと同じViewModelが再利用される

`parametersOf`だけでは、**引数が変わっても同じ`ViewModel`インスタンスが返る**ことがあります。別の`yweetId`の詳細画面に遷移したのに、前の画面のデータが残る、といった不具合の原因になります。

Koinは`ViewModelStore`上でインスタンスをキャッシュします。`key`を指定しないと、型（`YweetDetailViewModel`など）だけで同一視され、**パラメータが違っても同じインスタンス**が使われる場合があります。

そのため、ナビゲーション引数ごとに別インスタンスが欲しいときは、**引数の値を`key`にも渡す**と安全です。

```kotlin
// 推奨：yweetId ごとに別 ViewModel
koinViewModel(key = yweetId) { parametersOf(yweetId) }

// username ごとに別 ViewModel（プロフィール画面なども同様）
koinViewModel(key = username) { parametersOf(username) }
```

`key`は「この画面用のViewModelを一意に識別する名前」だと考えるとよいです。公式ドキュメントでも、バックスタック上で引数の違う画面が共存するときは`key`の利用が案内されています（[ViewModel in Compose | Koin](https://insert-koin.io/docs/reference/koin-compose/compose-viewmodel/)）。

インターンで実装するときは、パラメータ付きの詳細画面では **`key`と`parametersOf`の両方**を付けることを推奨します。

---

## 読み方のコツ

1. **「この画面のViewModelは何が欲しいか」** → `ViewModelModule`の該当行を見る。
2. **`get()`の先** → 同じファイル内の別の `single` / `factory` / 他モジュールの定義をたどる。
3. **同じ型が複数** → `named(...)`の有無を確認する。

デバッグ時は、`androidLogger()`が有効なので、未定義の型を`get()`しようとするとスタックトレースで不足している定義が分かりやすくなります。

---

## まとめ

| 要素 | 役割 |
|------|------|
| `startKoin` | アプリ起動時にコンテナを初期化し、モジュールを読み込む |
| `module { }` | 型と生成方法の束ねをグループ化 |
| `single` / `factory` | 共有インスタンスか、都度生成か |
| `named` | 同じ型の複数登録を区別する |
| `viewModel` | ViewModel用のスコープ付き定義 |
| `koinViewModel` / `parametersOf` | ComposeからViewModelを取得し、引数を渡す |
| `key` | パラメータ付きViewModelを引数ごとに別インスタンス化する |

[概要編](./1_概要.md) および [AndroidにおけるDI](./2_AndroidにおけるDI.md) で述べた「コンポジションルートで配線し、クラスは依存を受け取るだけ」という考え方が、上記の DSLと`get()`の組み合わせとして表現されています。
