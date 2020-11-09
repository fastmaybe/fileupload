package com.example.fileupload.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @Author: liulang
 * @Date: 2020/11/6 16:32
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@SuppressWarnings("all")
public enum  ResultStatus {


    /**
     * 1 开头为判断文件在系统的状态
     */
    IS_HAVE(100, "文件已存在！"),

    NO_HAVE(101, "该文件没有上传过。"),

    ING_HAVE(102, "该文件上传了一部分。");



    ResultStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    private final int value;

    private final String reasonPhrase;

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }





}
