package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dryork.entity.SysPermission;
import com.dryork.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
@Service
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
    int insertRolePermission(SysRolePermission sysRolePermission);

    int deleteRolePermissionByRoleIdAndPermissionId(@Param("rId") int roleId, @Param("pId") int permissionId);

    int deleteRolePermissionByRId(@Param("id") int id);
    int deleteRolePermissionByPId(@Param("id") int id);

    List<SysPermission> listPermissionByRId(@Param("id") int id);

    SysRolePermission getPermissionByRoleIdAndPermissionId(@Param("rId") int role_id, @Param("pId") int permission_id);
}
