# [前の資料](./1_パブリックタイムライン機能概要.md)
# パブリックタイムラインのUI層実装
パブリックタイムラインのUI層実装を行います。  

## UI層の説明
UI層は実際にユーザーが接する部分を実装します。  

クラス図では次に該当します。  
![public_timeline_UI](../uml/public_timeline_class_ui.png)

パブリックタイムライン画面のUI層のファイルは`ui/timeline`パッケージ内に作成するようにしましょう。  

## UI実装
### BindingModelの作成
まずは、BindingModelを定義します。  
BindingModelは、画面を表示する上で必要な情報をまとめた`data class`で実装されることがほとんどです。  
`Yweet`などのドメインモデルクラスをそのままUI実装に利用することもできますが、アプリの画面では複数のドメインモデルを組み合わせたり、ドメインモデルの値を加工して利用したりすることが多いため、表示する値を保持するだけのBindingModelを用意します。  
今回のパブリックタイムライン画面開発ではドメインモデルを特に加工することなく表示に利用できますが、プロジェクト全体で設計方針を統一するためにもBindingModelを実装します。  

APIレスポンスの `remote.models.Yweet` ではJsonを表現したデータモデル、ドメインの `Yweet` ではドメインモデル、`YweetBindingModel`ではuiに表示するためのデータモデルというように、責務によって使用するモデルを変換するようにしています。  

タイムライン1行分の見た目に必要な値を`ImageBindingModel`と`YweetBindingModel`に定義していきます。  
BindingModelは`ui/timeline/bindingmodel`パッケージにファイルを作成していきましょう。  

```Kotlin
package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel

data class ImageBindingModel(
  val id: String,
  val type: String,
  val url: String,
  val description: String?
)
```

```Kotlin
package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel

data class YweetBindingModel(
  val id: String,
  val displayName: String,
  val username: String,
  val avatar: String?,
  val content: String,
  val attachmentImageList: List<ImageBindingModel>
)
```

`Image`から`ImageBindingModel`に変換するための`ImageConverter`と、`Yweet`から`YweetBindingModel`に変換するための`YweetConverter`も実装しましょう。  
`ui/timeline/bindingmodel/converter`パッケージにファイルを作成していきます。  

```Kotlin
package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.Image
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.ImageBindingModel

object ImageConverter {
  fun convertToBindingModel(imageList: List<Image>): List<ImageBindingModel> =
    imageList.map { convertToBindingModel(it) }

  private fun convertToBindingModel(image: Image): ImageBindingModel = ImageBindingModel(
    id = image.id.value,
    type = image.type,
    url = image.url,
    description = image.description,
  )
}
```

```Kotlin
package com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.converter

import com.dmm.bootcamp.yatter.domain.model.Yweet
import com.dmm.bootcamp.yatter.ui.timeline.bindingmodel.YweetBindingModel

object YweetConverter {
  fun convertToBindingModel(yweetList: List<Yweet>): List<YweetBindingModel> =
    yweetList.map { convertToBindingModel(it) }

  fun convertToBindingModel(yweet: Yweet): YweetBindingModel =
    YweetBindingModel(
      id = yweet.id.value,
      displayName = yweet.user.displayName ?: "",
      username = yweet.user.username.value,
      avatar = yweet.user.avatar?.toString(),
      content = yweet.content,
      attachmentImageList = ImageConverter.convertToBindingModel(yweet.attachmentImageList)
    )
}
```

### UiStateの作成
BindingModelを定義したら続いてはUiStateを定義します。  

BindingModelは表示する値を保持する役割を持っており、UiStateはUI全体の状態を管理します。  
UiStateで保持する値は、
- 画面に表示されるデータ(BindingModel)  
- 画面のローディング状態を表すフラグ  
- エラー状態  

などその画面で起こりうるUIの状態全般です。  

そのため、パブリックタイムライン画面では次のようなUiStateが定義できます。  
- 画面に表示されるデータ  
- ローディングフラグ  
- リフレッシュフラグ  

`PublicTimelineUiState`を`ui/timeline`パッケージ内に作成します。
```Kotlin
data class PublicTimelineUiState(
  val yweetList: List<YweetBindingModel>,
  val isLoading: Boolean,
  val isRefreshing: Boolean,
)
```

詳しくは後述しますがUiStateは初期値が必要なため、emptyメソッドを用意しておきます。  
`companion object`で定義することで`PublicTimelineUiState`クラスをインスタンス化しなくともメソッドを呼び出すことができるようになっています。  

```Kotlin
data class PublicTimelineUiState(
  ...
) {
  companion object {
    fun empty(): PublicTimelineUiState = PublicTimelineUiState(
      yweetList = emptyList(),
      isLoading = false,
      isRefreshing = false,
    )
  }
}
```

ここまででUIに表示するデータを表現するクラスの準備ができました。  

### ViewModelの実装
続いては、UI表示に必要な実装を行うためのViewModelを実装します。  

まずは、ViewModelクラスを`com.dmm.bootcamp.yatter.ui.timeline`に定義します。  

```Kotlin
package com.dmm.bootcamp.yatter.ui.timeline

class PublicTimelineViewModel : ViewModel() {
  // TODO
}
```

#### UiStateの公開

