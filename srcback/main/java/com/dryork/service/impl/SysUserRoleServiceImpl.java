package com.dryork.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dryork.entity.SysRole;
import com.dryork.entity.SysUserRole;
import com.dryork.mapper.SysRoleMapper;
import com.dryork.mapper.SysUserRoleMapper;
import com.dryork.service.SysUserRoleService;
import com.dryork.utils.ResponseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Autowired
    SysRoleMapper sysRoleMapper;
    @Override
    public ResponseBase createUserRole(int u_id, int r_id) {
        if (baseMapper.getRoleByUIdAndRId(u_id, r_id) != null) {
            return ResponseBase.create().code(1).msg("该角色已拥有");
        }

        int eff = baseMapper.insertUserRole(new SysUserRole().setRoleId(r_id).setUserId(u_id));
        SysRole sysRole = sysRoleMapper.getRoleById(r_id);
        return ResponseBase.create().code(0).add("eff", eff).add("data", sysRole);
    }

    @Override
    public ResponseBase createUserRoles(int u_id, JSONArray r_ids) {
        int eff = 0;
        JSONArray array = new JSONArray();
        for(int i = 0; i < r_ids.size(); i++) {
            int r_id = r_ids.getInteger(i);
            if (baseMapper.getRoleByUIdAndRId(u_id, r_id) == null) {
                if (baseMapper.insertUserRole(new SysUserRole().setRoleId(r_id).setUserId(u_id)) == 1) {
                    eff ++;
                    SysRole sysRole = sysRoleMapper.getRoleById(r_id);
                    array.add(sysRole);
                }
            }
        }
        return ResponseBase.create().code(0).add("eff", eff).add("data", array);
    }

    @Override
    public ResponseBase deleteUserRole(int user_id, int role_id) {
        return ResponseBase.create().code(0).add("eff", baseMapper.deleteUserRoleByUserIdAndRoleId(user_id, role_id));
    }
}
