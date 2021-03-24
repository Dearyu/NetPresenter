package com.jw.netpresenter_demo.netpresenter;

import netpresenter.annotations.NetBuilder;
import netpresenter.iface.INetBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author junweiliu
 * @Description NetManager
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
@NetBuilder
public class NetManager implements INetBuilder {

    public static final String BaseUrl = "http://0.0.0.0/";

    public static Retrofit mRetrofit;

    public static Retrofit getBaseRetrofit() {
        if (null == mRetrofit) {
            mRetrofit = getRetrofit();
        }
        return mRetrofit;
    }

    /**
     * Retrofit  Rxjava
     *
     * @return
     */
    public static Retrofit getRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        return new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * Retrofit Call
     *
     * @return
     */
    public static Retrofit getRetrofitForCall() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        return new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public <T> T create(Class<T> t) {
        return getBaseRetrofit().create(t);
    }
}
