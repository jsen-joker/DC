package com.dryork.zk;

import com.alibaba.dubbo.common.URL;
import com.dryork.dc.core.zk.DcZookeeperRegistry;
import org.junit.Test;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcZookeeperRegistryTest {

    @Test
    public void test() {
        String url = "dc://127.0.0.1:2181?timeout=30000";
        DcZookeeperRegistry dcZookeeperRegistry = new DcZookeeperRegistry(URL.valueOf(url));
        System.out.println("ok");
    }
}