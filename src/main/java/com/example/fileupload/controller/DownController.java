package com.example.fileupload.controller;

import com.example.fileupload.service.DownService;
import com.example.fileupload.utils.RemoteFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: liulang
 * @Date: 2020/10/23 15:29
 */
@RestController
public class DownController {


    @Autowired
    private DownService downService;

    @GetMapping("down")
    public void down(@RequestParam("path")String path,@RequestParam("type")int type, HttpServletResponse response){


         downService.down(path, type, response) ;

    }
}
