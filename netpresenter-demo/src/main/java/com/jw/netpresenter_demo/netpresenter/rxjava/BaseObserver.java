package com.jw.netpresenter_demo.netpresenter.rxjava;


import netpresenter.annotations.NetListener;
import netpresenter.iface.INetListener;
import com.jw.netpresenter_demo.bean.BaseResponseBean;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * @Author junweiliu
 * @Description 基础Observer
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
@NetListener
public abstract class BaseObserver<T extends BaseResponseBean> implements Observer<T>, INetListener<T> {
    // Tag
    private static final String TAG = "BaseObserver";
    // error
    protected String errMsg = "";
    // Disposable
    protected Disposable mDisponable;

    @Override
    public void onSubscribe(Disposable d) {
        onStart();
        mDisponable = d;
    }

    @Override
    public void onNext(T t) {
//        Log.e(TAG, "scu" + t.toString());
        onFinished();
        if (null != t && ("200".equals(t.getCode()))) {
            onSuc(t);
        } else {
            onFail(t.getCode(), t.getMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
//        Log.e(TAG, "error:" + e.toString());
        onFinished();
        if (e instanceof ConnectTimeoutException
                || e instanceof SocketTimeoutException
                || e instanceof SocketException
                || e instanceof UnknownHostException) {
            errMsg = e.getMessage();
        } else if (e instanceof HttpException) {
            errMsg = e.getMessage();
        } else {
            errMsg = "SystemException";
        }
        onFail("", errMsg);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinished() {

    }

    protected void dispose() {
        if (null != mDisponable && !mDisponable.isDisposed()) {
            mDisponable.dispose();
        }
    }
}
