package sample.rocksdb.java.inspect;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.rocksdb.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * ■ファイルについて
 * ・キー値は数値
 * <p>
 * ■手順
 * 1. もろもろすべてクリア
 * 2. CSVファイル作成
 * 3. 読み込んでSSTファイル作成
 * 4. SSTファイルをロード
 * <p>
 * ■観点
 * ・正常に読み込めることを確認
 * ・SSTファイルのロードにてファイル間でキー範囲を跨るとエラーとなることを確認
 * <p>
 * ■確認できたこと
 * ・SSTファイルを作成する際にキーは順序がソートされていなければならない.
 * ・SSTファイルをロードする際にキーは重複していてはならない.
 * ・SSTファイルのロード失敗は、ColumnFamilyが別だと問題ないだろうが、SSTファイル作成時にColumnFamilyが指定できない
 */
public class InspectSSTCreateAndLoad_Sample03 {

    private static final int TARGET_NUM = 999;

    /**
     * サンプルファイル
     */
    private static final String SAMPLE_FILE_001 = "samples/sample03/files/sample001.csv";
    private static final String SAMPLE_FILE_002 = "samples/sample03/files/sample002.csv";
    private static final String SAMPLE_FILE_003 = "samples/sample03/files/sample003.csv";

    /**
     * SSTファイル
     */
    private static final String SAMPLE_SST_001 = "samples/sample03/sst/sst001.sst";
    private static final String SAMPLE_SST_002 = "samples/sample03/sst/sst002.sst";
    private static final String SAMPLE_SST_003 = "samples/sample03/sst/sst003.sst";

    /**
     * データベースパス
     */
    private static final String SAMPLE_DATABASE_DIR = "samples/sample03/database";


