package org.eee.account.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eee.account.entity.UserPrincipal;
import org.eee.account.service.OauthService;
import org.eee.account.service.UserPrincipalService;
import org.eee.account.util.JwtUtil;
import org.eee.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OauthSuccessHandler implements AuthenticationSuccessHandler
{

    @Autowired
    private OauthService oauthService;

    @Autowired
    private UserPrincipalService userPrincipalService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException
    {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User: {}", oAuth2User);

        User user = oauthService.getGithubUser(oAuth2User.getAttribute("id").toString());

        UserPrincipal userPrincipal;
        if(user == null)
        {
            userPrincipal = (UserPrincipal)userPrincipalService.loadUserByUsername(oAuth2User.getAttribute("email"));
            if(userPrincipal == null)
            {
                User newUser = new User();
                newUser.setEmail(oAuth2User.getAttribute("email"));
                newUser.setNickname(oAuth2User.getAttribute("login"));
                newUser.setGithubInfo(oAuth2User.getAttribute("id").toString());
                newUser.setEncryptedPassword(passwordEncoder.encode("yuzaoqian123"));
                oauthService.insertNewGithubUser(newUser);
            }
            else
            {
                oauthService.modifyGithubUser(oAuth2User.getAttribute("email"), oAuth2User.getAttribute("id").toString());
            }
        }

        userPrincipal = (UserPrincipal)userPrincipalService.loadUserByUsername(oAuth2User.getAttribute("email"));

        log.info("UserPrincipal: {}", userPrincipal);
        String token = JwtUtil.generateToken(userPrincipal);

        String redirectUrl = "http://localhost:5000/oauth2?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}
