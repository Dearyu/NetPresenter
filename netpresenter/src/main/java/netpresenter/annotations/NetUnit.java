package netpresenter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author junweiliu
 * @Description 网络单元
 * @Version 1.0
 * @CreateDate 2021/3/9
 * @QQ 1007271386
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface NetUnit {

}