ViewModelからUiStateを公開してUI実装時、UiStateにアクセスできるようにします。  
UiStateは`StateFlow`を利用して公開します。  
`StateFlow`はKotlin Coroutine Flowの一つです。`StateFlow`を利用しUI実装側で購読(収集)することにより最新の値を参照することができます。  
`StateFlow`をはじめとした`Flow`に関しては次の資料もご一読ください。  
- https://developer.android.com/kotlin/flow?hl=ja
- https://developer.android.com/kotlin/flow/stateflow-and-sharedflow?hl=ja

`StateFlow`と`MutableStateFlow`を利用します。  
`StateFlow`は保持している値がImmutableになり、初期化以降にデータの更新ができないため、更新が可能な`MutableStateFlow`と併せて利用します。  
`MutableStateFlow`単体でも動作自体はしますが、`MutableStateFlow`を外部に公開してしまうと、外部からもこの値を更新することが可能になってしまうため、外部に公開するものは更新ができない`StateFlow`、内部でロジックや状態に合わせて更新するために`MutableStateFlow`を使うという使い分け方をしています。  

次のようなコードにより、ViewModel内で更新するための`MutableStateFlow`を外部向けには`StateFlow`として見せることができます。  

```Kotlin
class PublicTimelineViewModel : ViewModel() {
  private val _uiState: MutableStateFlow<PublicTimelineUiState> =
    MutableStateFlow(PublicTimelineUiState.empty())
  val uiState: StateFlow<PublicTimelineUiState> = _uiState.asStateFlow()
}
```

このような定義の仕方を`backing properties`といいます。  
ViewModelなどのクラス内で処理を行い更新する値があり、その値を外部にも公開したいときによく用いられます。  

https://kotlinlang.org/docs/properties.html#backing-properties

#### 表示データの取得
UI構築に利用する値の準備が済み、実装に入っていきます。  
まずはYweetの一覧を取得するために`YweetRepository`を依存関係に追加します。  

```Kotlin
class PublicTimelineViewModel(
  private val yweetRepository: YweetRepository,
) : ViewModel() { ... }
```

続いてViewModel内に`YweetRepository`からYweetの一覧を取得するためのメソッドを実装します。  
非同期でViewModel内のメソッドからのみ呼び出せるように`private suspend`で実装します。  

メソッド内では以下の手順を実装します。  

1. `YweetRepository`からYweet一覧を取得
2. `PublicTimeline`内の`yweetList`を更新

```Kotlin
class PublicTimelineViewModel(...) {
  private suspend fun fetchPublicTimeline() {
    val yweetList = yweetRepository.findAllPublic() // 1
    _uiState.update {
      it.copy(
        yweetList = YweetConverter.convertToBindingModel(yweetList), // 2
      )
    }
  }
}
```

`_uiState.update {}`のように`MutableStateFlow#update`を実行することにより、現状の`MutableStateFlow`の値を利用しつつ更新することができます。  
また、`it.copy`のように`data class`の`copy`メソッドを利用することによりdata classの値の一部のみを更新することができます。  

前述したように実装した`fetchPublicTimeline`メソッドを呼び出すことにより`uiState`を最新のYweet一覧状態に更新することができるようになりました。  

続いては実際にViewModel外から呼び出すためのメソッドを実装します。  
ViewModelから公開するメソッド名は、UI側のイベントに合わせた名前にします。  
UI側のイベントに合わせた名前というのは、`on`から始まるような名前で、ボタンが押された時の`onClick~`やライフサイクルイベントに合わせた`onResume`、スワイプされた時の`onSwipe~`などなどです。
処理を行いたいUI側のイベントと一対一で対応するようなメソッドを用意します。  

ViewModelのメソッド名を処理の内容に合わせたものではなくイベントに合わせた命名にしているのには次の理由があります。  

- ユーザーのアクションに応じてどういった処理を行うべきかを呼び出し側で考えないようにしたい
- イベントに応じた処理をViewModelのテストで動作担保したい
- UI側での処理呼び忘れを防ぐ
  - 処理が複雑で複数のメソッドを呼び出す必要があるときに忘れない

今回の画面では画面を表示するたびに最新のリストを取得しておきたいため、`onResume`メソッドを用意します。  
`onResume`メソッド内では以下の処理を行います。  

1. ViewModelのライフサイクルに合わせたスコープのcoroutine起動
2. UiStateをローディング状態にする
3. `fetchPublicTimeline()`メソッドを呼び出しYweet一覧を更新
4. UiStateのローディング状態を解除する

```Kotlin
class PublicTimelineViewModel(...) {
  fun onResume() {
    viewModelScope.launch { // 1
      _uiState.update { it.copy(isLoading = true) } // 2
      fetchPublicTimeline() // 3
      _uiState.update { it.copy(isLoading = false) } // 4
    }
  }
}
```

`viewModelScope.launch`に関して補足をします。  
本来、coroutineを起動すると意図的に止めないと動き続けます。  
動き続けた場合、アプリが閉じられた後などにcoroutine内の処理が発火してエラーになってしまったりリソースを浪費したりと不都合が多くあります。  
そのためViewModelが生成・破棄されるタイミングでcoroutineを生成・キャンセルする必要があります。  
それぞれのViewModelでcoroutineの管理をすることもできますがViewModelごとに対応するのは大変なため、呼び出したViewModelが生存しているタイミングのみ動作するcoroutineを起動することができる`viewModelScope`が用意されています。  

