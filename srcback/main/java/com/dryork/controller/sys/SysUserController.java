package com.dryork.controller.sys;

import com.dryork.service.SysUserService;
import com.dryork.utils.ResponseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/10
 */
@RestController
@CrossOrigin
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    SysUserService sysUserService;

    @GetMapping("/login/{username}/{password}")
    public ResponseBase login(@PathVariable("username") String username, @PathVariable("password") String password) {
        return  sysUserService.login(username, password);
    }
}
