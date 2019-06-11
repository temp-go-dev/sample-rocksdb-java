# RocksDB

## RocksDBの調査
GoLangで組み込み実装を行いたかったが、
現状敷居が高いため、一旦Javaでの機能検証をメインで進めることにした。
そのためのプロジェクトが本プロジェクト。

## 含んでいるサンプルコード

含んでいるサンプルは、GettingStandard的なもの以外に、
検証依頼を受けたものが入っている。

### Getting Standard
Javaアプリケーションの起動にて確認

```
sample.rocksdb.java.app.Application
```

単純にRocksDBを利用してみたもの。
Keyに対して値を入れて、取り出してみる。という行為。

### SSTファイルの作成＋SSTファイルのロード
https://github.com/facebook/rocksdb/wiki/Creating-and-Ingesting-SST-files

#### 正常に作成→ロードするパターン
Javaアプリケーションの起動にて確認

```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample01
```

#### 正常に作成→ロードできないパターン
Javaアプリケーションの起動にて確認

- 作成したCSVファイルのキー値が半角英数字の乱数となっているためソートされていない
- SSTファイル作成時にはソート処理を含むため作成は完了している
- SSTファイルのロード時にはファイルを跨ったキーのソートを行っていないためエラーとなる

```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample02
```

#### 正常に作成→ロードできないパターン
Javaアプリケーションの起動にて確認

- 作成したCSVファイルのキー値はすべて数値であるが、ファイル１とファイル２を跨って数値が昇順となっていない
- ファイル1（1000001～201000）
- ファイル2（2000001～200999）
- ファイル3（3000001～300999）
- SSTファイルのロード時にはファイルを跨ったキーのソートを行っていないためエラーとなる

```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample03
```

