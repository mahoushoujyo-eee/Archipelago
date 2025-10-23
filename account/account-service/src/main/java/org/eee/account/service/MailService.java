package org.eee.account.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.eee.account.param.Response;
import org.eee.account.util.MailUtil;
import org.eee.account.util.RandomUtil;
import org.eee.account.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@DubboService
public class MailService
{
    @Autowired
    private RedisUtil redis;

    @Autowired
    private MailUtil mailUtil;

    public Response<String> sendMail(String email)
    {
        String code = RandomUtil.generateSixDigitCode();
        mailUtil.sendSimpleEmail(email, "SpringBlog验证码", "您的验证码是：" + code);
        redis.set(email, code);

        return Response.success(code, "发送验证码成功");
    }

    public boolean validateMail(String email, String code)
    {
        return redis.get(email).equals(code);
    }
}
