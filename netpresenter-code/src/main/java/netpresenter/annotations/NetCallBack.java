package netpresenter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import netpresenter.NetPresenterConfig;

/**
 * @Author junweiliu
 * @Description 网络回调
 * @Version 1.0
 * @CreateDate 2021/3/16
 * @QQ 1007271386
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface NetCallBack {

    String value() default NetPresenterConfig.DEFAULT_VALUE;

    String tag() default NetPresenterConfig.DEFAULT_TAG;

    CallBackType type() default CallBackType.SUC;
}
