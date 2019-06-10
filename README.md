# RocksDBを扱うサンプル

## 含んでいるサンプルコード

### SSTファイルの作成＋SSTファイルのロード
https://github.com/facebook/rocksdb/wiki/Creating-and-Ingesting-SST-files

#### 正常に作成→ロードするパターン
```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample01
```
#### 正常に作成→ロードできないパターン
- 作成したCSVファイルのキー値が半角英数字の乱数となっているためソートされていない
- SSTファイル作成時にはソート処理を含むため作成は完了している
- SSTファイルのロード時にはファイルを跨ったキーのソートを行っていないためエラーとなる

```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample02
```

#### 正常に作成→ロードできないパターン
- 作成したCSVファイルのキー値はすべて数値であるが、ファイル１とファイル２を跨って数値が昇順となっていない
- ファイル1（1000001～201000）
- ファイル2（2000001～200999）
- ファイル3（3000001～300999）
- SSTファイルのロード時にはファイルを跨ったキーのソートを行っていないためエラーとなる

```
sample.rocksdb.java.inspect.InspectSSTCreateAndLoad_Sample03
```

ダンプ？
https://github.com/facebook/rocksdb/wiki/Administration-and-Data-Access-Tool

https://protonail.com/
https://fastonosql.com/anonim_users_downloads
