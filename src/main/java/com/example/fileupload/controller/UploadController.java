package com.example.fileupload.controller;

import com.example.fileupload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @Author: liulang
 * @Date: 2020/10/23 10:32
 */
@RestController
public class UploadController {


    @Autowired
    private UploadService uploadService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("upload")
    public String upload(@RequestParam("readme") int type, @RequestParam("file") MultipartFile file){

        System.out.println();
       return uploadService.upload(file,type);
//        return "OK";
    }
    @GetMapping("/path")
    public String upload() {
        String path1= ClassUtils.getDefaultClassLoader().getResource("upload").getPath();
        String path = request.getSession().getServletContext().getRealPath("") + "upload";
        String path2 = request.getSession().getServletContext().getContextPath();

        File file = new File(path, "upload3.png");


        System.out.println(file.exists());
        System.out.println("path"+path);
        System.out.println("path1"+path1);
        System.out.println("path2"+path2);
        return "OK";
    }
}
