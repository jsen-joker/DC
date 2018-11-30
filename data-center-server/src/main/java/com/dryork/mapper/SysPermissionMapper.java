package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dryork.entity.SysPermission;
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
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    int insertPermission(SysPermission sysPermission);

    int deletePermission(@Param("permission") String permission);

    List<SysPermission> listPage(Page pagination);

    int countPermission();

    SysPermission getPermissionByPermission(@Param("permission") String permission);

    SysPermission getPermissionById(@Param("id") int id);

    List<SysPermission> listAll();
}
