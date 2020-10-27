package com.example.fileupload.config;

import com.example.fileupload.Cons;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: liulang
 * @Date: 2020/10/23 11:19
 */
@Configuration
@SuppressWarnings("all")
public class DYWebMvcConfigurer implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/upload1");
        //  首先  / =>(addViewControllers)=> /upload.html  =>(addResourceHandlers)=>  /static/upload.html
    }


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
