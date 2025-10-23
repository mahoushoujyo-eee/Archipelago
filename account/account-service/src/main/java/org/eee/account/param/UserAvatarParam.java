package org.eee.account.param;

import lombok.Data;

@Data
public class UserAvatarParam
{
    private Long userId;
    private String username;
    private String avatar;
}
