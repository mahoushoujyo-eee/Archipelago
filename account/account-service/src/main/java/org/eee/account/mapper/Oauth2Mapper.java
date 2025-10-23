package org.eee.account.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.eee.model.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Mapper
public interface Oauth2Mapper
{
    User getUserByGithub(String githubId);

    void modifyUserGithubInfo(String email, String githubId);

    void insertNewGithubUser(User user);
}
