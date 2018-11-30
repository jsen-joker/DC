package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dryork.entity.SysTemp;

import java.util.List;

/**
 * <p>
 * 短信模板 Mapper 接口
 * </p>
 *
 * @author jsen
 * @since 2018-07-20
 */
public interface SysTempMapper extends BaseMapper<SysTemp> {
    List<String> listDistinctDomains();
}
