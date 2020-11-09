package com.example.fileupload.service;


import com.example.fileupload.config.Constants;
import com.example.fileupload.utils.FileMD5Util;
import com.example.fileupload.vo.param.MultipartFileParam;
import com.jcraft.jsch.IO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * @Author: liulang
 * @Date: 2020/10/23 10:34
 */
@Service
public class UploadService {

    private final Logger logger = LoggerFactory.getLogger(UploadService.class);
    // 保存文件的根目录
    private Path rootPaht;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    public UploadService(@Value("${breakpoint.upload.dir}") String location) {
        this.rootPaht = Paths.get(location);
    }


    //这个必须与前端设定的值一致
    @Value("${breakpoint.upload.chunkSize}")
    private long CHUNK_SIZE;

    @Value("${breakpoint.upload.dir}")
    private String finalDirPath;


    /**
     *RandomAccessFile
     * @param param
     */
    public void uploadFileRandomAccessFile(MultipartFileParam param) throws IOException {
        String fileName = param.getName();
        //获取上传路径的文件夹
        String uploadDirPath = finalDirPath + param.getMd5();
        //临时文件名称
        String temFileName = fileName + "_tmp";

        File temDir = new File(uploadDirPath);
        File temFile = new File(uploadDirPath, temFileName);
        if (!temDir.exists()){
            temDir.mkdirs();
        }

        RandomAccessFile accessTmpFile = new RandomAccessFile(temFileName, "rw");

        //定位分块的偏移量
        long offset = param.getChunk() * CHUNK_SIZE;
       accessTmpFile.seek(offset);
       //写入
        byte[] data = param.getFile().getBytes();
        accessTmpFile.write(data);
        //释放
        accessTmpFile.close();

        //检查是否长传完毕
        boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);
        if (isOk){
            //上传完毕  重命名
            boolean flag = renameFile(temFile, fileName);
            if (flag){
                //删除conf文件
                File file = new File(uploadDirPath, fileName + ".conf");
                FileUtils.forceDelete(file);
            }
        }

    }

    /**
     * MappedByteBuffer   速度快一点
     * @param param
     * @throws IOException
     */
    public void uploadFileByMappedByteBuffer(MultipartFileParam param) throws IOException {
        String fileName = param.getName();

        //获取上传的文件夹路径
        String uploadDirPath = finalDirPath + param.getMd5();
        //临时文件名称
        String temFileName = fileName + "_tmp";

        File tmpDir = new File(uploadDirPath);
        File tmpFile = new File(uploadDirPath, temFileName);

        if (!tmpDir.exists()){
            tmpDir.mkdirs();
        }

        //获取文件通道
        FileChannel fileChannel = new RandomAccessFile(tmpFile, "rw").getChannel();

        //写入数据分片 获取偏移两
        long offset = CHUNK_SIZE * param.getChunk();
        byte[] dataBytes = param.getFile().getBytes();
        MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, dataBytes.length);

        mbb.put(dataBytes);

        //释放
        FileMD5Util.freedMappedByteBuffer(mbb);
        fileChannel.close();

        //检查是都上传完毕
        boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);

        if (isOk){
            boolean flag = renameFile(tmpFile,fileName);
            //重命名
            if (flag){
                File file = new File(uploadDirPath, fileName + ".conf");
                //删掉conf记录文件
                FileUtils.forceDelete(file);
            }
        }


    }




    /**
     * 检查文件 并且修改文件进度记录
     * @param param
     * @param uploadDirPath
     * @return
     */
    private boolean checkAndSetUploadProgress(MultipartFileParam param, String uploadDirPath) throws IOException {
        String fileName = param.getName();
        File confFile = new File(uploadDirPath, fileName + ".conf");
        RandomAccessFile accessFile = new RandomAccessFile(confFile, "rw");

        //标记该分段为完成的标记
        //设置总字节长为 分片大小长度
        accessFile.setLength(param.getChunks());
        //找到当前分片对应的未知
        accessFile.seek(param.getChunk());
        accessFile.writeByte(Byte.MAX_VALUE);

        //然后检查此记录 看看是否全部完成
        byte[] uploadLogs = FileUtils.readFileToByteArray(confFile);

        //类似
        boolean isComplete = true;

        for (int i = 0; i < uploadLogs.length && isComplete; i++) {
            isComplete = uploadLogs[i] == Byte.MAX_VALUE;
        }

        accessFile.close();

        if (isComplete){
            //完成了
            //更新状态
            stringRedisTemplate.opsForHash().put(Constants.FILE_UPLOAD_STATUS,param.getMd5(),"true");
            //更新文件
            stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY+param.getMd5(),uploadDirPath+"/"+fileName);
            return true;
        }else {
            if (!stringRedisTemplate.opsForHash().hasKey(Constants.FILE_UPLOAD_STATUS,param.getMd5())){
                stringRedisTemplate.opsForHash().put(Constants.FILE_UPLOAD_STATUS,param.getMd5(),"false");
            }
            if (!stringRedisTemplate.hasKey(Constants.FILE_MD5_KEY + param.getMd5())){
                stringRedisTemplate.opsForValue().set(Constants.FILE_MD5_KEY+param.getMd5(),uploadDirPath+"/"+fileName+".conf");
            }
            return false;
        }

    }

    private boolean renameFile(File toBeRenamed, String toFileNewName) {
        //检查文件是否存在
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()){
            logger.error("file is not exists");
            return false;
        }
        String p = toBeRenamed.getParent();
        File newFile = new File(p + File.separator + toFileNewName);

        return toBeRenamed.renameTo(newFile);
    }










    public void init() {
        try {
            Files.createDirectory(rootPaht);
        } catch (FileAlreadyExistsException e) {
            logger.error("文件夹已经存在了，不用再创建。");
        } catch (IOException e) {
            logger.error("初始化root文件夹失败。", e);
        }
    }
    public void deleteAll() {
        logger.info("开发初始化清理数据，start");
        FileSystemUtils.deleteRecursively(rootPaht.toFile());
        stringRedisTemplate.delete(Constants.FILE_UPLOAD_STATUS);
        Set<String> keys = stringRedisTemplate.keys(Constants.FILE_MD5_KEY+"*");
        stringRedisTemplate.delete(keys);
        logger.info("开发初始化清理数据，end");
    }

}
