package org.eee.model.entity;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class Comment extends DomainBase
{
    @Column(type = "BIGINT")
    private Long blogId;

    @Column(type = "VARCHAR(250)")
    private String content;

    @Column(type = "INT", defaultValue = "0")
    private Integer type;
}
