package org.eee.account.service;

import org.eee.account.mapper.Oauth2Mapper;
import org.eee.account.mapper.UserMapper;
import org.eee.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OauthService
{
    @Autowired
    Oauth2Mapper oauth2Mapper;

    @Autowired
    UserMapper userMapper;

    public User getGithubUser(String githubId)
    {
        return oauth2Mapper.getUserByGithub(githubId);
    }

    public void modifyGithubUser(String email, String githubId)
    {
        oauth2Mapper.modifyUserGithubInfo(email, githubId);
    }

    public void insertNewGithubUser(User user)
    {
        String username = userMapper.getMaxUserName();
        user.setUsername(username);
        oauth2Mapper.insertNewGithubUser(user);
    }
}
