package org.lmmarise.springfamework.orm.common;

import java.io.Serializable;

/**
 * 统一结果返回顶层设计
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/25 7:58 下午
 */
public class ResultMsg<T> implements Serializable {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResultMsg.class);

    private int status;     // 状态码
    private String msg;     // 状态码解释
    private T data;         // 任意结果

    public ResultMsg() {
    }

    public ResultMsg(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultMsg(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResultMsg(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
