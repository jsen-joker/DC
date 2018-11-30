package com.dryork.config.shiro;

import com.dryork.config.shiro.exception.TokenException;
import com.dryork.constants.JWTConstants;
import com.dryork.entity.SysPermission;
import com.dryork.entity.SysRole;
import com.dryork.entity.SysUser;
import com.dryork.mapper.SysUserMapper;
import com.dryork.service.logical.TokenService;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ShiroRealm extends AuthorizingRealm {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroRealm.class);

    @Autowired
    TokenService tokenService;
    @Autowired
    SysUserMapper sysUserMapper;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SysUser principal = (SysUser) principalCollection.getPrimaryPrincipal();
        int id = principal.getId();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Set<String> permissions = Sets.newHashSet();
        for (SysRole role:sysUserMapper.getRoleByUserId(id)) {
            simpleAuthorizationInfo.addRole(role.getValue());
            for (SysPermission permission:sysUserMapper.getPermissionByRoleId(role.getId())) {
                permissions.add(permission.getPermission());
                LOGGER.info(permission.getPermission());
            }
        }
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {
        String token = (String) authenticationToken.getCredentials();
        try {
            if (token == null) {
                // throw new AuthenticationException("token为空");
                return null;
            }
            int id = tokenService.getUserId(token);

            SysUser sysUser = sysUserMapper.getUserById(id);

            if (sysUser == null) {
                throw new TokenException("用户不存在");
            }

            tokenService.validToken(token, sysUser.getPassword(), JWTConstants.shortExp);
            return new SimpleAuthenticationInfo(sysUser, token, "my_realm");
        } catch (Exception e) {
            Throwables.throwIfUnchecked(e);
            throw new AuthenticationException();
        }

    }
}
