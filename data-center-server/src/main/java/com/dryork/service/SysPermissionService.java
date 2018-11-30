package com.dryork.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dryork.entity.SysPermission;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
public interface SysPermissionService extends IService<SysPermission> {
    ResponseBase createPermission(String permission);
    ResponseBase deletePermission(String permission);
    ResponseBase listPermission(int page, int capacity);

    ResponseBase listAll();
}
