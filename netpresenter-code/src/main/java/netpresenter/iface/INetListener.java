package netpresenter.iface;

/**
 * @Author junweiliu
 * @Description 基础响应回调接口
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
public interface INetListener<T> {

    void onStart();

    void onFinished();

    void onSuc(T t);

    void onFail(String... str);
}
