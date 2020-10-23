package com.example.fileupload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@SpringBootTest
class FileuploadApplicationTests {

    @Test
    void contextLoads() throws FileNotFoundException {
        File uploadPath = new File("rootupload");
        System.out.println(uploadPath.exists());
        File file2 = new File("classpath:/upload/upload3.png");
        System.out.println(file2.exists());
    }

}
