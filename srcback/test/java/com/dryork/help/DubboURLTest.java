package com.dryork.help;

import com.alibaba.dubbo.common.URL;
import org.junit.Test;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DubboURLTest {

    @Test
    public void test() {
        String url = "dc://127.0.0.1:2181";
        URL url1 = URL.valueOf(url);
        String bc = url1.getBackupAddress();
        assert bc != null;
    }
}
