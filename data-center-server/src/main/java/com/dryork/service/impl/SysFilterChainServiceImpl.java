package com.dryork.service.impl;

import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.dryork.config.shiro.conf.JShiroFilterFactoryBean;
import com.dryork.entity.SysFilterChain;
import com.dryork.mapper.SysFilterChainMapper;
import com.dryork.service.SysFilterChainService;
import com.dryork.utils.ResponseBase;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
@Service
public class SysFilterChainServiceImpl implements SysFilterChainService {

    @Autowired
    SysFilterChainMapper sysFilterChainMapper;
    @Autowired
    @Qualifier("shiroFilter")
    @Lazy
    JShiroFilterFactoryBean shiroFilter;

    @Override
    public void reloadFilterChain() {
        synchronized (this) {
            AbstractShiroFilter sF;
            try {
                sF = (AbstractShiroFilter) shiroFilter.getObject();

                PathMatchingFilterChainResolver resolver = (PathMatchingFilterChainResolver) sF
                        .getFilterChainResolver();
                // 过滤管理器
                DefaultFilterChainManager manager = (DefaultFilterChainManager) resolver.getFilterChainManager();
                // 清除权限配置
                manager.getFilterChains().clear();
                shiroFilter.getFilterChainDefinitionMap().clear();
                // 重新设置权限
                shiroFilter.setFilterChainDefinitionMap(listAll());//传入配置中的filterchains

                Map<String, String> chains = shiroFilter.getFilterChainDefinitionMap();
                //重新生成过滤链
                if (!CollectionUtils.isEmpty(chains)) {
                    chains.forEach((url, definitionChains) -> {
                        manager.createChain(url, definitionChains.trim().replace(" ", ""));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<SysFilterChain> listAll() {
        return sysFilterChainMapper.listAll();
    }

    @Override
    public ResponseBase createFilter(String url, String filters, int sort) {
        SysFilterChain sysFilterChain = sysFilterChainMapper.getFilterByUrl(url);
        if (sysFilterChain != null) {
            return ResponseBase.create().code(1).msg("该url filter存在");
        }

        int eff = sysFilterChainMapper.insertFilter(new SysFilterChain().setUrl(url).setFilters(filters).setSort(sort));
        if (eff > 0) {
            reloadFilterChain();
        }
        sysFilterChain = sysFilterChainMapper.getFilterByUrl(url);
        return ResponseBase.create().code(0).add("eff", eff).data(sysFilterChain);
    }

    @Override
    public ResponseBase deleteByUrl(String url) {
        int eff = sysFilterChainMapper.deleteByUrl(url);
        if (eff > 0) {
            reloadFilterChain();
        }
        return ResponseBase.create().code(0).add("eff", eff);
    }

    @Override
    public ResponseBase lists() {
        return ResponseBase.create().code(0).data(listAll());
    }
}
