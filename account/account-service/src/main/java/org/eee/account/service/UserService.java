package org.eee.account.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.eee.account.constant.RoleConstant;
import org.eee.account.entity.UserPrincipal;
import org.eee.account.mapper.RoleMapper;
import org.eee.account.mapper.UserMapper;
import org.eee.account.param.Response;
import org.eee.account.param.UserInfoParam;
import org.eee.account.param.UserRegisterParam;
import org.eee.account.param.UserResetParam;
import org.eee.model.entity.Role;
import org.eee.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@LogArgumentsAndResponse
public class UserService
{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper  roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserPrincipalService userPrincipalService;

    @Transactional(rollbackFor = Exception.class)
    public Response<Long> register(UserRegisterParam userParam)
    {
        if(userMapper.ifEmailExists(userParam.getEmail()))
            return Response.error(300, "邮箱已存在！");

        if(!mailService.validateMail(userParam.getEmail(), userParam.getEmailCode()))
            return Response.error(300, "邮箱验证码错误！");

        User user = new User();
        String username = userMapper.getMaxUserName();
        Long newUserName = Long.parseLong(username) + 1L;
        user.setUsername(String.valueOf(newUserName));
        user.setEmail(userParam.getEmail());
        user.setNickname(userParam.getNickname());
        user.setGithubInfo(userParam.getGithubInfo());
        user.setGoogleInfo(userParam.getGoogleInfo());
        user.setEncryptedPassword(passwordEncoder.encode(userParam.getPassword()));
        log.info("register user:{}", JsonSerializer.serialize(user));
        userMapper.insertUser(user);

        if(userParam.getInviteCode() != null && !userParam.getInviteCode().isEmpty())
        {
            roleMapper.registerRole(new Role("ROLE_ADMIN", user.getId(), RoleConstant.ROLE_ADMIN));
        }

        roleMapper.registerRole(new Role("ROLE_USER", user.getId(), RoleConstant.ROLE_USER));
        return Response.success(newUserName, "注册成功！您的uid为"+newUserName);
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<String> resetPassword(UserResetParam userParam)
    {
        if(!userMapper.ifEmailExists(userParam.getEmail()))
            return Response.error(300, "邮箱不存在！");

        if(!mailService.validateMail(userParam.getEmail(), userParam.getEmailCode()))
            return Response.error(300, "邮箱验证码错误！");

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setUsername(userParam.getEmail());
        userPrincipal.setPassword(userParam.getNewPassword());

        userPrincipalService.updatePassword(userPrincipal, userParam.getNewPassword());

        return Response.success("重置密码成功！");
    }

    public Response<UserInfoParam> loadUserById(UserPrincipal userPrincipal)
    {
        User user = userMapper.getUserByUserId(userPrincipal.getId());
        UserInfoParam userInfoParam = new UserInfoParam();
        userInfoParam.setId(user.getId());
        userInfoParam.setUsername(user.getUsername());
        userInfoParam.setName(user.getNickname());
        userInfoParam.setEmail(user.getEmail());
        List<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        userInfoParam.setAuthorities(authorities);

        if(user != null)
            return Response.success(userInfoParam);
        return Response.error(300, "用户不存在！");
    }

    public Response<String> update(UserInfoParam param)
    {
        log.info("更新用户名{}", param);
        if(param.getId() == null)
            return Response.error(300, "用户不存在！");
        if(param.getName() == null)
            return Response.error(300, "用户名不能为空！");

        userMapper.updateNickname(param.getId(), param.getName());
        return Response.success("更新成功！");
    }
}
