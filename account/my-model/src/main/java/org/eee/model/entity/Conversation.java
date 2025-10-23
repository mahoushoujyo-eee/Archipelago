package org.eee.model.entity;


import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class Conversation extends DomainBase
{
    @Column(type = "VARCHAR(255)")
    private String conversationId;

    @Column(type = "BIGINT")
    private long userId;

    @Column(type = "VARCHAR(255)")
    private String title;
}
