package com.dryork.controller.shiro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.dryork.utils.ResponseBase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/16
 */
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class ShiroCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroCommon.class);

    private static final String[] AS = new String[] {};
    private static final ResponseBase OK = ResponseBase.create().code(0);
    private static final ResponseBase NO_OK= ResponseBase.create().code(1);
    @GetMapping("/has/role/{role}")
    public ResponseBase hasRole(@PathVariable("role") String role) {
        LOGGER.info(role);
        Subject subject = SecurityUtils.getSubject();
        return subject.hasRole(role) ? OK : NO_OK;
    }
    @GetMapping("/has/permission/{permission}")
    public ResponseBase hasPermission(@PathVariable("permission") String permission) {
        LOGGER.info(permission);
        Subject subject = SecurityUtils.getSubject();
        return subject.isPermitted(permission) ? OK : NO_OK;
    }
    @GetMapping("/has/permissions/{permissions}")
    public ResponseBase hasPermissions(@PathVariable("permissions") String permissions) {
        LOGGER.info(permissions);
        try {
            JSONArray jsonArray = JSON.parseArray(permissions);
            Subject subject = SecurityUtils.getSubject();
            return subject.isPermittedAll(jsonArray.toArray(AS)) ? OK : NO_OK;
        } catch (Exception e) {
//            e.printStackTrace();
            try {
                permissions = URLDecoder.decode(permissions, "utf-8");
                LOGGER.info(permissions);
                JSONArray jsonArray = JSON.parseArray(permissions);
                Subject subject = SecurityUtils.getSubject();
                return subject.isPermittedAll(jsonArray.toArray(AS)) ? OK : NO_OK;
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            return NO_OK;
        }
    }
}
