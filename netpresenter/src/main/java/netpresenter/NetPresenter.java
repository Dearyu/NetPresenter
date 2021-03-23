package netpresenter;

import netpresenter.annotations.NetService;
import netpresenter.iface.INetBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author junweiliu
 * @Description NetPresenter
 * @Version 1.0
 * @CreateDate 2021/3/9
 * @QQ 1007271386
 */
public final class NetPresenter {

    private static final Map<Class<?>, Constructor<? extends INetBinder>> NETBINDINGS = new LinkedHashMap<>();

    public static netpresenter.NetBinder bind(Object target) {
        return new netpresenter.NetBinder(createNetPresenterForClass(target));
    }

    private static List<INetBinder> createNetPresenterForClass(Object target) {
        List binders = new ArrayList();
        Class<?> cls = target.getClass();
        for (Field field : cls.getDeclaredFields()) {
            NetService netService = field.getAnnotation(NetService.class);
            if (null != netService) {
                Constructor<? extends INetBinder> bindingCtor = NETBINDINGS.get(field.getType());
                if (null == bindingCtor) {
                    try {
                        Class<?> bindingClass = cls.getClassLoader().loadClass(field.getType().getName() + "_NetPresenter");
                        bindingCtor = (Constructor<? extends INetBinder>) bindingClass.getConstructor(Object.class, String[].class);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    NETBINDINGS.put(field.getType(), bindingCtor);
                }
                INetBinder binder = null;
                try {
                    binder = bindingCtor.newInstance(target, netService.notCancel());
                    field.setAccessible(true);
                    field.set(target, binder);
                } catch (NullPointerException e) {
                    throw new NetPresenterException("Can't find" + field.getType().getName() + "_NetPresenter");
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (null != binder) {
                    binders.add(binder);
                }
            }
        }
        return binders;
    }

    public static void unBind(NetBinder binders) {
        for (INetBinder binder : binders.getINetBinders()) {
            binder.unbind();
        }
    }
}

