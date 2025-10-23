package org.eee.account.param;

import lombok.Data;

@Data
public class UserRegisterParam
{
    private String username;
    private String password;
    private String email;
    private String inviteCode;
    private String emailCode;
    private String nickname;
    private String githubInfo;
    private String googleInfo;
}
