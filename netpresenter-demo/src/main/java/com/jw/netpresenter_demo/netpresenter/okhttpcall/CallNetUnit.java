package com.jw.netpresenter_demo.netpresenter.okhttpcall;

import netpresenter.iface.INetListener;
import netpresenter.iface.INetUnit;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @Author junweiliu
 * @Description java类作用描述
 * @Version 1.0
 * @CreateDate 2021/3/19
 * @QQ 1007271386
 */
//@NetUnit
public class CallNetUnit implements INetUnit {

    Call mCall;

    @Override
    public INetUnit setObservable(Object observable) {
        mCall = (Call) observable;
        return this;
    }

    @Override
    public INetUnit request(INetListener observer) {
        observer.onStart();
        if (null != mCall) {
            mCall.enqueue((Callback) observer);
        }
        return this;
    }

    @Override
    public void cancelRequest() {
        if (null != mCall && !mCall.isCanceled()) {
            mCall.cancel();
        }
    }
}