    public static void main(String[] args) throws IOException {

        RocksDB.loadLibrary();

        // クリア
        clear();

        // サンプルファイル作成
        System.out.println("create sample file.");
        createSampleFiles();

        // SSTファイル作成
        System.out.println("write sample sst file.");
        writeSampleSST();

        // SSTファイル読み込み
        try (final Options options = new Options().setCreateIfMissing(true)) {

            try (final RocksDB db = RocksDB.open(options, SAMPLE_DATABASE_DIR)) {

                IngestExternalFileOptions ingestExternalFileOptions = new IngestExternalFileOptions();
                db.ingestExternalFile(
                        Arrays.asList(new String[]{SAMPLE_SST_001, SAMPLE_SST_002, SAMPLE_SST_003}),
                        ingestExternalFileOptions);
                assert false;
            } catch (RocksDBException e) {
                System.out.println("ファイルを跨ぐとRangeが入り混じるためエラーとなる.");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * ディレクトリクリア.
     */
    private static void clear() {
        try {
            Files.deleteIfExists(Paths.get(SAMPLE_FILE_001));
            Files.deleteIfExists(Paths.get(SAMPLE_FILE_002));
            Files.deleteIfExists(Paths.get(SAMPLE_FILE_003));
            Files.deleteIfExists(Paths.get(SAMPLE_SST_001));
            Files.deleteIfExists(Paths.get(SAMPLE_SST_002));
            Files.deleteIfExists(Paths.get(SAMPLE_SST_003));
            FileUtils.deleteDirectory(Paths.get(SAMPLE_DATABASE_DIR).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * サンプルファイル作成.
     * 常に上書き.
     */
    private static void createSampleFiles() {

        try {

            List<String> values1 = new ArrayList<>();
            List<String> values2 = new ArrayList<>();
            List<String> values3 = new ArrayList<>();

            DecimalFormat decimalFormat = new DecimalFormat("00000");

            for (int i = 1; i <= TARGET_NUM; i++) {
                StringJoiner joiner1 = new StringJoiner(",");
                joiner1.add("1" + decimalFormat.format(i));
                joiner1.add("col001_" + RandomStringUtils.randomAlphanumeric(50));
                joiner1.add("col002_" + RandomStringUtils.randomAlphanumeric(50));
                values1.add(joiner1.toString());

                StringJoiner joiner2 = new StringJoiner(",");
                joiner2.add("2" + decimalFormat.format(i));
                joiner2.add("col003_" + RandomStringUtils.randomAlphanumeric(50));
                joiner2.add("col004_" + RandomStringUtils.randomAlphanumeric(50));
                values2.add(joiner2.toString());

                StringJoiner joiner3 = new StringJoiner(",");
                joiner3.add("3" + decimalFormat.format(i));
                joiner3.add("col005_" + RandomStringUtils.randomAlphanumeric(50));
                joiner3.add("col006_" + RandomStringUtils.randomAlphanumeric(50));
                values3.add(joiner3.toString());
            }

            Files.createDirectories(Paths.get(SAMPLE_FILE_001).getParent());

            // TODO: この処理を加えることによって、sample001.csvには201000というキーを最終行に含むことになる.
            StringJoiner joiner2 = new StringJoiner(",");
            joiner2.add("2" + decimalFormat.format(1000));
            joiner2.add("col003_" + RandomStringUtils.randomAlphanumeric(50));
            joiner2.add("col004_" + RandomStringUtils.randomAlphanumeric(50));
            values1.add(joiner2.toString());
            Files.write(Paths.get(SAMPLE_FILE_001), values1, StandardCharsets.UTF_8);
            Files.createDirectories(Paths.get(SAMPLE_FILE_002).getParent());
            Files.write(Paths.get(SAMPLE_FILE_002), values2, StandardCharsets.UTF_8);
            Files.createDirectories(Paths.get(SAMPLE_FILE_003).getParent());
            Files.write(Paths.get(SAMPLE_FILE_003), values3, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("fail sample file create.", e);
        }
    }

    /**
     * ファイルの内容をSSTファイルへ書き込み.
     */
    private static void writeSampleSST() {

        // TODO: このあたりのオプションを掘り下げるべき?
        final EnvOptions envOptions = new EnvOptions();
        final StringAppendOperator stringAppendOperator = new StringAppendOperator();
        final Options options = new Options().setMergeOperator(stringAppendOperator);
        SstFileWriter sstFileWriter = new SstFileWriter(envOptions, options);

        Path sampleSST001Path = Paths.get(SAMPLE_SST_001);
        Path sampleSST002Path = Paths.get(SAMPLE_SST_002);
        Path sampleSST003Path = Paths.get(SAMPLE_SST_003);

        try {
            Files.createDirectories(sampleSST001Path.getParent());
        } catch (IOException e) {
            throw new RuntimeException("fail create parent directory.", e);
        }

        try {

            // サンプルファイル1を読み込んで、SSTファイルを作成
            sstFileWriter.open(sampleSST001Path.toAbsolutePath().toString());
            List<String> lines1 = Files.readAllLines(Paths.get(SAMPLE_FILE_001), StandardCharsets.UTF_8);
            // TODO: parallelで処理するとキーがソートされなくなるため、エラーとなる
            lines1.stream().sorted()/*.parallel()(*/.forEach(line -> {
                byte[] keyBytes = line.split(",")[0].getBytes();
                byte[] valueBytes = (line.split(",")[1] + "\t" + line.split(",")[2]).getBytes();
                // Slice keySlice = new Slice(line.split(",")[0]);
                // Slice valueSlice = new Slice(line.split(",")[1] + "\t" + line.split(",")[2]);
                try {
                    // sstFileWriter.put(keySlice, valueSlice);
                    sstFileWriter.put(keyBytes, valueBytes);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            sstFileWriter.finish();

            // サンプルファイル2を読み込んで、SSTファイルを作成
            sstFileWriter.open(sampleSST002Path.toAbsolutePath().toString());
            List<String> lines2 = Files.readAllLines(Paths.get(SAMPLE_FILE_002), StandardCharsets.UTF_8);
            lines2.stream().forEach(line -> {
                byte[] keyBytes = line.split(",")[0].getBytes();
                byte[] valueBytes = (line.split(",")[1] + "\t" + line.split(",")[2]).getBytes();
                try {
                    sstFileWriter.put(keyBytes, valueBytes);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            sstFileWriter.finish();

            // サンプルファイル3を読み込んで、SSTファイルを作成
            sstFileWriter.open(sampleSST003Path.toAbsolutePath().toString());
            List<String> lines3 = Files.readAllLines(Paths.get(SAMPLE_FILE_003), StandardCharsets.UTF_8);
            lines3.stream().forEach(line -> {
                byte[] keyBytes = line.split(",")[0].getBytes();
                byte[] valueBytes = (line.split(",")[1] + "\t" + line.split(",")[2]).getBytes();
                try {
                    sstFileWriter.put(keyBytes, valueBytes);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
            sstFileWriter.finish();


        } catch (RocksDBException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
