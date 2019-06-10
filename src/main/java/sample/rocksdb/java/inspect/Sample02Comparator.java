package sample.rocksdb.java.inspect;

import java.util.Comparator;

public class Sample02Comparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        String[] o1Array = o1.split(",");
        String[] o2Array = o2.split(",");
        return o1Array[0].compareTo(o2Array[0]);
    }
}
