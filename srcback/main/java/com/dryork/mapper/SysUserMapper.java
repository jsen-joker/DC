package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dryork.entity.SysPermission;
import com.dryork.entity.SysRole;
import com.dryork.entity.SysUser;
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
public interface SysUserMapper extends BaseMapper<SysUser> {

    List<SysRole> getRoleByUserId(@Param("id") int id);
    List<SysPermission> getPermissionByRoleId(@Param("id") int id);

    int insertUser(SysUser sysUser);
    int deleteUser(@Param("name") String name);

    List<SysUser> listPage(Page pagination);

    int countUser();

    SysUser getUserByName(@Param("name") String name);
    SysUser getUserById(@Param("id") int id);

}
