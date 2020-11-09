package com.example.fileupload.config;

import com.example.fileupload.Cons;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Author: liulang
 * @Date: 2020/10/23 11:19
 */
//@Configuration
public class DYWebMvcConfigurer implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //项目path http://localhost:8788/


        //http://localhost:8788/upload2.png
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        //http://localhost:8788/local/upload.png
        registry.addResourceHandler("/local/**")
                .addResourceLocations("file:///"+ Cons.localPath);

        //http://localhost:8788/upload/upload3.png
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("classpath:/upload/");


    }


}
