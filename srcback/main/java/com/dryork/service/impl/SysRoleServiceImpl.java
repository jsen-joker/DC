package com.dryork.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dryork.entity.SysRole;
import com.dryork.mapper.SysRoleMapper;
import com.dryork.mapper.SysRolePermissionMapper;
import com.dryork.mapper.SysUserRoleMapper;
import com.dryork.service.SysRoleService;
import com.dryork.utils.ResponseBase;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public ResponseBase createRole(String value) {

        SysRole sysRole = baseMapper.getRoleByValue(value);
        if (sysRole != null) {
            return ResponseBase.create().code(1).msg("角色存在");
        }
        sysRole = new SysRole().setValue(value).setState(1);
        int eff = baseMapper.insertRole(sysRole);
        sysRole = baseMapper.getRoleByValue(value);
        return ResponseBase.create().code(0).add("eff", eff).add("data", sysRole);
    }

    @Override
    public ResponseBase deleteRole(String value) {

        SysRole sysRole = baseMapper.getRoleByValue(value);
        if (sysRole != null) {
            int uEff = sysUserRoleMapper.deleteUserRoleByRId(sysRole.getId());
            int pEff = sysRolePermissionMapper.deleteRolePermissionByRId(sysRole.getId());
            return ResponseBase.create().code(0).add("eff", baseMapper.deleteRole(value)).add("user_role_eff", uEff).add("role_permission_eff", pEff);
        } else {
            return ResponseBase.create().code(1).msg("role not exist");
        }
    }

    @Override
    public ResponseBase listRole(int page, int capacity) {
        return ResponseBase.create().code(0).add("data", baseMapper.listPage(new Page<>(page, capacity))).add("total", count());
    }

    @Override
    public ResponseBase listAll() {
        return ResponseBase.create().code(0).data(baseMapper.listAll());
    }

    private int count() {
        return baseMapper.countRole();
    }
}
