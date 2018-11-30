package com.dryork.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysUserRole;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
public interface SysUserRoleService extends IService<SysUserRole> {
    ResponseBase createUserRole(int u_id, int r_id);
    ResponseBase createUserRoles(int u_id, JSONArray r_ids);
    ResponseBase deleteUserRole(int user_id, int role_id);
}
