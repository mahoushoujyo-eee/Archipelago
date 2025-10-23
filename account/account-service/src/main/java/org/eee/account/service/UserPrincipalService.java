package org.eee.account.service;

import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.eee.account.entity.UserPrincipal;
import org.eee.account.mapper.RoleMapper;
import org.eee.account.mapper.UserMapper;
import org.eee.model.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserPrincipalService implements UserDetailsService, UserDetailsPasswordService
{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email)
    {
        UserPrincipal user = userMapper.getUserByEmail(email);
        if(user == null)
            throw new UsernameNotFoundException("用户名不存在");

        List<Role> roles  =  roleMapper.getRoleByUserId(user.getId());
        log.info("load user roles {}", JsonSerializer.serialize(roles));
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());
        user.setAuthorities(authorities);

        log.info("load user {}", JsonSerializer.serialize(user));
        return user;
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        UserPrincipal userPrincipal = (UserPrincipal) user;
        userPrincipal.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updatePasswordByEmail(userPrincipal.getUsername(), newPassword);

        userPrincipal.setPassword(newPassword);
        return user;
    }
}
