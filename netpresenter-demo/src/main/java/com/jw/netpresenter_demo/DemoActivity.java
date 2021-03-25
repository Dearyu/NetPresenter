package com.jw.netpresenter_demo;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.jw.netpresenter_demo.bean.BaseDataBean;
import com.jw.netpresenter_demo.bean.NetRequest;
import com.jw.netpresenter_demo.service.DemoRxJavaService;
import com.jw.netpresenter_demo.service.DemoRxJavaTwoService;

import androidx.appcompat.app.AppCompatActivity;
import netpresenter.NetBinder;
import netpresenter.NetPresenter;
import netpresenter.annotations.CallBackType;
import netpresenter.annotations.NetCallBack;
import netpresenter.annotations.NetService;
import okhttp3.RequestBody;

public class DemoActivity extends AppCompatActivity {
    // TAG
    private static final String TAG = "DemoActivity";
    // default
    @NetService
    DemoRxJavaService mDemoRxJavaService;
    // value and notcancel value为标签和回调的@NetCallBack标签保持一致 notCancel为unBind时不取消的请求数组(格式是方法名)
    @NetService(value = "serviceTwo", notCancel = {"getTwoMsg"})
    DemoRxJavaTwoService mDemoRxJavaTwoService;
    // bind
    private NetBinder mBind;

    // retrofit call 使用call时 需打开@NetUnit @NetListener
//    @NetService(value = "callService")
//    DemoCallService mDemoCallService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = NetPresenter.bind(this);
        mDemoRxJavaService.getTestMsg("key");
        mDemoRxJavaService.getTestMsg("key", "value");
        mDemoRxJavaTwoService.getOneMsg(1);
        mDemoRxJavaTwoService.getTwoMsg(RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(new NetRequest())));
//        mDemoCallService.getCallMsg("key");
    }

    // DemoRxJavaService
    @NetCallBack
    public void getDemoServiceMsgSuc(String tag, Object bean) {
        Log.d(TAG, "getDemoServiceMsgSuc: " + "\ntag:" + tag + "\nmsg:" + bean.toString());
        switch (tag) {
            case "getTestMsg":
                Object obj = ((BaseDataBean<Object>) bean).getData();
                break;
            case "getTestMsg_2":  // 尽量避免重载方法 如果有 确认先后执行顺序 后执行的tag格式为后缀tag_数字(参数个数)
                break;
        }
    }

    @NetCallBack(type = CallBackType.FAIL)
    public void getDemoServiceMsgFail(String tag, String... msgs) {
        Log.d(TAG, "getDemoServiceMsgFail: " + "\ntag:" + tag + "\nmsg:" + msgs.toString());
    }

    // DemoRxJavaTwoService 回调参数需按照格式返回
    @NetCallBack(value = "serviceTwo", type = CallBackType.SUC)
    public void getDemoTwoServiceMsgSuc(String tag, Object bean) {
    }

    @NetCallBack(value = "serviceTwo", type = CallBackType.FAIL)
    public void getDemoTwoServiceFail(String tag, String... msgs) {
    }

    @NetCallBack(value = "serviceTwo", type = CallBackType.START)
    public void getDemoTwoServiceStart(String tag) {
    }

    @NetCallBack(value = "serviceTwo", type = CallBackType.FINISH)
    public void getDemoTwoServiceFinish(String tag) {
    }

    // DemoCallService
//    @NetCallBack(value = "callService", type = CallBackType.FAIL)
//    public void getDemoCallServiceMsgFail(String tag, String... msgs) {
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBind) {
            NetPresenter.unBind(mBind);
        }
    }
}