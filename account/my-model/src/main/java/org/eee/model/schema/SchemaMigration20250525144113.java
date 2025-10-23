package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250525144113 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.alterColumn("role", "creator_id",
                ColumnMetadata.builder().name("creator_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("role", "modifier_id",
                ColumnMetadata.builder().name("modifier_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("like", "creator_id",
                ColumnMetadata.builder().name("creator_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("like", "modifier_id",
                ColumnMetadata.builder().name("modifier_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("comment", "creator_id",
                ColumnMetadata.builder().name("creator_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("comment", "modifier_id",
                ColumnMetadata.builder().name("modifier_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("blog", "creator_id",
                ColumnMetadata.builder().name("creator_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("blog", "modifier_id",
                ColumnMetadata.builder().name("modifier_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("user", "creator_id",
                ColumnMetadata.builder().name("creator_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("user", "modifier_id",
                ColumnMetadata.builder().name("modifier_id").type("BIGINT").nullable(false).unique(false).build());
        forwardBuilder.alterColumn("user", "state", ColumnMetadata.builder().name("state").type("INT").nullable(true)
                .unique(false).defaultValue("0").build());
    }
    @Override
    public void backward() {
        backwardBuilder.alterColumn("role", "creator_id", ColumnMetadata.builder().name("creator_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("role", "modifier_id", ColumnMetadata.builder().name("modifier_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("like", "creator_id", ColumnMetadata.builder().name("creator_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("like", "modifier_id", ColumnMetadata.builder().name("modifier_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("comment", "creator_id", ColumnMetadata.builder().name("creator_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("comment", "modifier_id", ColumnMetadata.builder().name("modifier_id")
                .type("BIGINT").nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("blog", "creator_id", ColumnMetadata.builder().name("creator_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("blog", "modifier_id", ColumnMetadata.builder().name("modifier_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("user", "creator_id", ColumnMetadata.builder().name("creator_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("user", "modifier_id", ColumnMetadata.builder().name("modifier_id").type("BIGINT")
                .nullable(false).unique(false).defaultValue("1").build());
        backwardBuilder.alterColumn("user", "state",
                ColumnMetadata.builder().name("state").type("INT").nullable(true).unique(false).build());
    }
}