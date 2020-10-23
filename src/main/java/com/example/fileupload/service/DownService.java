package com.example.fileupload.service;

import com.example.fileupload.utils.RemoteFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @Author: liulang
 * @Date: 2020/10/23 15:32
 */
@Service
public class DownService {


    public boolean down(String path, int type, HttpServletResponse response) {

        switch (type){
            case 1 :  return doDown1(path,response);
            case 2 :  return doDown2(path,response);
            default:
        }
      return false;
    }
    /**
     *             场景一   要下载远程路径的文件
     *      *  例如 文件已经存在某个地方 我们已经知道其路径 或者可以查到其路径 且知道是什么文件了
     *      *
     *      *  //处理方式  一： 直接连接远程 path 获取流 直接流刷入 response  如doDown1
     *      *
     *      *  //处理方式 二： 先下载在临时目录 然后再放回去   如doDown2
     *
     *              场景二   下载 后台服务器上文件 直接获取流 丢进去
     *
     *
     *              场景三   下载 文件服务器文件
     *                      1 获取文件流   response丢回去 (未尝试 待验证)
     *                      2 下载在服务器 临时文件  然后再返回去
     *
     *
     */

    /**
     * @param path
     * @param response
     */
    private boolean doDown1(String path, HttpServletResponse response) {

        //一般是 这里一般是知道 或者可以查到 文件名 现在假设 查出来文件叫   文档.pdf

        try {
            URL urlfile    = new URL(path);
            HttpURLConnection   httpUrl = (HttpURLConnection)urlfile.openConnection();
            httpUrl.connect();
            InputStream is = httpUrl.getInputStream();
            ServletOutputStream outputStream = response.getOutputStream();

            String fileName = URLEncoder.encode("文档.pdf","UTF-8");

            //TODO 这两行如果注释掉  就是直接返回流 但不会下载  浏览器会直接展示文件
//            response.setContentType("application/force-download");
//            response.setHeader("Content-Disposition","attachent;filename="+fileName);
            IOUtils.copy(is,outputStream);

            response.setContentLength(is.available());

            outputStream.flush();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean doDown2(String path, HttpServletResponse response) {

        //一般是 这里一般是知道 或者可以查到 文件名 现在假设 查出来文件叫   文档.pdf
        String fileName = "qqqqqqqqqq.pdf";
        FileInputStream is =null;
        String localPath =null;
        try {
            String fileNameUtf8 = URLEncoder.encode(fileName,"UTF-8");
            //下载再临时目录
             localPath = System.getProperty("java.io.tmpdir") + fileNameUtf8;
            System.err.println(localPath);
            RemoteFileUtils.downloadRemoteFile(path,localPath);

            //
             is = new FileInputStream(localPath);

            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition","attachent;filename="+fileNameUtf8);
            response.setContentLength(is.available());
            //
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(is,out);
            out.flush();




            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if (is!=null){
                try {
                    is.close();
                    FileUtils.forceDelete(new File(localPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
