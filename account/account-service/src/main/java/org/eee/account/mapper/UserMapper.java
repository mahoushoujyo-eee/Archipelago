package org.eee.account.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.eee.account.entity.UserPrincipal;
import org.eee.model.entity.User;

@Mapper
public interface UserMapper
{
    UserPrincipal getUserByUsername(String username);

    void updatePassword(String username, String newPassword);

    void updatePasswordByEmail(String email, String newPassword);

    void insertUser(User user);

    String getMaxUserName();

    UserPrincipal getUserByEmail(String email);

    boolean ifEmailExists(String email);

    User getUserByUserId(Long userId);

    void updateNickname(Long userId, String nickname);
}
