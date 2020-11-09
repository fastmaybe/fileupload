package com.example.fileupload.controller;

import com.example.fileupload.config.Constants;
import com.example.fileupload.service.UploadService;
import com.example.fileupload.vo.ResultStatus;
import com.example.fileupload.vo.ResultVo;
import com.example.fileupload.vo.param.MultipartFileParam;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: liulang
 * @Date: 2020/10/23 10:32
 */
@RestController
@RequestMapping("index")
public class UploadController {

    private Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private UploadService uploadService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @PostMapping("checkFileMd5")
    public Object checkFileMd5(String md5) throws IOException {
        Object o = stringRedisTemplate.opsForHash().get(Constants.FILE_UPLOAD_STATUS, md5);
        if (o == null){
            return new ResultVo(ResultStatus.NO_HAVE);
        }

        boolean b = Boolean.parseBoolean(o.toString());
        String filePath = stringRedisTemplate.opsForValue().get(Constants.FILE_MD5_KEY + md5);

        if (b){
            return new ResultVo(ResultStatus.IS_HAVE,filePath);
        }else {
            File confFile = new File(filePath);
            byte[] uploadLogs = FileUtils.readFileToByteArray(confFile);
            List<String> missChunks = new LinkedList<>();

            for (int i = 0; i < uploadLogs.length; i++) {
                if (uploadLogs[i] != Byte.MAX_VALUE){
                    missChunks.add(i+"");
                }
            }

            return new ResultVo(ResultStatus.ING_HAVE,missChunks);
        }

    }















    @PostMapping("fileUpload")
    public ResponseEntity upload(MultipartFileParam param, HttpServletRequest request){

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart){
            logger.info("{} 文件上传开始...",param.getName());

            try {
                uploadService.uploadFileByMappedByteBuffer(param);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("文件上传失败，{}",param.toString());
                return ResponseEntity.status(500).body("文件上传失败"+e.getMessage());
            }
        }
        return ResponseEntity.ok().body("上传成功。");
    }

}
