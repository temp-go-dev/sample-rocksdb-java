package sample.rocksdb.java.app;

import org.rocksdb.*;

public class Application {

    public static void main(String[] args) {

        RocksDB.loadLibrary();

        try (final Options options = new Options().setCreateIfMissing(true)) {

            try (final RocksDB db = RocksDB.open(options, "{{path}}")) {

                byte[] key1 = "key1".getBytes();
                byte[] key2 = "key2".getBytes();
// some initialization for key1 and key2

                try {
                    final byte[] value = db.get(key1);
                    if (value != null) {  // value == null if key1 does not exist in db.
                        String valueStr = new String(value);
                        System.out.println("valueStr -> "+valueStr);
                        db.put(key2, value);

                    } else {
                        db.put(key1, "takashimanz".getBytes());
//                        db.get
                        db.merge(key1, "takashimanozomu".getBytes());
                    }

                    final byte[] value2 = db.get(key2);
                    String valueStr2 = new String(value2);
                    System.out.println("valueStr2 -> "+valueStr2);
                    //db.remove(key1);
                } catch (RocksDBException e) {
                    // error handling
                }

                Snapshot snapshot = db.getSnapshot();
                ReadOptions ro = new ReadOptions();
                ro.setSnapshot(snapshot);
                db.get(ro, "".getBytes());
            } catch (RocksDBException e) {
                e.printStackTrace();
            }

        }


    }
}
