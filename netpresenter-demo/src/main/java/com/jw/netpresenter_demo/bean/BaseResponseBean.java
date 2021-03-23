package com.jw.netpresenter_demo.bean;

/**
 * @Author junweiliu
 * @Description 基础响应bean
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
public abstract class BaseResponseBean {

    private String msg;

    private String code;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
