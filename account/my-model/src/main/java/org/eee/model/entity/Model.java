package org.eee.model.entity;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class Model
{
    @PrimaryKey
    @AutoIncrement
    @Column(type = "BIGINT")
    private long id;
    @Column(type = "VARCHAR(250)")
    private String name;
    @Column(type = "VARCHAR(250)")
    private String description;
    @Column(type = "INT")
    private int level;

    @Column(type = "VARCHAR(500)")
    private String apiKey;
    @Column(type = "VARCHAR(100)")
    private String baseUrl;

}
