package com.dryork.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysUser;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
public interface SysUserService extends IService<SysUser> {
    ResponseBase createUser(String name);
    ResponseBase deleteUser(String name);
    ResponseBase listUser(int page, int capacity);

    ResponseBase login(String username, String password);
}
