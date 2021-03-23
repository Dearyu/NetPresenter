package com.jw.netpresenter_demo.service;


import com.jw.netpresenter_demo.bean.BaseDataBean;
import com.jw.netpresenter_demo.bean.NetMsgBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * @Author junweiliu
 * @Description java类作用描述
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
public interface DemoRxJavaTwoService {

    @GET("demo/one")
    Observable<BaseDataBean<Object>> getOneMsg(@Query("one") int one);

    @POST("demo/two")
    Observable<NetMsgBean> getTwoMsg(@Body RequestBody body);

}
