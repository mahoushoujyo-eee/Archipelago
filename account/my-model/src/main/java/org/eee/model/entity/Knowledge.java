package org.eee.model.entity;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Data
@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class Knowledge extends DomainBase
{
    @Column(type = "VARCHAR(100)")
    private String title;

    @Column(type = "VARCHAR(500)")
    private String collectionName;

    @Column(type = "VARCHAR(500)")
    private String description;
}
