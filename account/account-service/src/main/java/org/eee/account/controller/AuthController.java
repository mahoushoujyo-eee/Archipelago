package org.eee.account.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.eee.account.param.Response;
import org.eee.account.param.UserRegisterParam;
import org.eee.account.param.UserResetParam;
import org.eee.account.service.UserService;
import org.eee.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController
{
    @Autowired
    private UserService  userService;

    @PostMapping("/register")
    public Response<Long> register(@RequestBody UserRegisterParam user, HttpServletResponse response)
    {
        log.info("Registering user: {}", user);
        response.setStatus(HttpStatus.OK.value());
        return userService.register(user);
    }

    @PostMapping("/reset")
    public Response<String> reset(@RequestBody UserResetParam user, HttpServletResponse response)
    {
        log.info("Resetting user: {}", user);
        response.setStatus(HttpStatus.OK.value());
        return userService.resetPassword(user);
    }

}
