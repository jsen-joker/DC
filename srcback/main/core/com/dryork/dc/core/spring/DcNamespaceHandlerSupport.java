package com.dryork.dc.core.spring;

import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcNamespaceHandlerSupport extends NamespaceHandlerSupport {
    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after
     * construction but before any custom elements are parsed.
     *
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("app", new DcBeanDefinitionParser(DcApp.class, true));
        registerBeanDefinitionParser("table", new DcBeanDefinitionParser(DcTable.class, true));
        registerBeanDefinitionParser("column", new DcBeanDefinitionParser(DcColumn.class, true));
    }
}
