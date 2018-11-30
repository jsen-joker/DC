package com.dryork.config;

import com.dryork.config.shiro.ShiroFilter;
import com.dryork.config.shiro.ShiroRealm;
import com.dryork.config.shiro.auth.CustomRolesAuthorizationFilter;
import com.dryork.config.shiro.conf.JSessionConf;
import com.dryork.config.shiro.conf.JShiroFilterFactoryBean;
import com.dryork.service.SysFilterChainService;
import com.dryork.service.impl.SysFilterChainServiceImpl;
import com.google.common.collect.Maps;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import javax.servlet.Filter;
import java.util.Map;

/**
 * shiro spring boot config
 * @author jsen
 * @since 2018-04-08
 */
@Configuration
@Service
public class ShiroConf {


    @Bean("sysFilterChainService")
    public SysFilterChainService getSysFilterChainService() {
        return new SysFilterChainServiceImpl();
    }


    @Bean("securityManager")
    public DefaultWebSecurityManager getManager(ShiroRealm realm, JSessionConf jSessionConf) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setSessionManager(jSessionConf);

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        manager.setSubjectDAO(subjectDAO);
        return manager;
    }


    @Bean("shiroFilter")
    public JShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        JShiroFilterFactoryBean factoryBean = new JShiroFilterFactoryBean();

        factoryBean.setSecurityManager(securityManager);


        Map<String, Filter> filterMap = Maps.newHashMap();
        filterMap.put("rolesOr", new CustomRolesAuthorizationFilter());
        filterMap.put("jwt", new ShiroFilter());
        factoryBean.setFilters(filterMap);


        factoryBean.setFilterChainDefinitionMap(getSysFilterChainService().listAll());

        factoryBean.setUnauthorizedUrl("/pub/loginFailed");

        return factoryBean;
    }



    /** * 下面的代码是添加注解支持 */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        // https://zhuanlan.zhihu.com/p/29161098
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     *  开启shiro aop注解支持.
     *  使用代理方式;所以需要开启代码支持;
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


}
