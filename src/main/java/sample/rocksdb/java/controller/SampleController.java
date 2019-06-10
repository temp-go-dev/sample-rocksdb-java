package sample.rocksdb.java.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.rocksdb.java.bean.Sample;
import sample.rocksdb.java.service.SampleService;

@RestController
@RequestMapping(value = "sample")
public class SampleController {

    @Autowired
    SampleService sampleService;

    @GetMapping
    public ResponseEntity<Sample> get() {
        Sample sample = new Sample();
        sample.setStrProp1("hoge");
        sample.setStrProp2("fuga");
        return new ResponseEntity<Sample>(sample, HttpStatus.OK);
    }

}
