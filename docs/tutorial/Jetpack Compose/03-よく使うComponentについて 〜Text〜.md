# [前の資料](./02-Composable関数について.md)
# よく使うコンポーネントについて 〜Text編〜

JetpackComposeでUIを構築するには、コンポーネントを組み合わせることで実現します。

よく使う基本となるコンポーネントについてユースケース別に紹介します

## テキストを表示したい
テキストを表示するには、`Text`コンポーネントを使用します

```kotlin
@Composable
fun GreetingText() {
    Text("Welcome to Android World in DMM")
}
```

`Text`コンポーネントに文字列を渡すことによりテキストが表示されるようになります

![Text Sample](image/text_sample.png)

上のコードをMainActivityに追加したら、`GreetingTextPreview`を追加しましょう。

```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingTextPreview() {
    MyApplicationTheme {
        GreetingText()
    }
}
```
`@Preview`というマークが付与されたComposable関数は、Android Studioのエディタ上でプレビューを表示することができます。  
プレビュー画面が表示されない場合は、AndroidStudioの右上にある、三本線と四角形が並んだマークのボタンを押して、プレビュー画面を表示してください。  
![2-1](image/3-1.png)

---

`Text`コンポーネントには文字列リソースIDを渡すことによってもテキストを表示できます。
実際のアプリではハードコードを避けるためリソースによる指定を推奨します。

リソースで定義することで、後から文言を変更したい時にはstrings.xmlの修正のみで済むため修正箇所を局所にできます

e.g.
```kotlin
@Composable
fun GreetingText() {
    Text(stringResource(R.string.greeting))
}
```

```xml
# strings.xml

<resources>
    <string name="greeting">Welcome to Android World in DMM</string>
</resources>

```

## テキストの見た目を変えたい
`Text`には見た目を設定するためのオプショナルパラメータが複数提供されています

本サンプルでは、テキストの色の変更と文字サイズの変更に触れます


### テキストの色を変える
色を変えるには`color`パラメータを指定します

使用できるColorクラスの定義については[Colorクラスのソース](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui-graphics/src/commonMain/kotlin/androidx/compose/ui/graphics/Color.kt;l=115?q=%20androidx.compose.ui.graphics.Color&ss=androidx%2Fplatform%2Fframeworks%2Fsupport&hl=ja)をご参照ください

```kotlin
@Composable
fun GreetingText() {
    Text(
        "Welcome to Android World in DMM",
        color = Color.Red
    )
}
```
![Text Sample](image/text_red_sample.png)


### テキストの文字サイズを変える
文字サイズを変更するには`fontSize`を指定します

```kotlin
@Composable
fun GreetingTitleText() {
    Column {
        Text(
            "Welcome Android World(8sp)",
            fontSize = 8.sp
        )
        Text("Welcome Android World(default)")
        Text(
            "Welcome Android World(16sp)",
            fontSize = 16.sp
        )
        Text(
            "Welcome Android World(24sp)",
            fontSize = 24.sp
        )
    }
}
```
![Text Size Sample](image/text_size_sample.png)

## テキストを編集したい
編集可能なテキストボックスを実現するためには、`TextField`を使用します

サンプルでは以下の3つの引数を渡すことでTextFieldを構築します

- value : 表示したいテキストをStringで渡します。編集によって値が更新された際に反映されるよう、`remember`を使用します
- onValueChange : 値が変更された際に発火するリクエストイベントです。以下のサンプルでは変更後のテキストをそのまま`text`変数に格納しています
- label: TextFieldの内側に表示されるオプショナルなテキストです

```kotlin
@Composable
fun SimpleFilledTextFieldSample() {
    var text by remember { mutableStateOf("Welcome Android") }

    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Greet Label") }
    )
}
```

非フォーカス時

![TextField Sample](image/textfield_sample.png)

フォーカス時

![TextField Focus Sample](image/textfield_sample_forcus.png)


サンプルをコピペした場合、by と it と Text の下に赤い波線が表示されます。
適切な関数がインポートできておらず、エラーとなってしまうためです。
カーソルを`by`に合わせて、OptionキーとEnterキーを同時に押して次の処理を行ってください。
* `import operator 'MutableState.setValue'`
* `import operator 'State.getValue'`

こうすると、必要な関数がimportされ、使えるようになります。



本サンプルで紹介できなかった便利なパラメータやテキストのフォント変更なども可能なので、[developers guide](https://developer.android.com/jetpack/compose/text?hl=ja)もぜひご覧ください

# [次の資料](./04-よく使うComponentについて%20〜Button〜.md)
