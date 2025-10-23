package org.eee.model.schema;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import java.util.List;

public class SchemaMigration20250519152226 extends SchemaMigrationBase {
    @Override
    public void forward() {
        forwardBuilder.createTable("like", builder -> {
            builder.column().name("blog_id").type("BIGINT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("comment", builder -> {
            builder.column().name("blog_id").type("BIGINT").nullable(true).unique(false);
            builder.column().name("content").type("VARCHAR(250)").nullable(true).unique(false);
            builder.column().name("state").type("INT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creator_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creation_time").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifier_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modification_time").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.dropTable("Comment");
        forwardBuilder.dropTable("Like");
    }
    @Override
    public void backward() {
        backwardBuilder.createTable("Comment", builder -> {
            builder.column().name("blogId").type("BIGINT").nullable(true).unique(false);
            builder.column().name("content").type("VARCHAR(250)").nullable(true).unique(false);
            builder.column().name("state").type("INT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creatorId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creationTime").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifierId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modificationTime").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        backwardBuilder.createTable("Like", builder -> {
            builder.column().name("blogId").type("BIGINT").nullable(true).unique(false);
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("creatorId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("creationTime").type("DATETIME").nullable(true).unique(false).defaultValue("NOW()");
            builder.column().name("modifierId").type("BIGINT").nullable(false).unique(false);
            builder.column().name("modificationTime").type("DATETIME").nullable(true).unique(false)
                    .defaultValue("NOW()").onUpdate("NOW()");
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        backwardBuilder.dropTable("like");
        backwardBuilder.dropTable("comment");
    }
}