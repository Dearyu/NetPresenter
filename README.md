# NetPresenter

## 简介:
NetPresenter是一款基于retrofit2的轻量级无侵入的快速网络请求开发工具,使用 apt技术自动生成网络代理层代码,减轻编码负担,加快编码速度.



	
### 使用方法:


#### 第一步:

添加依赖

1.项目根目录中添加jitpack

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

2.开发项目中添加

```groovy
android {
  ...
  // java 1.8
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}

dependencies {
   implementation 'com.github.Dearyu:NetPresenter:1.0.7'
   annotationProcessor 'com.github.Dearyu:NetPresenter:1.0.7'
}
```

#### 第二步:

配置相关内容:

- @NetBuilder:网络构建类,需实现INetBuilder接口,提供Retrofit实例,用于构建网络请求.

- @NetUnit:网络单元类,需实现INetUnit接口,用于创建一个网络单元进行网络请求的相关操作.

- @NetListener:网络回调类,需实现INetListener接口,用于处理网络响应的回调类.

**在netpresenter-demo中提供了RxJava和retrofit2 Call的两种实现方式的例子**

这里演示用RxJava配置的例子

1.NetBuilder:

```java
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

    @Override
    public <T> T create(Class<T> t) {
        return getBaseRetrofit().create(t);
    }
}

```

2.NetUnit:

```java
@NetUnit
public class RxJavaNetUnit implements INetUnit {
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
```

3.NetListener:

```java
@NetListener
public abstract class BaseObserver<T extends BaseResponseBean> implements Observer<T>, INetListener<T> {
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
        onFinished();
        if (null != t && ("200".equals(t.getCode()))) {
            onSuc(t);
        } else {
            onFail(t.getCode(), t.getMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
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
```

#### 第三步:

使用NetPresenter

1.retrofit service:

```java
public interface DemoRxJavaService {

    @GET("demo/test")
    Observable<Object> getTestMsg(@Query("key") String key);
}
```

2.在使用DemoRxJavaService的地方添加@NetService注解,在调用DemoRxJavaService的方法前,需先调用NetPresenter.bind方法绑定Service,当然在需要关闭请求时,需使用NetPresenter.unBind方法进行关闭操作,之后可添加相应响应的回调方法(@NetCallBack)进行处理,NetPresenter规定了四种响应(CallBackType):SUC(请求成功),FAIL(失败),START(开始),FINISH(完成),这里用activity举例:

```java
public class DemoActivity extends AppCompatActivity {
  	@NetService
  	DemoRxJavaService mDemoRxJavaService;
    // bind
    private NetBinder mBind;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBind = NetPresenter.bind(this);
        mDemoRxJavaService.getTestMsg("key");
    }
     
    // DemoRxJavaService
    @NetCallBack(type = CallBackType.SUC)
    public void getDemoServiceMsgSuc(String tag, Object bean) {
        switch (tag) {
            case "getTestMsg":
                break;
                ...
        }
    }

    @NetCallBack(type = CallBackType.FAIL)
    public void getDemoServiceMsgFail(String tag, String... msgs){
      
    }
  
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mBind) {
            NetPresenter.unBind(mBind);
        }
    }
}
```

**回调方法中tag为请求方法的方法名,SUC中bean为成功响应的请求结果,Fail中msgs为响应的错误信息 可根据需求自行配置 参考示例NetListener.**




### 更多用法:


1.@NetService中可以设置value值,与@NetCallBack中value值相对应

2.@NetService中可以设置notCancel ,值为字符串数组,规定NetPresenter.unBind时不取消的请求,格式为请求方法名

```java
@NetService(value = "serviceTwo", notCancel = {"getTwoMsg"}) DemoRxJavaTwoService mDemoRxJavaTwoService;

@NetCallBack(value = "serviceTwo", type = CallBackType.SUC)
public void getDemoTwoServiceMsgSuc(String tag, Object bean) {
}

@NetCallBack(value = "serviceTwo", type = CallBackType.FAIL)
public void getDemoTwoServiceFail(String tag, String... msgs){
}

@NetCallBack(value = "serviceTwo", type = CallBackType.START)
public void getDemoTwoServiceStart(String tag) {
}

@NetCallBack(value = "serviceTwo", type = CallBackType.FINISH)
public void getDemoTwoServiceFinish(String tag) {
}
```

**更多示例 参考netpresenter-demo DemoActivity**




### Tips


- retrofit service需功能单一,不能加入其它无用成员,方法等

- retrofit service中尽量避免重载方法,如果有,确认先后执行顺序,后执行的tag格式为tag_数字(参数个数)



