package org.eee.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Table(name = "user", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class User extends DomainBase
{
    @Column(type = "VARCHAR(30)", unique = true)
    private String username;

    @Column(type = "VARCHAR(30)", unique = true)
    private String email;

    @Column(type = "VARCHAR(100)")
    private String encryptedPassword;

    @Column(type = "INT", defaultValue = "0")
    private Integer state;

    @Column(type = "VARCHAR(30)")
    private String nickname;

    @Column(type = "VARCHAR(100)")
    private String githubInfo;

    @Column(type = "VARCHAR(100)")
    private String googleInfo;
}
