package com.dryork.test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.registry.NotifyListener;
import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import com.dryork.dc.core.zk.DcZookeeperRegistry;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class XMLSchemaTest2 {


    @Test
    public void test() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"dc-config.xml"});

        String url = "dc://127.0.0.1:2181?timeout=30000";
        DcZookeeperRegistry dcZookeeperRegistry = new DcZookeeperRegistry(URL.valueOf(url));

        String[] allBeanNames = ctx.getBeanDefinitionNames();
        DcApp dcApp = ctx.getBean(DcApp.class);
        for (Map.Entry<String, DcTable> entry: ctx.getBeansOfType(DcTable.class).entrySet()) {
            System.out.println(entry.getValue());
        }
        dcZookeeperRegistry.subscribe(URL.valueOf("dc://DC?category=column"), new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                System.out.println(urls);
            }
        });
        for (Map.Entry<String, DcColumn> entry: ctx.getBeansOfType(DcColumn.class).entrySet()) {
            dcZookeeperRegistry.register(entry.getValue().genURL(dcApp));
        }
//        Object o = applicationContext.getBean("dc-client-1");
//        DcApp dcApp = applicationContext.getBean(DcApp.class);
//        for(String beanName : allBeanNames) {
//            Object o = ctx.getBean(beanName);
//            System.out.println(beanName);
//        }
//        ctx.start();
        System.out.println("OK");
    }

}
