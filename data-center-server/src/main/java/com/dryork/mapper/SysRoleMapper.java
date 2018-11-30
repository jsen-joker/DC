package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dryork.entity.SysRole;
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
public interface SysRoleMapper extends BaseMapper<SysRole> {
    int insertRole(SysRole sysRole);

    int deleteRole(@Param("ve") String value);

    List<SysRole> listPage(Page pagination);

    int countRole();

    SysRole getRoleByValue(@Param("ve") String value);

    SysRole getRoleById(@Param("id") int id);

    List<SysRole> listAll();

}
