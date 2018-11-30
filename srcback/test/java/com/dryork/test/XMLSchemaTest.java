package com.dryork.test;

import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:dc-config.xml"})
public class XMLSchemaTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void test() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
//        Object o = applicationContext.getBean("dc-client-1");
//        DcApp dcApp = applicationContext.getBean(DcApp.class);
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
        System.out.println("OK");
    }

}
