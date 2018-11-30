package com.dryork.dc.server.service.logical;

import com.alibaba.dubbo.common.URL;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-29
 */
public interface DcServerCoreService {



    /**
     * 服务端处理zk文件变化
     * @param urls
     */
    void listenColumnChange(List<URL> urls);

    /**
     * 服务端注册监听zk变化，首次lookup
     */
    void serverListen();

}
