package org.eee.account.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.eee.account.param.Response;
import org.eee.account.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth/mail")
public class MailController
{
    @Autowired
    MailService mailService;

    @GetMapping("/send/{email}")
    public Response<String> sendMail(@PathVariable("email")String email, HttpServletResponse response)
    {
        log.info("发送邮件请求：{}", email);
        response.setStatus(HttpStatus.OK.value());
        return mailService.sendMail(email);
    }
}
