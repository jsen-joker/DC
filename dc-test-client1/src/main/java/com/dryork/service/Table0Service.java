package com.dryork.service;

import com.dryork.entity.Table0;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-11-20
 */
public interface Table0Service extends IService<Table0> {
    ResponseBase add();
    ResponseBase del(Integer id);
    ResponseBase update(Integer id);
}
