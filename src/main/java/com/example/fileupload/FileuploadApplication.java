package com.example.fileupload;

import com.example.fileupload.service.UploadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FileuploadApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {


        SpringApplication.run(FileuploadApplication.class, args);
    }
    @Bean
    CommandLineRunner init(final UploadService UploadService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                UploadService.deleteAll();
                UploadService.init();
            }
        };
    }
}
