package com.dryork.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.dryork.entity.SysSettings;
import com.dryork.mapper.SysSettingsMapper;
import com.dryork.service.SysSettingsService;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 * 系统设置表 服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-09-22
 */
@Service
public class SysSettingsServiceImpl extends ServiceImpl<SysSettingsMapper, SysSettings> implements SysSettingsService {

    @Override
    public ResponseBase listAll(String query) {
        QueryWrapper<SysSettings> queryWrapper = new QueryWrapper<>();
        if (query != null && !"".equals(query.trim())) {
            queryWrapper.like("vkey", query).or().like("remark", query);
        }
        return ResponseBase.create().code(0).data(baseMapper.selectList(queryWrapper));
    }

    @Override
    public ResponseBase updateSysSettings(Integer id, String value) {
        SysSettings sysSettings = baseMapper.selectById(id);
        if (sysSettings == null) {
            return ResponseBase.create().code(1).msg("找不到该系统设置");
        }

        sysSettings.setValue(value);

        baseMapper.updateById(sysSettings);
        return ResponseBase.create().code(0);
    }
}
