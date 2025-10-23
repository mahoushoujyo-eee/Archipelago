package org.eee.account.param;

import lombok.Data;

@Data
public class UserResetParam
{
    private String email;
    private String emailCode;
    private String newPassword;
}
