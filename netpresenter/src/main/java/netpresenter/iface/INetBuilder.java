package netpresenter.iface;

/**
 * @Author junweiliu
 * @Description 网络构造
 * @Version 1.0
 * @CreateDate 2021/3/15
 * @QQ 1007271386
 */
public interface INetBuilder {

    <T> T create(Class<T> t);
}
