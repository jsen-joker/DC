package com.dryork.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysRolePermission;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {
    ResponseBase createRolePermission(int r_id, int p_id);
    ResponseBase createRolePermissions(int r_id, JSONArray p_idList);
    ResponseBase deleteRolePermission(int roleId, int permissionId);

}
