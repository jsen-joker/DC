package com.dryork.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysSettings;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 * 系统设置表 服务类
 * </p>
 *
 * @author jsen
 * @since 2018-09-22
 */
public interface SysSettingsService extends IService<SysSettings> {
    ResponseBase listAll(String query);
    ResponseBase updateSysSettings(Integer id, String value);
}
