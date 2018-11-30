package com.dryork.test;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 19/11/2018
 */
public class URLTest {
    @Test
    public void test() throws MalformedURLException {
        URL url = new URL("jdbc:mysql://localhost:3306/dryork_dc?useUnicode=true&characterEncoding=UTF-8".replace("jdbc:mysql", "http"));
        System.out.print(url.getPath().split("/")[1]);
    }
}
