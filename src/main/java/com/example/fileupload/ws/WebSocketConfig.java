package com.example.fileupload.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author wd
 * @Program common-web
 * @create 2019-06-28 15:33
 */
@Configuration
public class WebSocketConfig {

   /*
   * 使用内置tomcat容器时需要开启这个bean，而用外置的tomcat时需要注释掉这个bean
   *
   * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
    */


   @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}