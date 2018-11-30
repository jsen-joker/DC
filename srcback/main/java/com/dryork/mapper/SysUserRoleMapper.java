package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dryork.entity.SysRole;
import com.dryork.entity.SysUserRole;
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
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    int insertUserRole(SysUserRole sysUserRole);

    int deleteUserRoleById(@Param("id") int id);
    int deleteUserRoleByUserIdAndRoleId(@Param("uId") int user_id, @Param("rId") int role_id);

    int deleteUserRoleByUId(@Param("id") int id);
    int deleteUserRoleByRId(@Param("id") int id);

    List<SysRole> listRolesByUId(@Param("id") int id);

    SysUserRole getRoleByUIdAndRId(@Param("uId") int user_id, @Param("rId") int role_id);
}
