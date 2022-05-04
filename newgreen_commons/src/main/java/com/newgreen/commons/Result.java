package com.newgreen.commons;

import java.io.Serializable;

/**
 * 返回秒杀前端的消息封装
 */
public class Result implements Serializable {

    private int code;//返回的业务码  200：成功执行  403：发生错误
    private String message;//信息
    //存储其他信息
    private Object other;


    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.code=0;
        this.message = "执行成功";
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
