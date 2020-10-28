package com.example.fileupload.controller;

import com.example.fileupload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: liulang
 * @Date: 2020/10/23 10:32
 */
@RestController
public class UploadController {


    @Autowired
    private UploadService uploadService;



    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file){

       return uploadService.upload(file,1);
//        return "OK";
    }
    @GetMapping("/upload")
    public void upload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //取出监听器MyProgress在session中保存的进度信息
        String progress=(String) req.getSession().getAttribute("progress");
        //响应
        resp.getWriter().print(progress);
    }
}
