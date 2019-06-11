package sample.rocksdb.java.app;

import org.apache.commons.lang3.SerializationUtils;
import org.rocksdb.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application2 {

    public static void main(String[] args) {

        RocksDB.loadLibrary();

        try (ColumnFamilyOptions columnFamilyOptions = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            List<ColumnFamilyDescriptor> columnFamilyDescriptors = Arrays.asList(
                    new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, columnFamilyOptions),
                    new ColumnFamilyDescriptor("column-family1".getBytes(), columnFamilyOptions),
                    new ColumnFamilyDescriptor("column-family2".getBytes(), columnFamilyOptions)
            );

            List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();

            try (DBOptions options = new DBOptions().setCreateIfMissing(true).setCreateMissingColumnFamilies(true);
                 RocksDB rocks = RocksDB.open(options, "column-family-db", columnFamilyDescriptors, columnFamilyHandles)) {
                // column handle
                ColumnFamilyHandle defaultColumnFamilyHandle = columnFamilyHandles.get(0);
                ColumnFamilyHandle columnFamily1Handle = columnFamilyHandles.get(1);
                ColumnFamilyHandle columnFamily2Handle = columnFamilyHandles.get(2);

                Object obj = new Object();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try {
                    FileOutputStream fos = new FileOutputStream(new File(""));

                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(obj);
                    oos.flush();
                    baos.flush();
                    baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // put
                rocks.put(defaultColumnFamilyHandle,
                        "key".getBytes(StandardCharsets.UTF_8), "value-default".getBytes(StandardCharsets.UTF_8));
                rocks.put(columnFamily1Handle,
                        "key".getBytes(StandardCharsets.UTF_8), "value-column-family1".getBytes(StandardCharsets.UTF_8));
                rocks.put(columnFamily2Handle,
                        "key".getBytes(StandardCharsets.UTF_8), "value-column-family2".getBytes(StandardCharsets.UTF_8));



            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
