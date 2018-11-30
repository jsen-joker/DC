package com.dryork.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysTemp;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 * 短信模板 服务类
 * </p>
 *
 * @author jsen
 * @since 2018-07-20
 */
public interface SysTempService extends IService<SysTemp> {

    ResponseBase list(String domain, String query);

    ResponseBase add(String key, String data, String remark, String domain);

    ResponseBase update(Integer id, String data, String remark);

    ResponseBase delete(Integer id);

    ResponseBase listTypes();

    String getTemplate(String key, String domain);

}
