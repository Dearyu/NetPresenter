package com.jw.netpresenter_demo.service;


import com.jw.netpresenter_demo.bean.BaseDataBean;
import com.jw.netpresenter_demo.bean.NetMsgBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * @Author junweiliu
 * @Description java类作用描述
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
public interface DemoRxJavaService {

    @GET("demo/test")
    Observable<BaseDataBean<Object>> getTestMsg(@Query("key") String key);

    @GET("demo/test")
    Observable<NetMsgBean> getTestMsg(@Query("key") String key, @Query("value") String value);

}
