package netpresenter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import netpresenter.NetPresenterConfig;

/**
 * @Author junweiliu
 * @Description 网络代理
 * @Version 1.0
 * @CreateDate 2021/3/9
 * @QQ 1007271386
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NetService {

    String value() default NetPresenterConfig.DEFAULT_VALUE;

    String[] notCancel() default {};
}
