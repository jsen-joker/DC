package com.dryork.controller.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dryork.constants.ConstantResponse;
import com.dryork.service.*;
import com.dryork.utils.ResponseBase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/8
 */
@RestController
@CrossOrigin
@RequestMapping("/ACRUD")
public class AuthzCRUD {

    @Autowired
    SysFilterChainService sysFilterChainService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysPermissionService sysPermissionService;
    @Autowired
    SysUserRoleService sysUserRoleService;
    @Autowired
    SysRolePermissionService sysRolePermissionService;

    @GetMapping("/create")
    public ResponseBase createFilter(@RequestParam("url") String url, @RequestParam("filters") String filters, @RequestParam("sort") int sort) {
        return sysFilterChainService.createFilter(url, filters, sort);
    }
    @GetMapping("/delete")
    public ResponseBase removeFilterByUrl(@RequestParam("url") String url) {
        System.err.println("DDDD");
        return sysFilterChainService.deleteByUrl(url);
    }
    @GetMapping("/filter/list")
    public ResponseBase listAllFilterChain() {
        return sysFilterChainService.lists();
    }

    // user
    @GetMapping("/create/user/{username}")
    public ResponseBase createUser(@PathVariable("username") String username) {
        return sysUserService.createUser(username);
    }
    @GetMapping("/delete/user/{username}")
    public ResponseBase deleteUser(@PathVariable("username") String username) {
        return sysUserService.deleteUser(username);
    }

    // role
    @GetMapping("/create/role/{role}")
    public ResponseBase createRole(@PathVariable("role") String role) {
        return sysRoleService.createRole(role);
    }
    @GetMapping("/delete/role/{role}")
    public ResponseBase deleteRole(@PathVariable("role") String role) {
        return sysRoleService.deleteRole(role);
    }

    // permission
    @GetMapping("/create/permission/{permission}")
    public ResponseBase createPermission(@PathVariable("permission") String permission) {
        return sysPermissionService.createPermission(permission);
    }
    @GetMapping("/delete/permission/{permission}")
    public ResponseBase deletePermission(@PathVariable("permission") String permission) {
        return sysPermissionService.deletePermission(permission);
    }

    // user add role
    @GetMapping("/create/uar/{uid}/{rid}")
    public ResponseBase addRoleToUser(@PathVariable("uid") int uid, @PathVariable("rid") int rid) {
        return sysUserRoleService.createUserRole(uid, rid);
    }
    @GetMapping("/create/uars/{uid}/{rids}")
    public ResponseBase addRolesToUser(@PathVariable("uid") int uid, @PathVariable("rids") String rids) {
        try {
            JSONArray array = JSON.parseArray(rids);
            return sysUserRoleService.createUserRoles(uid, array);
        } catch (Exception e) {
            try {
                JSONArray array = JSON.parseArray(URLDecoder.decode(rids, "utf-8"));
                return sysUserRoleService.createUserRoles(uid, array);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                return ConstantResponse.FAIL.msg("JSON格式转换出错");
            }
        }
    }
    @GetMapping("/delete/uar/{user_id}/{role_id}")
    public ResponseBase deleteRoleToUser(@PathVariable("user_id") int user_id, @PathVariable("role_id") int role_id) {
        return sysUserRoleService.deleteUserRole(user_id, role_id);
    }

    // role add permission
    @GetMapping("/create/rap/{rid}/{pid}")
    public ResponseBase addPermissionToRole(@PathVariable("rid") int rid, @PathVariable("pid") int pid) {
        return sysRolePermissionService.createRolePermission(rid, pid);
    }
    @GetMapping("/create/raps/{rid}/{pids}")
    public ResponseBase addPermissionsToRole(@PathVariable("rid") int rid, @PathVariable("pids") String pids) {
        try {
            JSONArray array = JSON.parseArray(pids);
            return sysRolePermissionService.createRolePermissions(rid, array);
        } catch (Exception e) {
            try {
                JSONArray array = JSON.parseArray(URLDecoder.decode(pids, "utf-8"));
                return sysRolePermissionService.createRolePermissions(rid, array);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                return ConstantResponse.FAIL.msg("JSON格式转换出错");
            }
        }
    }
    @GetMapping("/delete/rap/{role_id}/{permission_id}")
    public ResponseBase deletePermissionToRole(@PathVariable("role_id") int role_id, @PathVariable("permission_id") int permission_id) {
        return sysRolePermissionService.deleteRolePermission(role_id, permission_id);
    }


    /**
     *
     * @param page
     * @param capacity
     * @return
     */
    @GetMapping("/user/list/{page}/{capacity}")
    public ResponseBase listUsers(@PathVariable("page") int page, @PathVariable("capacity") int capacity) {
        return sysUserService.listUser(page, capacity);
    }

    @GetMapping("/role/list/{page}/{capacity}")
    public ResponseBase listRoles(@PathVariable("page") int page, @PathVariable("capacity") int capacity) {
        return sysRoleService.listRole(page, capacity);
    }
    @GetMapping("/permission/list/{page}/{capacity}")
    public ResponseBase listPermission(@PathVariable("page") int page, @PathVariable("capacity") int capacity) {
        return sysPermissionService.listPermission(page, capacity);
    }

    @GetMapping("/role/listAll")
    public ResponseBase listAllRole() {
        return sysRoleService.listAll();
    }
    @GetMapping("/permission/listAll")
    public ResponseBase listAllPermission() {
        return sysPermissionService.listAll();
    }



}
