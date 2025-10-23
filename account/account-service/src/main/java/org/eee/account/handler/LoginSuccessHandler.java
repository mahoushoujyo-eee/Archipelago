package org.eee.account.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eee.account.entity.UserPrincipal;
import org.eee.account.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoginSuccessHandler  implements AuthenticationSuccessHandler
{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        response.setContentType("application/json;charset=UTF-8");
        Object principal = authentication.getPrincipal();
        String token = JwtUtil.generateToken((UserPrincipal) principal);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("data", token);
        responseMap.put("message", "登录成功");
        responseMap.put("code", "200");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseMap));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
