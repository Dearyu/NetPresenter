package netpresenter.iface;


/**
 * @Author junweiliu
 * @Description 网络单元接口
 * @Version 1.0
 * @CreateDate 2021/3/10
 * @QQ 1007271386
 */
public interface INetUnit {

    INetUnit setObservable(Object observable);

    INetUnit request(INetListener observer);

    void cancelRequest();
}
