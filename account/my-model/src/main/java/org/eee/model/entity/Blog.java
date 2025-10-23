package org.eee.model.entity;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class Blog extends DomainBase
{
    @Column(type="VARCHAR(100)")
    private String nameInOss;

    @Column(type="VARCHAR(50)")
    private String title;

    @Column(type="VARCHAR(300)")
    private String imgUrl;

    @Column(type="INT")
    private int type;

    @Column(type="VARCHAR(500)")
    private String tags;
}
