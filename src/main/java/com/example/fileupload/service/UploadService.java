package com.example.fileupload.service;


import com.example.fileupload.Cons;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @Author: liulang
 * @Date: 2020/10/23 10:34
 */
@Service
public class UploadService {



    public String upload(MultipartFile file,int type){
        System.out.println(file.getName());

        try {
            switch (type){
                case 1: return saveFile1(file);


                case 2:return saveFile2(file);

                case 3:return saveFile3(file);

                default:
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





        return "NO";
    }

    //方式一  transferTo
    private String  saveFile1(MultipartFile file) throws IOException {
        File saveFile = new File(Cons.localPath + File.separator + System.currentTimeMillis() + file.getOriginalFilename());

        file.transferTo(saveFile);

        return saveFile.getName();
    }

    //方式二 原生流
    private String  saveFile2(MultipartFile file) throws IOException {
        File saveFile = new File(Cons.rootupload + File.separator + System.currentTimeMillis() + file.getOriginalFilename());

        InputStream is = file.getInputStream();

        FileOutputStream fos = new FileOutputStream(saveFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        byte[] bytes = new byte[1024];
        int ret = 0;
        while ((ret=is.read(bytes))!= -1){
            bos.write(bytes,0,ret);
        }
        return saveFile.getName();
    }

    //方式三  apache FileUtils
    private String  saveFile3(MultipartFile file) throws IOException {
        String path1= ClassUtils.getDefaultClassLoader().getResource("upload").getPath();

        File saveFile = new File(path1 + File.separator + System.currentTimeMillis() + file.getOriginalFilename());

        org.apache.commons.io.FileUtils.copyToFile(file.getInputStream(),saveFile);
        return saveFile.getName();
    }

    //等等....



}
