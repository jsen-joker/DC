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
     * @param dcRequest
     * @return
     */
    DcReturnMessage exec(DcRequest dcRequest);

    /**
     * 客户端注册到zk
     */
    void clientRegisterToZK();

}
