package com.dryork.dc.core.service.dubbo;

import com.dryork.dc.core.utils.DcMessage;
import com.dryork.dc.core.utils.DcReturnMessage;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-27
 */
public interface DcDubboExecuteService {

    /**
     * 核心远程执行入口
     * @param dcMessage
     * @return
     */
    DcReturnMessage execute(DcMessage dcMessage);

    String echo(String name);

}
