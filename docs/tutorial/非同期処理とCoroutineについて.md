# Androidアプリ開発における非同期処理

レイヤー化された Android アプリでは、**非同期処理を行うために** 複数の層で Kotlin Coroutine を利用するのが一般的です。  
Repository や API 通信（infra 層）だけでなく、UseCase（usecase 層）や ViewModel（UI 層）でも Coroutine が登場します。Yatter もこの構成です。

本章では、Coroutine の文法の前に **なぜ非同期処理が必要なのか**、**過去にはどんな手法があったか**、**各層で Coroutine をどう使うか** を説明します。コード例は本研修の Yatter を用います。

> 本章は Android アプリ開発に限定した説明です。非同期処理全般の厳密な定義や理論は扱いません。

---

## 1. Android で非同期処理が必要な理由

### メインスレッドは UI 専用

Android アプリには **メインスレッド（UI スレッド）** があります。  
画面の描画、タップの受付、Jetpack Compose の再描画など、**ユーザーが触れる部分はすべてここで動きます**。

メインスレッドは基本**1本**のため、メインスレッド上で時間のかかる処理を **完了まで待つ（ブロックする）** と、他の処理が進みません。

```
[メインスレッド]
  タップ受付 → API通信(3秒待ち) → 画面更新
              ↑ 待っている間、画面は固まり、操作できない
```

### Android アプリでは「時間がかかる処理」が多い

Android アプリ全般で、次のような処理に **数百 ms〜数秒** かかります。

| 処理 | 例 |
|------|-----|
| ネットワーク通信 | タイムライン取得、ログイン |
| ディスク I/O | SharedPreferences、DB、ファイル読み書き |
| 画像のデコード | 大きな画像の読み込み |

たとえば Yatter でも、タイムライン取得（ネットワーク通信）やログイン（ネットワーク通信 + 端末内保存）など、上記に該当する処理があります。

これらをメインスレッドで **同期的に** 実行すると、次の問題が起きます。

- 画面が固まり（フリーズ）、タップ等の操作に反応しない
- 数秒続くと **ANR（Application Not Responding）** ダイアログが表示される

ANR は「メインスレッドが応答しなくなった」という OS からの警告です。  
**Android では「UI は即座に更新し、重い処理は別スレッドで実施」が基本ルール** になります。

ANR についての詳細は [デバッグの仕方](../../1.はじめに/3_デバッグの仕方.md) も参照してください。

### 「非同期」とは何か（Android 文脈）

Android 開発で言う非同期は、ざっくり次のイメージです。

> **時間のかかる処理をメインスレッドから切り離し、完了したら結果を UI に反映する**

ポイントは2つです。

1. **待ち時間中も UI を動かし続ける**
2. **結果が返ってきたらメインスレッドで画面を更新する**（UI の更新はメインスレッドで行う）

---

## 2. 過去にはどんな手法があったか

現在、Kotlin で書く Android アプリでは、非同期処理の選択肢として **Coroutine が標準** となっています。  
Thread、Callback、RxJava などは **レガシーコードを読むとき** に出てくる程度で、新規実装で積極的に選ぶことはほとんどありません。

本研修でも **Coroutine 中心** に解説しています。  
以下は「過去にどんなやり方があったか」の参考です。

| 手法 | ざっくりした特徴 |
|------|------------------|
| Thread / Executor | スレッド管理を自分で行う |
| Callback | 成功/失敗をコールバックで受け取る。連鎖すると読みにくい |
| RxJava | ストリーム合成が強力。かつて主流 |
| **Coroutine** | 同期コードのように書ける。Android 公式推奨 |

---

## 3. Coroutine を使う理由と、よくある誤解

### Android / Kotlin エコシステムの標準

