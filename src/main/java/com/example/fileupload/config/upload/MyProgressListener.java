package com.example.fileupload.config.upload;



import org.apache.commons.fileupload.ProgressListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.NumberFormat;

/**
 * @Author: liulang
 * @Date: 2020/10/27 16:02
 */
public class MyProgressListener implements ProgressListener {

    private HttpSession session;

    public MyProgressListener(HttpServletRequest request){
        session = request.getSession();
    }


    /**
     *
     * @param pBytesRead 已经读取的
     * @param pContentLength  总共大小
     * @param pItems 读取的第几个文件
     */
    @Override
    public void update(long pBytesRead, long pContentLength, int pItems) {
        //将数据进行格式化
        //已读取数据由字节转换为M
        double readM=pBytesRead/1024.0/1024.0;
        //已读取数据由字节转换为M
        double totalM=pContentLength/1024.0/1024.0;

        //已读取百分百
        double percent=readM/totalM;

        //格式化数据
        //已读取
        String readf=dataFormat(pBytesRead);
        //总大小
        String totalf=dataFormat(pContentLength);
        //进度百分百
        NumberFormat format= NumberFormat.getPercentInstance();
        String progress=format.format(percent);


        System.err.println(progress);
        session.setAttribute("progress",progress);

    }


    /**
     * 格式化读取数据的显示 单位byte
     * @param
     * @return 格式化后的数据，如果小于1M显示单位为KB，如果大于1M显示单位为M
     */
    public String dataFormat(double data){
        String formdata="";
        if (data>=1024*1024) {//大于等于1M
            formdata=Double.toString(data/1024/1024)+"M";
        }else if(data>=1024){//大于等于1KB
            formdata=Double.toString(data/1024)+"KB";
        }else{//小于1KB
            formdata=Double.toString(data)+"byte";
        }
        return formdata.substring(0, formdata.indexOf(".")+2);
    }
}
