package org.eee.account.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.eee.account.entity.UserPrincipal;
import org.eee.account.param.Response;
import org.eee.account.param.UserAvatarParam;
import org.eee.account.param.UserInfoParam;
import org.eee.account.service.AvatarService;
import org.eee.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RestController
@RequestMapping(("/user"))
@Slf4j
public class UserController
{
    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/me")
    public Response<UserInfoParam> me()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
        {
            // 通常token会存储在Principal对象中，具体取决于你的实现
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            return userService.loadUserById(principal);
        }
        return Response.error(401, "token信息异常！");
    }

    @PostMapping("/update")
    public Response<String> update(@RequestBody UserInfoParam param)
    {
        return userService.update(param);
    }

    @PostMapping("/avatar")
    public Response<String> updateAvatar(@RequestBody UserAvatarParam param)
    {
//        具有示范意义的代码，保留一下
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated())
//        {
//            // 通常token会存储在Principal对象中，具体取决于你的实现
//            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
//            return avatarService.getAvatarUrl(principal);
//        }
        com.atguigu.param.Response<URL> avatarUrl = avatarService.getAvatarUrl(param);
        if (avatarUrl.getCode() == 200)
        {
            return Response.success(avatarUrl.getData().toString());
        }
        return Response.error(avatarUrl.getCode(), avatarUrl.getMessage());
    }
}
