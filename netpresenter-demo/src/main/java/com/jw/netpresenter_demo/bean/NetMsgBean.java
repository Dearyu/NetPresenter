package com.jw.netpresenter_demo.bean;

import java.util.List;

/**
 * @Author junweiliu
 * @Description 网络信息实体
 * @Version 1.0
 * @CreateDate 2021/3/19
 * @QQ 1007271386
 */
public class NetMsgBean extends BaseResponseBean {

    private String name;

    private List<String> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
