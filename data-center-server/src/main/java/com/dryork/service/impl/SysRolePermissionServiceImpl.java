package com.dryork.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dryork.entity.SysPermission;
import com.dryork.entity.SysRolePermission;
import com.dryork.mapper.SysPermissionMapper;
import com.dryork.mapper.SysRolePermissionMapper;
import com.dryork.service.SysRolePermissionService;
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
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Autowired
    SysPermissionMapper sysPermissionMapper;

    @Override
    public ResponseBase createRolePermission(int r_id, int p_id) {


        if (baseMapper.getPermissionByRoleIdAndPermissionId(r_id, p_id) != null) {
            return ResponseBase.create().code(1).msg("该角色已拥有该权限");
        }

        int eff = baseMapper.insertRolePermission(new SysRolePermission().setRoleId(r_id).setPermissionId(p_id));
        SysPermission sysPermission = sysPermissionMapper.getPermissionById(p_id);
        return ResponseBase.create().code(0).add("eff", eff).add("data", sysPermission);
    }

    @Override
    public ResponseBase createRolePermissions(int r_id, JSONArray p_idList) {
        int eff = 0;
        JSONArray array = new JSONArray();
        for(int i = 0; i < p_idList.size(); i++) {
            int p_id = p_idList.getInteger(i);
            if (baseMapper.getPermissionByRoleIdAndPermissionId(r_id, p_id) == null) {
                if (baseMapper.insertRolePermission(new SysRolePermission().setRoleId(r_id).setPermissionId(p_id)) == 1) {
                    eff ++;
                    SysPermission sysPermission = sysPermissionMapper.getPermissionById(p_id);
                    array.add(sysPermission);
                }
            }
        }
        return ResponseBase.create().code(0).add("eff", eff).add("data", array);
    }

    @Override
    public ResponseBase deleteRolePermission(int roleId, int permissionId) {
        return ResponseBase.create().code(0).add("eff", baseMapper.deleteRolePermissionByRoleIdAndPermissionId(roleId, permissionId));
    }
}
