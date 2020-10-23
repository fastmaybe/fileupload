package com.example.fileupload.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: liulang
 * @Date: 2020/10/23 15:20
 */
public class RemoteFileUtils {


    /**
     * 下载文件
     * @param remoteFilePath
     * @param localFilePath
     */
    public static void downloadRemoteFile(String remoteFilePath,String localFilePath){
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File f = new File(localFilePath);
        try
        {
            urlfile = new URL(remoteFilePath);
            httpUrl = (HttpURLConnection)urlfile.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(f));
            int len = 2048;
            byte[] b = new byte[len];
            while ((len = bis.read(b)) != -1)
            {
                bos.write(b, 0, len);
            }
            bos.flush();
            bis.close();
            httpUrl.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                bis.close();
                bos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        downloadRemoteFile("http://10.113.23.33:9344/3,01026efe05ba","G:\\uploaddemo\\remote.jpg");
    }



}
