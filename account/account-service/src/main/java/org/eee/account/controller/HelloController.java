package org.eee.account.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController
{
    @GetMapping("/user/hello")
    public String helloUser()
    {
        return "hello";
    }

    @GetMapping("/public/hello")
    public String helloPublic()
    {
        log.info("helloPublic");
        return "hello";
    }

    @GetMapping("/admin/hello")
    public String helloAdmin()
    {
        return "hello";
    }
}
