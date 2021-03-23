package netpresenter.compiler;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.google.auto.common.MoreElements.getPackage;

/**
 * @Author junweiliu
 * @Description 工具类
 * @Version 1.0
 * @CreateDate 2021/3/15
 * @QQ 1007271386
 */
public class NetPresenterUtil {

    public static ClassName getNetClassName(TypeElement typeElement) {
        String packageName = getPackage(typeElement).getQualifiedName().toString();
        String className = typeElement.getQualifiedName().toString().substring(
                packageName.length() + 1);
        return ClassName.get(packageName, className + "_NetPresenter");
    }

    public static String extractMessage(String msg) {
        if (isEmpty(msg)) {
            return "";
        }
        return msg.substring(msg.indexOf('<') + 1, msg.length() - 1);
    }

    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }
}
