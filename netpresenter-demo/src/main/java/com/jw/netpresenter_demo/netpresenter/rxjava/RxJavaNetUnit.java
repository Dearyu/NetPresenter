package com.jw.netpresenter_demo.netpresenter.rxjava;

import netpresenter.annotations.NetUnit;
import netpresenter.iface.INetListener;
import netpresenter.iface.INetUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author junweiliu
 * @Description RxJava网络单元
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
@NetUnit
public class RxJavaNetUnit implements INetUnit {
    // TAG
    private static final String TAG = "RxJavaNetUnit";
    // Observable
    protected Observable mObservable;
    // Observer
    protected BaseObserver mObserver;

    @Override
    public INetUnit setObservable(Object observable) {
        mObservable = ((Observable) observable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return this;
    }

    @Override
    public INetUnit request(INetListener observer) {
        if (null == mObservable) {
            throw new IllegalArgumentException("Not Fund INetUnit");
        }
        mObserver = (BaseObserver) observer;
        mObservable.subscribe(mObserver);
        return this;
    }

    @Override
    public void cancelRequest() {
        if (null != mObserver) {
            mObserver.dispose();
        }
    }
}