- Google は Coroutine を [Android 開発の推奨手段](https://developer.android.com/kotlin/coroutines) として位置づけている
- Retrofit、Room、ViewModel（`viewModelScope`）など **主要ライブラリが Coroutine 前提**
- レイヤー化されたアプリでは、**domain / usecase / infra / UI の各層** で `suspend fun` や `viewModelScope` を使うのが一般的

### 誤解1: `suspend` = バックグラウンドで動く

よく勘違いされやすい点です。

`suspend` の意味は「この関数は処理の途中で **一時停止** できる」ことで、どのスレッドで処理するかなどは示していません。  

例えば、suspend メソッドである `findAllPublic` を次のように呼び出した場合、Main スレッドで実行されます。

```kotlin
viewModelScope.launch {          // ここは Main スレッド
  val list = yweetRepository.findAllPublic()  // suspend だが、Main のまま進む
  _uiState.update { ... }         // Main スレッド
}
```

`findAllPublic()` の **実装として** `withContext(Dispatchers.IO)` が呼ばれたタイミングで、初めて IO スレッドへ切り替わります。

| キーワード | 役割 |
|------------|------|
| `suspend fun` | 「待ち時間が発生しうる処理」であることを示す |
| `viewModelScope.launch { }` | ViewModel に紐づく Coroutine を起動する。`{ }` 内から `suspend fun` を呼べる |
| `withContext(Dispatchers.IO)` | **実行するスレッド** を IO に切り替える |

`suspend fun` は Coroutine の外から直接呼べません。ViewModel の公開メソッド（`onResume` など）は通常の `fun` のため、`launch { }` で Coroutine を起動してから `suspend fun` を呼びます。

**非同期処理 = Coroutine を起動すること + 必要ならスレッドを切り替えること**、とセットで理解してください。

### 誤解2: Coroutine = 新しいスレッドを作る仕組み

**これも間違いです。**

Coroutine は **処理の実行単位（軽量なタスク）** です。  
どのスレッドで動くかは **Dispatcher** が決めます。

| Dispatcher | 用途 |
|------------|------|
| `Main` | UI 更新 |
| `IO` | ネットワーク、DB、ファイル |
| `Default` | CPU 負荷の高い計算 |

一般的に、infra 層の Repository 実装で API 通信や DB アクセスを `withContext(Dispatchers.IO)` に載せ、**メインスレッドをブロックしない** ようにします。Yatter も同様です。

### ライフサイクルとの連携

ViewModel では `viewModelScope` により、**ViewModel が破棄されたら Coroutine を自動キャンセル** できます。

```kotlin
fun onResume() {
  viewModelScope.launch {
    fetchPublicTimeline()
  }
}
```

画面を離したあとも処理が走り続けると、次の不都合があります。

- 通信や CPU・バッテリーを無駄に消費する
- 不要な状態更新や後続処理が走る

画面が不要になった時点で、その画面向けの処理は止めてよい、というのが基本です。

---

## 4. レイヤーごとの Coroutine の使い方

レイヤー化アーキテクチャを採用する Android アプリでは、層ごとに Coroutine の役割が分かれます。  
以下は一般的なパターンであり、本研修の Yatter もこれに沿っています。

### 層ごとの役割

| 層 | Coroutine の使い方 | 例 |
|----|-------------------|-----|
| domain | Repository / DomainService を `suspend fun` で定義 | `suspend fun findAllPublic(): List<Yweet>` |
| usecase | `suspend fun execute()` で下位層を呼び出す | ログイン UseCase |
| infra | API 定義・Repository 実装、`withContext(IO)` | `YweetRepositoryImpl` |
| UI | `viewModelScope.launch` で Coroutine を起動 | `PublicTimelineViewModel` |

domain 層の `suspend fun` には、次の2つの意図があります。

- **時間がかかりうる操作であること** — 呼び出し側は Coroutine 内にいる必要がある、というインターフェース上の約束
- **処理の詳細を呼び出し元に意識させないこと** — データ取得が API なのか DB なのかメモリ上なのかは、呼び出し側は知る必要がない

スレッド切り替えなどの具体的な処理は、infra 層の実装に隠します。

### Query と Command で呼び出し経路が異なる

Yatter では、**データの読み取り（Query）** と **操作（Command）** で設計を分けています。

**Query（読み取り）— パブリックタイムラインなど**

```
ViewModel ──→ Repository（interface / domain）
                  └── RepositoryImpl（infra）──→ API
```

UseCase を挟まず、ViewModel から Repository を直接呼びます。

**Command（操作）— ログイン・ツイートなど**

```
ViewModel ──→ UseCase ──→ DomainService / Repository
                              └── 実装（infra）──→ API など
```

ViewModel から UseCase を経由して下位層を呼びます。UseCase がビジネスロジックをコードに落とし込みます。

### パターン A: Repository / DomainService の定義（domain 層）

```kotlin
interface YweetRepository {
  suspend fun findAllPublic(): List<Yweet>
}
```

### パターン B: UseCase（usecase 層）— Command のみ

```kotlin
override suspend fun execute(username: Username, password: Password): LoginUseCaseResult {
  try {
    if (username.value.isBlank()) return LoginUseCaseResult.Failure.EmptyUsername
    loginService.execute(username, password)
    loginUserPreferences.putUsername(username.value)
    return LoginUseCaseResult.Success
  } catch (e: Exception) {
    return LoginUseCaseResult.Failure.OtherError(e)
  }
}
```

### パターン C: API 定義・Repository 実装（infra 層）

Retrofit の API は `suspend fun` で定義します。

```kotlin
interface YatterApi {
  @GET("timelines/public")
  suspend fun getPublicTimeline(): List<YweetJson>
}
```

Repository 実装で I/O スレッドに切り替えて API を呼び出します。

```kotlin
override suspend fun findAllPublic(): List<Yweet> = withContext(Dispatchers.IO) {
  YweetConverter.convertToDomainModel(yatterApi.getPublicTimeline())
}
```

### パターン D: ViewModel（UI 層）

Android アプリでは、**ViewModel が Coroutine を起動する入口** になることが多いです。Yatter もこのパターンです。

- 公開メソッド（`onResume` など）は通常の `fun`
- `suspend` 関数は `private` にして、`viewModelScope.launch` から呼ぶ

```kotlin
fun onResume() {
  viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    try {
      fetchPublicTimeline()
    } finally {
      _uiState.update { it.copy(isLoading = false) }
    }
  }
}

private suspend fun fetchPublicTimeline() {
  val list = yweetRepository.findAllPublic()
  _uiState.update { it.copy(yweetList = convert(list)) }
}
```

`launch` は Repository 呼び出しだけでなく、`Channel.send` など **suspend 関数の呼び出し全般** に必要です。

### 呼び出しの流れ（Query の例）

パブリックタイムライン取得の場合、次の順で `suspend` が連なります。

```
1. ViewModel: viewModelScope.launch { ... }     ← Coroutine 起動（Main）
2. ViewModel: fetchPublicTimeline()             ← suspend（Main）
3. Repository: findAllPublic()                  ← suspend（Main から呼び出し）
4. RepositoryImpl: withContext(IO) { ... }      ← ここで IO スレッドへ
5. API: getPublicTimeline()                     ← suspend（IO スレッド）
6. ViewModel: _uiState.update { ... }           ← Main に戻って UI 更新
```

`withContext(Dispatchers.IO) { ... }` の `{ }` を抜けると、呼び出し元のスレッド（ここでは Main）に戻ります。  
そのため、手順 4 の API 呼び出しが終わったあとの `_uiState.update` は Main で実行され、UI を安全に更新できます。

### 覚えておく最低限のルール

1. **`suspend fun` は Coroutine 内（または別の `suspend fun` 内）からしか呼べない**
2. **`suspend` を付けただけではバックグラウンド実行にならない** — スレッド切り替えは `withContext` などで明示する
3. **Coroutine の起動（`launch`）は ViewModel など UI 層で行う**（Yatter も ViewModel が入口）
4. **UI 更新はメインスレッド**（`viewModelScope` はデフォルトで Main）
5. **ネットワーク・DB アクセスは IO スレッド**（infra 層の `withContext(Dispatchers.IO)`）

通信エラーなどは、UseCase や ViewModel で `try/catch` して結果型（`LoginUseCaseResult` など）や UI 状態に反映します（詳細は各機能の実装章）。

---

## 5. 深掘りは外部資料へ

以下は Coroutine 全体の仕様なので、必要に応じて参照してください。

- [Kotlin Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html)
- [Coroutines on Android](https://developer.android.com/kotlin/coroutines)
- [Advanced coroutines](https://developer.android.com/kotlin/coroutines/coroutines-adv)（Dispatcher 詳細）

**完全に理解する必要はありません。**  
まずは上記パターン A〜D を実装し、エラーが出たら戻って読む、で十分です。

# [次の資料](../../2.パブリックタイムライン/appendix/1_domain層実装.md)