ViewModelで非同期処理を呼び出す時は、`viewModelScope.launch`を行いViewModel用のcoroutineを扱うようにしましょう。  

詳細は次のドキュメントをご覧ください。  
https://developer.android.com/topic/libraries/architecture/coroutines?hl=ja#viewmodelscope

画面を表示するたびに取得するメソッドは実装できたため、続いては画面を下の方にスワイプして画面を更新する[PullToRefresh](https://developer.android.com/develop/ui/views/touch-and-input/swipe?hl=ja)時のメソッドを用意します。  
画面リフレッシュしたい時に呼び出されるメソッドになる想定のため名前は`onRefresh`とします。  
基本的な流れは`onResume`時と同様です。  

1. ViewModelのライフサイクルに合わせたスコープのcoroutine起動
2. UiStateをリフレッシュ状態にする
3. `fetchPublicTimeline()`メソッドを呼び出しYweet一覧を更新
4. UiStateのリフレッシュ状態を解除する

```Kotlin
class PublicTimelineViewModel(...) {
  fun onRefresh() {
    viewModelScope.launch { // 1
      _uiState.update { it.copy(isRefreshing = true) } // 2
      fetchPublicTimeline() // 3
      _uiState.update { it.copy(isRefreshing = false) } // 4
    }
  }
}
```

これでViewModelに必要な処理を実装できました。  

### UI実装に必要なクラス・ファイルを追加
ViewModelの準備ができたところでUI実装を本格的に始めていきます。  

まずは以下のファイルを作成しましょう。
- ui/timeline/PublicTimelinePage
  - パブリックタイムラインのPageを実装
- ui/timeline/PublicTimelineTemplate
  - パブリックタイムラインのTemplateを実装
- ui/timeline/YweetRow

PageとTemplateに関しては後述します。  

### UI実装
Jetpack Composeを利用したUI実装を行います。  

Jetpack Composeではコンポーザブルと呼ばれるコンポーザブル可能な関数を組み合わせることによりUIを構築します。  
コンポーザブルは次のような`@Composable`アノテーションのついた関数になります。  
本来、Kotlinの関数名は小文字始まりが一般的ですがコンポーザブルの命名は大文字始まりにします。  

```Kotlin
@Composable
fun FirstComposable() {
}
```

#### YweetRowの実装
`YweetRow`ファイルを開き、パブリックタイムライン画面で表示するYweet一覧の1行分のUIを実装します。  
次のような見た目になることを目指します。  

![yweet_preview](../image/2/status_row_preview.png)

まずは、`YweetRow`コンポーザブルを定義します。  
`ui/timeline`パッケージ内にファイルを作成します。  
1つのYweetを表示するコンポーザブルになるため、必要な値が含まれている`YweetBindingModel`を引数にとります。  

また、コンポーザブルを定義する時にはModifierも引数で受け取るようにしましょう。  
Modifierに関しては以下ドキュメントをご覧ください。  
https://developer.android.com/develop/ui/compose/modifiers?hl=ja

```Kotlin
@Composable
fun YweetRow(
  yweetBindingModel: YweetBindingModel,
  modifier: Modifier = Modifier,
) {
}
```

コンポーザブル関数の関数名と引数を決めたらPreviewの用意をすることをお勧めします。  
Previewを用意しない場合、アプリ全体をビルドしエミュレータや実機上で動かさないとどのように表示されるかという確認ができません。  
Previewを用意すると多少の変更はビルドなくとも変更を確認でき、ビルドが必要になっても該当箇所のビルドのみで反映することができるため、素早く確認しながらの実装ができます。  

Previewの利用は、`@Preview`アノテーションを使って次のようなコードを書きます。  
`@Preview`アノテーションをつけたコンポーザブル内でプレビューを表示したいコンポーザブルを呼び出すことでプレビューが表示できます。  
IDE右上に`Code`/`Split`/`Design`並んでいる箇所で`Split`を選択するとコードを書きながらプレビューを確認できるためおすすめです。  
`YweetRow`引数の`YweetBindingModel`はプレビュー用のため、適当な値で問題ありません。  

`YatterTheme`や`Surface`に関しては後述しますのでひとまずそのまま写経してください。  

```Kotlin
@Preview
@Composable
private fun YweetRowPreview() {
  YatterTheme {
    Surface {
      YweetRow(
        yweetBindingModel = YweetBindingModel(
          id = "id",
          displayName = "mito",
          username = "mitohato14",
          avatar = "https://avatars.githubusercontent.com/u/19385268?v=4",
          content = "preview content",
          attachmentImageList = listOf(
            ImageBindingModel(
              id = "id",
              type = "image",
              url = "https://avatars.githubusercontent.com/u/39693306?v=4",
              description = "icon"
            )
          )
        )
      )
    }
  }
}
```

`Preview`のコンポーザブルを用意することでプレビューを確認しながら開発ができるようになりました。  
プレビュー機能には静止画のプレビュー表示の他にアニメーションのプレビュー・実際のデバイスのように操作できるInteractive Mode・プレビューの内容をデバイス上で確認できるRun Previewがあります。  
画面右側にあるプレビュー画面にカーソルを合わせると次のようなアイコンが表示されるとおもいます。  
この3つのアイコンを選択することでアニメーションをプレビューしたりプレビューを操作してみたりデバイスで動作確認したりすることができます。  

![compose preview](../image/2/compose_preview.png)

Previewの準備ができたら、`YweetRow`コンポーザブルの実装を進めていきます。  

まずは`Row`コンポーザブルを使ってコンポーザブルを並べます。  
`Row`コンポーザブルは`{}`内のコンポーザブルを横一列に並べることができます。  
引数で受け取ったModifierはルート(ツリーの一番上)にあたるコンポーザブルに渡すことが一般的なためRowに渡しています。  
また、引数で受け取っているModifierに対して、`fillMaxWidth()`と`padding(vertical = 4.dp)`を指定しています。  
この2つを指定することで、このRowは横幅を取れるだけとり、縦方向(vertical)に4dpのpaddingを当てるというデザイン反映ができます。  
さらに、`horizontalArrangement = Arrangement.spacedBy(8.dp)`を指定して、横一列に並べるコンポーザブル同士の隙間を`8dp`空けることができます。

```Kotlin
@Composable
fun YweetRow(...) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
  }
}
```

まずはアバター画像を設置します。  
画像を表示するためのコンポーザブルとして、`Image`が用意されていますが今回表示するアバター画像はURLをもとに画像を取得して表示する必要があるため、`Image`コンポーザブルそのままでは利用できません。  
画像データのキャッシュ等を楽に管理するため、今回は[`Coil`](https://coil-kt.github.io/coil/compose/)というライブラリが提供している、`AsyncImage`を利用します。  
`AsyncImage`にURLを渡すことで自動的に画像データを読み込み表示することができます。  

Rowコンポーザブルは左から右に向けて並べるため、アバター画像はそのまま配置して問題ありません。  

```Kotlin
@Composable
fun YweetRow(...) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    AsyncImage(
      modifier = Modifier.size(48.dp),
      model = yweetBindingModel.avatar,
      contentDescription = "アバター画像",
      contentScale = ContentScale.Crop,
    )
  }
}
```

- `modifier`でアイコン画像のサイズ
- `model`に画像URLなどの画像
- `contentDescription`に画像の説明テキスト
- `contentScale`で`AsyncImage`コンポーザブルのサイズにCrop

という指定をしています。  

今回はURLから取得した画像をアバター画像として表示しますが、URLから画像取得できなかった場合を考慮して、プレイスホルダーを設定します。  
プレイスホルダー画像を`AsyncImage`に設定する場合は、`model`引数に直接URLを渡していた箇所を`ImageRequest`に置き換えます。  

```Kotlin
@Composable
fun YweetRow(...) {
  Row(...) {
    val context = LocalContext.current

    // プレイスホルダー画像の生成
    val placeholder = ResourcesCompat.getDrawable(
      context.resources,
      R.drawable.avatar_placeholder,
      null,
    )

    AsyncImage(
      modifier = Modifier.size(48.dp),
      // ImageRequestを作成して、画像取得できていない状態のプレイスホルダー設定
      model = ImageRequest.Builder(context)
        .data(yweetBindingModel.avatar)
        .placeholder(placeholder)
        .error(placeholder)
        .fallback(placeholder)
        .setHeader("User-Agent", "Mozilla/5.0") // モックサーバーから画像取得する場合のみ追加
        .build(),
      contentDescription = stringResource(id = R.string.public_timeline_avatar_content_description),
      contentScale = ContentScale.Crop,
    )
  }
}
```

画像の配置ができたら表示名とユーザー名、Yweetの内容を縦方向に並べるために`Column`を利用します。  
ここでも並べるコンポーザブル同士の間に余白をつけたいです。
`Column`では縦方向になるため、`verticalArrangement`に余白を指定します。  

```Kotlin
@Composable
fun YweetRow(...) {
  Row(...) {
    AsyncImage(...)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    }
  }
}
```

表示名とユーザー名を横に並べるために、文字を表示するためのコンポーザブルである`Text`コンポーザブルを呼び出します。  
表示名とユーザー名を横に並べ、ユーザー名のみを文字色が薄めになるようにします。  
横に並べるだけであれば、先ほども利用した`Row`が利用できそうですが、今回は表示名とユーザー名が画面からはみ出るほど長かった場合に、途中で文字列を切って、「...」で省略するために、1つの`Text`コンポーザブルで表現します。

1つの`Text`コンポーザブルで見た目の違う文字列を表現するには、[`buildAnnotatedString`](https://developer.android.com/develop/ui/compose/text/style-text?authuser=1#multiple-styles)を利用します。  
見た目を変えたい文字列部分を`withStyle`を利用して装飾することで見た目を調整します。

```Kotlin
@Composable
fun YweetRow(...) {
  Row(...) {
    AsyncImage(...)

    Column(...) {
      Text(
        text = buildAnnotatedString {
          // appendで文字列セット
          append(yweetBindingModel.displayName)
          withStyle(
            style = SpanStyle(
              // 文字色を薄くするために、ContentAlpha.mediumを指定
              color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            )
          ) {
            append(" @${yweetBindingModel.username}")
          }
        },
        maxLines = 1, // 文字列が複数行にならないように指定
        overflow = TextOverflow.Ellipsis, // はみ出した分を「...」で表現
        fontWeight = FontWeight.Bold, // 文字を太字に
      )
    }
  }
}
```

最後にYweetのコンテンツ(テキストと画像)を表示するための`Text`コンポーザブルと`AsyncImage`コンポーザブルを表示します。  
`content`は単純なテキストになるため、`Text`コンポーザブルでそのまま表示します。  
`attachmentImageList`は複数の画像が入っている場合があるため、リストに含まれる画像全てを横一列に並べ、`AsyncImage`で画像を表示します。  
画像の数がいくつになっても横一列に全て並べるために今回は`LazyRow`を利用します。  
横一列にコンポーザブルを並べるという点は`Row`と同様ですが、並べたコンポーザブルをスクロール可能にしたり数が不定なデータを効率的に表示したりするためによく利用されるコンポーザブルです。(詳細な違いは後述します)  

```Kotlin
@Composable
fun YweetRow(...) {
  Row(...) {
    AsyncImage(...)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        ...
      )
      Text(text = yweetBindingModel.content)

      LazyRow {
        // itemsの第一引数に並べたいデータセットを渡す
        items(yweetBindingModel.attachmentImageList) { attachmentImage ->
          // データ1件あたりに表示したいコンポーザブルを呼び出す
          AsyncImage(
            model = attachmentImage.url,
            contentDescription = attachmentImage.description
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
      }
    }
  }
}
```

ここまで実装できたらプレビュー上で表示が問題なさそうか確認します。  
プレビュー機能の都合上、アバター画像などのURLから画像を取得する部分は動作しませんので、プレイスホルダー画像が表示され、テキストがきちんと表示されていることが確認できれば問題ありません。  
プレビューが表示される箇所にビルドが必要とあった場合は、ビルドを行なってください。  

#### Templateの実装

Yweetの1行分のUIが構築できたところでリストの実装を`Template`にて行います。  

DMMでのJetpack Composeの実装をする上で`Page`と`Template`という概念を用います。  
この2つはAtomic Designを参考にしています。  

ざっくりとした説明にはなりますが  
`Page`では、ViewModelからJetpack Composeで構築したUIで利用する値を取得し渡す役割があります。基本的には`Template`のみを呼び出します。  
`Template`は複数のコンポーザブルを呼び出し画面全体のレイアウトをします。`Page`から渡された値を各コンポーザブルに渡す役割もあります。  
詳細は実装を進めると把握できるようになってくると思います。  

先にTemplateを実装します。  
`PublicTimelineTemplate`ファイルを開きコンポーザブルを定義します。  
このタイミングで必要になりそうな引数も定義しておきます。  
`yweetList`や`isLoading`、`isRefreshing`はUiStateにも定義されている値です。  
`onRefresh`はPullToRefreshによって画面を更新する時のトリガーとして用いられるものでラムダを受け取るようにします。  

```Kotlin
@Composable
fun PublicTimelineTemplate(
  yweetList: List<YweetBindingModel>,
  isLoading: Boolean,
  isRefreshing: Boolean,
  onRefresh: () -> Unit,
) {
}
```

定義ができたらこちらでもプレビューの用意をしてから実装に入ります。  

```Kotlin
@Preview
@Composable
private fun PublicTimelineTemplatePreview() {
  YatterTheme {
    Surface {
      PublicTimelineTemplate(
        yweetList = listOf(
          YweetBindingModel(
            id = "id1",
            displayName = "display name1",
            username = "username1",
            avatar = null,
            content = "preview content1",
            attachmentImageList = listOf()
          ),
          YweetBindingModel(
            id = "id2",
            displayName = "display name2",
            username = "username2",
            avatar = null,
            content = "preview content2",
            attachmentImageList = listOf()
          ),
        ),
        isLoading = true,
        isRefreshing = false,
        onRefresh = {}
      )
    }
  }
}
```

#### リスト表示

Jetpack Composeでスクロールが必要なリスト表示をするときは`LazyColumn`・`LazyRow`・`LazyLayout`といった`Lazy~`から始まるコンポーザブルを利用することが一般的です。特に今回のような縦方向のリストは`LazyColumn`を利用します。  
https://developer.android.com/develop/ui/compose/lists?hl=ja

`LazyColumn`を利用してリスト表示をするときは次のようなコードになります。  

```Kotlin
val list = listOf(...)
LazyColumn {
  items(list) { item ->
    RowComposable(item)
  }
}
```

`LazyColumn`のラムダ内で利用できる`items`というメソッドにリストと1行分のコンポーザブルを渡します。  
そうすることにより、`list`に含まれている要素分のコンポーザブルが表示できます。  

前述の内容を踏まえて、Yweet一覧を表示してみてください。  

実装してみると以下のような実装になります。  

```Kotlin
@Composable
fun PublicTimelineTemplate(...) {
  LazyColumn {
    items(yweetList) { item ->
      YweetRow(yweetBindingModel = item)
    }
  }
}
```

パブリックタイムライン画面ではYweetの一覧が画面全体に表示されるような見た目になりますので、`fillMaxSize()`を指定して画面全体に広がるようにします。  
画面全体に広がったままでは視認性が悪いため、`contentPadding`を指定し`LazyColumn`内の要素にpaddingがつくようにしてみます。  

```Kotlin
LazyColumn(
  modifier = Modifier.fillMaxSize(),
  contentPadding = PaddingValues(8.dp),
) {...}
```

ここまで実装するとプレビューにリストが表示されるようになったと思います。  
現状はプレビューに2件の要素を渡しているため、要素数分のリスト項目が表示されていることを確認してみてください。  

![PublicTimelineのプレビュー例](../image/2/public_timeline_preview.png)

`LazyColumn`や`LazyRow`を利用してコンポーザブルを複数並べるようなリスト表示を行いましたが、ただコンポーザブルを並べるだけであれば、表示したい要素を`forEach`で並べてスクロールさせることもできます。  

```Kotlin
Column(
  modifier = Modifier
    .verticalScroll(rememberScrollState())
) {
  yweetList.forEach { item ->
    YweetRow(yweetBindingModel = item)
  }
}
```

ただ、この方法の場合はパフォーマンス面で大きな懸念があります。  
`Lazy~`を利用しないリスト表示では、リスト全体の描画が一度に全て行われ、その全てがシステム側で保持されます。そのため、リストの要素数が1万件あった場合に1万件全ての描画・保有コストがかかるため動作が著しく重くなります。  
それに対して`Lazy~`なコンポーザブルを利用した場合には、画面に表示される分のみを描画・保持されます。その状態でスクロールすると新しく表示される要素が描画され、画面外になった要素は破棄されるため保持する内容が必要最小限に抑えられることでパフォーマンス面の問題が解決されます。スクロールするたびに発生する新規描画・破棄のパフォーマンス面のコストは気にならないほどなので問題ありません。  
主な違いや使い分けとしては以下の表のようになります。

||Row/Column|LazyRow/LazyColumn|
|-|-|-|
|スクロール可能か|Modifier設定で可能|デフォルトで可能|
|描画タイミング|一度に全てを描画|画面に表示される項目だけを描画|
|表示要素数|少ない、固定|多い、可変|
|主な利用用途|画面内に収まるような数の表示、並べることにフォーカスした表示|大規模なデータセットの表示、リストにすることにフォーカスした表示|

詳細は次のドキュメントも併せてご覧ください。  
https://developer.android.com/jetpack/compose/lists?hl=ja

---

リスト表示ができたところで、PullToRefreshでのリスト更新を実装します。  

まずは、PullToRefreshする際に表示されるローディングインディケータが表示できるように`Box`コンポーザブルで`LazyColumn`をラップします。  
また、この`Box`コンポーザブルが画面全体を覆えるように`fillMaxSize()`の指定、`Box`コンポーザブル内の要素が画面中央に配置されるように`contentAlignment`の指定をします。  

```Kotlin
fun PublicTimelineTemplate(...) {
  Box(
    modifier = Modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    LazyColumn(...)  
  }
}
```

PullToRefreshには`PullRefreshIndicator`コンポーザブルを利用します。  
このコンポーザブルを利用するためには`PullRefreshState`という状態オブジェクトが必要です。  
まずはこの状態オブジェクトを`isRefreshing`と`onRefresh`を渡して定義します。  
こうすることにより、PullToRefreshが実行されたときに、`onRefresh`の処理を発火することができます。  

```Kotlin
fun PublicTimelineTemplate(...) {
  val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
  Box(...) {
    LazyColumn(...)
  }
}
```

また、今回利用するPullToRefreshの実装はまだStableでは無いため、そのままではAndroid Studio上でエラー表示になっていると思います。  
実験的に追加されているAPIを利用するためにも、`@OptIn(ExperimentalMaterialApi::class)`を` PublicTimelineTemplate`の上部に追加して利用できるようにしましょう。  

```Kotlin
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PublicTimelineTemplate(...)
```

状態オブジェクトが定義できたら、`PullRefreshIndicator`を配置します。  
次のように先ほど定義した状態オブジェクトと`isRefreshing`、そしてPullToRefreshしたときのローディングインディケータを画面上部の真ん中に表示するために、`align(Alignment.TopCenter)`指定をします。
こうすることにより、`isRefreshing`が`true`の間にローディングインディケータが表示され、`false`になると非表示になります。  

```Kotlin
Box(...) {
  LazyColumn(...)
  PullRefreshIndicator(
    refreshing = isRefreshing,
    state = pullRefreshState,
    modifier = Modifier.align(Alignment.TopCenter)
  )
}
```

PullToRefresh実装の仕上げです。  

ユーザーが画面上部から下部に向けてスワイプして`PullToRefresh`することを検知するために、`Box`コンポーザブルに`pullRefresh(pullRefreshState)`を追加します。  

```Kotlin
Box(
  modifier = Modifier
    .fillMaxSize()
    .pullRefresh(pullRefreshState),
  contentAlignment = Alignment.Center,
) {...}
```

これにより`PullToRefresh`の実装が完了しました。  

プレビュー画面で実装できているか確認しましょう。  
プレビューのInteractive Modeを起動してパブリックタイムライン画面をクリックしたまま下に引っ張ると画面上部からローディングインディケータが降りてくると思います。  
これで`PullToRefresh`の動作確認まで完了しました。  

---

PullToRefreshでのローディング表示が実装できたため、初回読み込みなど画面全体に表示するローディングの実装を行います。  

画面全体のローディングには`CircularProgressIndicator()`が利用できます。  
`CircularProgressIndicator`をBoxコンポーザブル内に配置しローディング表示しましょう。  

```Kotlin
Box(...) {
  LazyColumn(...)
  PullRefreshIndicator(...)

  CircularProgressIndicator()
}
```

ここのままだと常にローディングインディケータが表示されるため、`isLoading`が`true`な時のみ表示するようにif文で分岐させます。  

```Kotlin
Box(...) {
  LazyColumn(...)
  PullRefreshIndicator(...)

  if (isLoading) {
    CircularProgressIndicator()
  }
}
```

`PublicTimelineTemplatePreview`コンポーザブルに設定している`isLoading`の値を`true`と`false`を切り替えてみて画面中央にローディングインディケータが表示されることを確認してみてください。  

プレビュー機能の`Run Preview`で実際にデバイス上で動かした方がローディングインディケータが動いているところが確認しやすいです。  

ここまでで、リスト表示が完了しました。  

#### Scaffold

リスト表示までは完了しましたが、このままではリストだけの殺風景な画面になってしまっていますので、`Scaffold`コンポーザブルを利用してよくあるアプリのような画面にします。  
`Scaffold`コンポーザブルとは、`TopAppBar`、`BottomAppBar`、`FloatingActionButton`、`Drawer`などの一般的なマテリアルデザインのレイアウトを提供します。  
`Scaffold`コンポーザブルの適した引数に適したコンポーザブルを渡すことで現在のAndroidアプリにおいて一般的なデザインであるマテリアルデザインに則ったレイアウトをすることが可能です。  

Yatterアプリにおいても、画面上部の`TopAppBar`、画面右下の`FloatingActionButton`を実装する予定のため`Scaffold`を用います。  
これまで実装していたコンポーザブルのルートである`Box`コンポーザブルを`Scaffold`でラップします。  

ラップした際に`Box`コンポーザブル周辺に赤い波線が引かれていると思います。  
`Scaffold`を使うときには、`Scaffold`から渡される`PaddingValues`を利用する必要があります。  
この`PaddingValues`は`Scaffold`に設定される要素に応じて適切な値が渡されるため、`Scaffold`を利用する際はきちんと利用するようにしましょう。  

```Kotlin
@Composable
fun PublicTimelineTemplate(...) {
  Scaffold() { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
        .padding(paddingValues),
    ) {...}
  }
}
```
まずは`TopAppBar`を配置して、`タイムライン`という文字列を表示するようにします。  
`Scaffold`の`topBar`引数にラムダでコンポーザブルを配置します。  

```Kotlin
@Composable
fun PublicTimelineTemplate(...) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = "タイムライン")
        },
      )
    }
  ) {...}
}
```

プレビューを確認すると画面上部に「タイムライン」と書かれたバーが表示されるようになったと思います。  

ここまで実装できたら、Activityと`PublicTimelineTemplate`を繋ぎ込みます。  

`PublicTimelineTemplate`でYweet一覧の実装はできましたが、このコンポーザブルはまだどこからも呼び出されていないため、アプリを実行しても表示されることはありません。  
そのため、`Template`コンポーザブルを呼び出す必要があります。  

#### コンポーザブルの繋ぎ込み

まずは、`Template`コンポーザブルと`Page`コンポーザブルを繋ぎこみます。  

`PublicTimelinePage`ファイルを開いて、`PublicTimelinePage`コンポーザブルを定義します。  

```Kotlin
@Composable
fun PublicTimelinePage() {
}
```

`Page`コンポーザブルは特にUIを実装しないため、今回はプレビューを省略します。  

前述もしましたが、`Page`コンポーザブルの役目はViewModelから状態を取り出し、`Template`コンポーザブルに渡すことですので、`PublicTimelinePage`引数にViewModelを取ります。  

```Kotlin
@Composable
fun PublicTimelinePage(
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
}
```

ViewModelを引数で受け取ったら、ViewModelから状態を抜き出します。  
今回の設計では画面に表示するための状態をUiStateとしてまとめていますので、まずは次のコードでUiStateを抜き出します。  

```Kotlin
val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()
```

このコードを記載することで`publicTimelineViewModel#uiState`の変更を監視することができ、更新をUIに反映することができます。  

UiStateの監視準備ができたところで`Page`コンポーザブルから`Template`コンポーザブルを呼び出し、渡せる値は引数に渡します。  

```Kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PublicTimelinePage(
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
  val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()

  PublicTimelineTemplate(
    yweetList = uiState.yweetList,
    isLoading = uiState.isLoading,
    isRefreshing = uiState.isRefreshing,
    onRefresh = ,
  )
}
```

`onRefresh`はViewModelのメソッドになるため、`UiState`からは取得できません。  
メソッドを引数として渡すときは関数オブジェクトとして渡す必要があります。  

関数オブジェクトとして渡すには`::`をメソッド名の前に利用します。  
今回は特に`ViewModel`内のメソッドのため、`publicTimelineViewModel::onRefresh`と記載します。  

```Kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PublicTimelinePage(
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
  val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()

  PublicTimelineTemplate(
    yweetList = uiState.yweetList,
    isLoading = uiState.isLoading,
    isRefreshing = uiState.isRefreshing,
    onRefresh = publicTimelineViewModel::onRefresh,
  )
}
```

次に、`onResume`のライフサイクルイベントでViewModelの`onResume`を呼び出すようにします。

`LifecycleEventEffect`を使うことによって、指定したライフサイクルのイベントごとに処理を実行させることができます。

```kotlin
@Composable
fun PublicTimelinePage(
  publicTimelineViewModel: PublicTimelineViewModel = koinViewModel(),
) {
  val uiState by publicTimelineViewModel.uiState.collectAsStateWithLifecycle()
  
  LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
    publicTimelineViewModel.onResume()
  }

  PublicTimelineTemplate(
    yweetList = uiState.yweetList,
    isLoading = uiState.isLoading,
    isRefreshing = uiState.isRefreshing,
    onRefresh = publicTimelineViewModel::onRefresh,
  )
}
```

`PublicTimelinePage`コンポーザブルではデフォルト引数でViewModelをDIから取得しますが、まだ `ViewModelModule`に登録していないため、このままでは実行時にエラーになります。`koinViewModel()`の説明は [Koinを使ったDI](../../tutorial/DIについて/3_Koinを使ったDI.md) を参照してください。

`di/ViewModelModule.kt`を開き、`PublicTimelineViewModel`のコメントアウトを外します（手順の全体像は [DI実装](./appendix/3_DI実装.md)）。

```kotlin
internal val viewModelModule = module {
//  viewModel { MainViewModel(get()) }
  viewModel { PublicTimelineViewModel(get()) } // コメントアウトを外す
//  viewModel { PostViewModel(get(), get()) }
//  viewModel { RegisterViewModel(get()) }
//  viewModel { LoginViewModel(get()) }
}
```

これでViewModel, Pageコンポーザブル, Templateコンポーザブルの繋ぎこみが完了しました。  

---

最後にActivityとPageコンポーザブルとの繋ぎこみを行います。  

`MainActivity`ファイルを開き、`onCreate`メソッドを探します。  

`onCreate`メソッドは画面が生成されるときに呼び出されるライフサイクルメソッドで、UIの実装や呼び出しもこのタイミングで行います。  

ActivityからJetpack Composeで構築したUIを呼び出すには`setContent {}`を利用します。  
次のようなコードを記載して呼び出すようにしましょう。  

```Kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)

  setContent {
    YatterTheme {
      Surface {
        PublicTimelinePage()
      }
    }
  }
}
```

`YatterTheme`と`Surface`が再度出てきました。  

この2つのメソッドはアプリ全体で利用する色や文字スタイル、UIコンポーネントの形状といったものを管理してアプリ全体を一つのテーマで統一するために用いられるものです。  

この2つを利用しないと、1つのアプリなのに画面によって使われている色や見た目が違っていたり、統一するためにボイラーコードを多く書く必要が出てきます。  
それらを簡潔にするために利用されます。  

`YatterTheme`でアプリ全体のテーマ（色や文字スタイル、UIコンポーネントの形状を含みます）を管理するコンポーザブルで、デフォルトの場合`${プロジェクト名}Theme`といった命名でプロジェクト作成時に自動生成されています。  
`Theme.kt`というファイルでテーマが定義されていますので、アプリ全体の色を変えたい時などに変更してみてください。  

`Surface`コンポーザブルでは、ダークテーマ対応をはじめとする背景色に対して適切なコンテンツカラーを利用できるようにサポートするコンポーザブルです。  
例えば白背景の上には黒文字といったコントラスト比を意識した画面は`Surface`を利用しなくとも実装できます。ですが、デバイスのダークテーマに合わせて背景色を黒をベースにした色に変わったときに文字色が黒文字のままで見えにくい・見えない画面になってしまいます。  
そこで`Surface`コンポーザブルを利用することで黒背景に対しては白文字など、背景色が変更されてもコントラスト比を保ったまま表示ができるようにしています。  

## Appendix
今回、UI構築を行う際にタイトルや`contentDescription`を文字列で直書きしています。  
ただ、実務のAndroidアプリ開発では文字列リソースとして`strings.xml`というファイルで一元管理することが多いです。  

文字列リソースとして扱う利点として次の内容があります。  

- 同じ文字列を表示している箇所の変更時に修正が一箇所で済む
- 複数言語対応時に端末の設定に応じて自動で言語選択される

文字列リソースは`res/values/strings.xml`で扱います。  
次のように定義・利用します。  

```XML
<string name="string_resource_id">表示する文字列</string>
```

```Kotlin
// Jetpack Compose
stringResource(id = R.string.string_resource_id)

// Android View
context.getString(R.string.string_resource_id)
```

今回は資料の見やすさのため直書きしていますが、文字列リソースを利用して実装もしてみましょう。  

詳しい内容は公式ドキュメントをご一読ください。  
https://developer.android.com/guide/topics/resources/string-resource?hl=ja

# [Appendixへ](./appendix/1_domain層実装.md)
# [次の章へ](../3.ログイン/1_ログイン機能概要.md)
