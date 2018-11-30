package com.dryork.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysRole;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
public interface SysRoleService extends IService<SysRole> {

    ResponseBase createRole(String value);
    ResponseBase deleteRole(String value);
    ResponseBase listRole(int page, int capacity);

    ResponseBase listAll();

}
