package org.eee.account.param;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
public class UserInfoParam
{
    private Long id;
    private String username;
    private String name;
    private String email;
    private List<String> authorities;
}
