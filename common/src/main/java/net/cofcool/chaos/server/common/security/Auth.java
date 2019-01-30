package net.cofcool.chaos.server.common.security;


import java.io.Serializable;

/**
 * 授权管理
 *
 * @param <D> 用户相关数据
 * @param <ID> ID
 *
 * @author CofCool
 */
public interface Auth<D extends Serializable, ID extends Serializable> extends Serializable {

    ID getId();

    /**
     * 读取用户相关数据，如商户数据等
     * @return D 用户相关数据
     */
    D getData();

}
