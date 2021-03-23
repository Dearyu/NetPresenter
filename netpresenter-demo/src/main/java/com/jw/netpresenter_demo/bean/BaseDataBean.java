package com.jw.netpresenter_demo.bean;

/**
 * @Author junweiliu
 * @Description data响应
 * @Version 1.0
 * @CreateDate 2020/1/2
 * @QQ 1007271386
 */

public class BaseDataBean<T> extends BaseResponseBean {

    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
