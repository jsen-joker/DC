package com.dryork.dc.client.service.logical;

import com.dryork.dc.core.utils.DcRequest;
import com.dryork.dc.core.utils.DcReturnMessage;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
public interface DcClientCoreService {

    /**
     * 客户端执行sql入口
     * 注意客户端不需要执行本地更新的sql，
     * 这里远程执行成功后才会执行本地sql
     *
     * @param dcRequest
     * @param isCopy 该字段功能当前未实现，是否为复制，区别在于，复制的情况下，CUD操作成功后只会更新对应的dc字段，eg. dc_id etc.
     *               而非复制的情况下，会执行对应的CUD操作，这意味着，不需要再本地执行对应的CUD操作
     * @return
     */
    DcReturnMessage exec(DcRequest dcRequest, boolean isCopy);

    String echo(String name);

    /**
     * 客户端注册到zk
     */
    void clientRegisterToZK();

}
