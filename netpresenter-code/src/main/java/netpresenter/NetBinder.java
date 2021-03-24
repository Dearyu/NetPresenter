package netpresenter;

import java.util.List;

import netpresenter.iface.INetBinder;

/**
 * @Author junweiliu
 * @Description NetBinder
 * @Version 1.0
 * @CreateDate 2021/3/19
 * @QQ 1007271386
 */
public class NetBinder {

    List<netpresenter.iface.INetBinder> mINetBinders;

    public NetBinder(List<netpresenter.iface.INetBinder> INetBinders) {
        mINetBinders = INetBinders;
    }

    public List<netpresenter.iface.INetBinder> getINetBinders() {
        return mINetBinders;
    }

    public void setINetBinders(List<INetBinder> INetBinders) {
        mINetBinders = INetBinders;
    }
}
