package com.jw.netpresenter_demo.netpresenter.okhttpcall;

import netpresenter.iface.INetListener;
import com.jw.netpresenter_demo.bean.BaseResponseBean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @Author junweiliu
 * @Description java类作用描述
 * @Version 1.0
 * @CreateDate 2021/3/19
 * @QQ 1007271386
 */
//@NetListener
public abstract class BaseCall<T extends BaseResponseBean> implements Callback<T>, INetListener<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onFinished();
        if (200 == response.raw().code()) {
            if ("200".equals(response.body().getCode())) {
                onSuc(response.body());
            } else {
                onFail(response.body().getCode(), response.body().getMsg());
            }
        } else {
            onFail(response.raw().code() + "", response.raw().message());
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFinished();
        onFail("", t.getMessage());
    }
}
