package com.dryork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dryork.utils.TextUtils;
import org.springframework.stereotype.Service;
import com.dryork.entity.SysTemp;
import com.dryork.mapper.SysTempMapper;
import com.dryork.service.SysTempService;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 * 短信模板 服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-07-20
 */
@Service
public class SysTempServiceImpl extends ServiceImpl<SysTempMapper, SysTemp> implements SysTempService {

    @Override
    public ResponseBase list(String domain, String query) {
        QueryWrapper<SysTemp> queryWrapper = new QueryWrapper<SysTemp>(new SysTemp());
        if (!TextUtils.isEmpty(domain)) {
            queryWrapper.eq("domain", domain);
        }
        if (query != null && !"".equals(query.trim())) {
            queryWrapper.and(i -> i.like("data", query).or().like("vkey", query));
        }
        return ResponseBase.create().code(0).data(baseMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseBase add(String key, String data, String remark, String domain) {
        SysTemp sysTemp = new SysTemp().setVkey(key);
        sysTemp = baseMapper.selectOne(new QueryWrapper<>(sysTemp));
        if (sysTemp != null) {
            return ResponseBase.create().code(1).msg("模板存在");
        }
        sysTemp = new SysTemp().setVkey(key).setData(data).setRemark(remark).setType(1).setDomain(domain);
        int eff = baseMapper.insert(sysTemp);

        return ResponseBase.create().code(0).add("eff", eff);
    }

    @Override
    public ResponseBase update(Integer id, String data, String remark) {
        SysTemp sysTemp = new SysTemp().setId(id);
        sysTemp = baseMapper.selectOne(new QueryWrapper<>(sysTemp));
        if (sysTemp == null) {
            return ResponseBase.create().code(1).msg("模板不存在");
        }
        if(data != null) {
            sysTemp.setData(data);
        }
        if (remark != null) {
            sysTemp.setRemark(remark);
        }
        int eff = baseMapper.updateById(sysTemp);
        return ResponseBase.create().code(0).add("eff", eff);
    }

    @Override
    public ResponseBase delete(Integer id) {
        SysTemp sysTemp = new SysTemp().setId(id).setType(1);
        int eff = baseMapper.delete(new QueryWrapper<>(sysTemp));
        return ResponseBase.create().code(0).add("eff", eff);
    }

    @Override
    public ResponseBase listTypes() {
        return ResponseBase.create().code(0).data(baseMapper.listDistinctDomains());
    }

    @Override
    public String getTemplate(String key, String domain) {
        SysTemp sysTemp = baseMapper.selectOne(new QueryWrapper<>(new SysTemp().setDomain(domain).setVkey(key)));
        if (sysTemp != null) {
            return  sysTemp.getData();
        }
        return null;
    }
}
