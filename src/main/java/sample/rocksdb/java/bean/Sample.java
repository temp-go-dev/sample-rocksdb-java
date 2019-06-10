package sample.rocksdb.java.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Sample implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String strProp1;

    private String strProp2;

}
