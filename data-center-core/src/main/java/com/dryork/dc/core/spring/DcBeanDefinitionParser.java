package com.dryork.dc.core.spring;

import com.dryork.dc.core.entity.DcApp;
import com.dryork.dc.core.entity.DcColumn;
import com.dryork.dc.core.entity.DcTable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * </p>
 *
 * @author jsen
 * @since 2018-11-26
 */
public class DcBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;
    private final boolean required;

    public DcBeanDefinitionParser(Class<?> beanClass, boolean required) {
        this.beanClass = beanClass;
        this.required = required;
    }

    /**
     * Parse the specified {@link Element} and register the resulting
     * {@link BeanDefinition BeanDefinition(s)} with the
     * {@link ParserContext#getRegistry() BeanDefinitionRegistry}
     * embedded in the supplied {@link ParserContext}.
     * <p>Implementations must return the primary {@link BeanDefinition} that results
     * from the parse if they will ever be used in a nested fashion (for example as
     * an inner tag in a {@code <property/>} tag). Implementations may return
     * {@code null} if they will <strong>not</strong> be used in a nested fashion.
     *
     * @param element       the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
     * @param parserContext the object encapsulating the current state of the parsing process;
     *                      provides access to a {@link BeanDefinitionRegistry}
     * @return the primary {@link BeanDefinition}
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element, parserContext, beanClass, "", required);
    }

    private static BeanDefinition parse(Element element, ParserContext parserContext, Class<?> beanClass, String prefix, boolean required) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);

        String id = null;
        if (DcApp.class.equals(beanClass)) {
            String name = element.getAttribute("name");
            String type = element.getAttribute("type");
            id = name + "dc-app";
            beanDefinition.getPropertyValues().addPropertyValue("name", name);
            beanDefinition.getPropertyValues().addPropertyValue("type", type);
        } else if (DcTable.class.equals(beanClass)) {
            String local = element.getAttribute("local");
            String remote = element.getAttribute("remote");
            id = local + "-dc-table";
            beanDefinition.getPropertyValues().addPropertyValue("local", local);
            beanDefinition.getPropertyValues().addPropertyValue("remote", remote);
            parseNested(element, parserContext, DcColumn.class, true, "column", "dcTable", id, beanDefinition);
        } else if (DcColumn.class.equals(beanClass)) {
            String local = element.getAttribute("local");
            id = local + prefix + "-dc-column";
            String remote = element.getAttribute("remote");
            String key = element.getAttribute("key");
            String groupType = element.getAttribute("groupType");
            String group = element.getAttribute("group");
            String type = element.getAttribute("type");
            beanDefinition.getPropertyValues().addPropertyValue("local", local);
            beanDefinition.getPropertyValues().addPropertyValue("remote", remote);
            beanDefinition.getPropertyValues().addPropertyValue("key", key);
            beanDefinition.getPropertyValues().addPropertyValue("groupType", groupType);
            beanDefinition.getPropertyValues().addPropertyValue("group", group);
            beanDefinition.getPropertyValues().addPropertyValue("type", type);
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }
        return beanDefinition;
    }

    private static void parseNested(Element element, ParserContext parserContext, Class<?> beanClass, boolean required,
                                    String tag, String property, String ref, BeanDefinition beanDefinition) {
        NodeList nodeList = element.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if (tag.equals(node.getNodeName())
                            || tag.equals(node.getLocalName())) {
                        BeanDefinition subDefinition = parse((Element) node, parserContext, beanClass, "-" + ref, required);
                        if (subDefinition != null && ref != null && ref.length() > 0) {
                            subDefinition.getPropertyValues().addPropertyValue(property, new RuntimeBeanReference(ref));
                        }
                    }
                }
            }
        }
    }
}
